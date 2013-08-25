package controllers

import play.api.mvc.{AnyContent, Action, Controller, Request}
import java.io.{PrintStream, ByteArrayOutputStream, File}
import play.api.Logger
import de.leanovate.jbj.core.runtime.context.Context
import de.leanovate.jbj.core.runtime.env.CgiEnvironment
import de.leanovate.jbj.core.ast.{NodePosition, NoNodePosition}
import de.leanovate.jbj.core.runtime.value.{PVar, StringVal, ArrayVal}
import java.net.URLDecoder
import de.leanovate.jbj.core.JbjEnv
import de.leanovate.jbj.core.runtime.exception.NotFoundJbjException

object JbjPhp extends Controller {

  private val jbj = JbjEnv(PlayLocator, errorStream = Some(System.err))

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
        val bOut = new ByteArrayOutputStream()
        val out = new PrintStream(bOut, false, "UTF-8")

        jbj.run(resourceName, RequestInfoAdapter(request), out)

        bOut.flush()
        bOut.close()
        Ok(bOut.toByteArray).as("text/html")
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
