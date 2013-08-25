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

object JbjPhp extends Controller {

  private val jbj = JbjEnv(PlayLocator, errorStream =  Some(System.err))

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
      jbj.parse(resourceName) match {
        case Some(Left(prog)) =>
          val bOut = new ByteArrayOutputStream()
          val out = new PrintStream(bOut, false, "UTF-8")
          implicit val ctx = jbj.newGlobalContext(out)
          implicit val position = NoNodePosition

          handleCommonParameters(request)
          request.method match {
            case "GET" =>
              val requestArray = CgiEnvironment.decodeKeyValues(request.queryString.toSeq.flatMap {
                case (key, values) => values.map(key -> _)
              })
              ctx.defineVariable("_REQUEST", PVar(requestArray))
              ctx.defineVariable("_GET", PVar(requestArray.copy))
            case "POST" =>
              val requestArray = request.body.asFormUrlEncoded.map {
                keyValues =>
                  CgiEnvironment.decodeKeyValues(keyValues.toSeq.flatMap {
                    case (key, values) => values.map(key -> _)
                  })
              }.getOrElse(ArrayVal())
              ctx.defineVariable("_REQUEST", PVar(requestArray))
              ctx.defineVariable("_POST", PVar(requestArray.copy))
          }

          prog.exec

          bOut.flush()
          bOut.close()
          Ok(bOut.toByteArray).as("text/html")
        case Some(Right(t)) =>
          Logger.error("Error", t)
          InternalServerError
        case None =>
          NotFound
      }
    }
  }

  private def handleCommonParameters(request: Request[AnyContent])(implicit ctx: Context, position: NodePosition) {
    val serverArgv = ArrayVal(URLDecoder.decode(request.rawQueryString, "UTF-8").split(" ").map {
      str => None -> StringVal(str)
    }: _*)
    ctx.defineVariable("_SERVER", PVar(ArrayVal(
      Some(StringVal("PHP_SELF")) -> StringVal(request.uri),
      Some(StringVal("argv")) -> serverArgv,
      Some(StringVal("argc")) -> serverArgv.count,
      Some(StringVal("REQUEST_METHOD")) -> StringVal(request.method),
      Some(StringVal("QUERY_STRING")) -> StringVal(request.rawQueryString)
    )))
  }
}
