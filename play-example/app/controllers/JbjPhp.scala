package controllers

import play.api.mvc.{AnyContent, Action, Controller, Request}
import java.io.{PrintStream, ByteArrayOutputStream, File}
import play.api.Logger
import de.leanovate.jbj.core.JbjEnvironmentBuilder
import de.leanovate.jbj.runtime.exception.NotFoundJbjException

object JbjPhp extends Controller {

  private val jbj = JbjEnvironmentBuilder().withScriptLocator(PlayJbjScriptLocator).withErrStream(System.err).build()

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
      try {
        val responseAdapter = BufferedResponseAdapter()

        jbj.run(resourceName, RequestInfoAdapter(request), responseAdapter)

        responseAdapter.toResult
      } catch {
        case e: NotFoundJbjException =>
          NotFound
        case e: Throwable =>
          Logger.error("Error", e)
          InternalServerError
      }
    }
  }
}
