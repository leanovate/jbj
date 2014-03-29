package controllers

import play.api.mvc.{AnyContent, Action, Controller, Request}
import java.io.{PrintStream, ByteArrayOutputStream, File}
import play.api.Logger
import de.leanovate.jbj.core.JbjEnvironmentBuilder
import de.leanovate.jbj.runtime.exception.{FatalErrorJbjException, NotFoundJbjException}
import de.leanovate.jbj.api.http.{JbjException, JbjSettings}
import de.leanovate.jbj.bcmath.BcMathExtension
import de.leanovate.jbj.pcre.PcreExtension

object JbjPhp extends Controller {
  private val jbjSettings = new JbjSettings
  jbjSettings.setShortOpenTag(true)
  jbjSettings.setAspTags(true)
  private val jbj = JbjEnvironmentBuilder()
    .withExtension(BcMathExtension)
    .withExtension(PcreExtension)
    .withScriptLocator(PlayJbjScriptLocator)
    .withErrStream(System.err)
    .withSettings(jbjSettings)
    .build()

  def get(path: String, file: String, pathInfo: String) = Action {
    request =>
      runPhp(path, file, request)
  }

  def post(path: String, file: String, pathInfo: String) = Action {
    request =>
      runPhp(path, file, request)
  }

  private def runPhp(path: String, file: String, request: Request[AnyContent]) = {
    val resourceName = Option(path + "/" + file).map(name => if (name.startsWith("/")) name else ("/" + name)).get

    if (!new File(resourceName).getCanonicalPath.startsWith(new File(path).getCanonicalPath)) {
      NotFound
    } else {
      val responseAdapter = BufferedResponseAdapter()
      try {

        jbj.run(resourceName, RequestInfoAdapter(request), responseAdapter)

        responseAdapter.toResult
      } catch {
        case e: NotFoundJbjException =>
          NotFound
        case e: JbjException =>
          responseAdapter.setStatus(INTERNAL_SERVER_ERROR, "internal server error")
          Logger.error(s"jbj error at ${e.getPosition}", e)
          responseAdapter.toResult
        case e: Throwable =>
          responseAdapter.setStatus(INTERNAL_SERVER_ERROR, "internal server error")
          Logger.error(s"Filed to read $resourceName", e)
          responseAdapter.toResult
      }
    }
  }
}
