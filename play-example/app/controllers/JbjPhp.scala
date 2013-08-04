package controllers

import play.api.mvc.{AnyContent, Action, Controller, Request}
import java.io.{PrintStream, ByteArrayOutputStream, File}
import play.api.Play
import play.api.Play.current
import de.leanovate.jbj.parser.JbjParser
import de.leanovate.jbj.runtime.{ValueRef, Context}
import de.leanovate.jbj.runtime.context.GlobalContext
import scala.io.Source
import de.leanovate.jbj.runtime.env.CgiEnvironment
import de.leanovate.jbj.ast.{NodePosition, NoNodePosition}
import de.leanovate.jbj.runtime.value.{StringVal, ArrayVal}
import java.net.URLDecoder

object JbjPhp extends Controller {

  case class PhpScript(fileName: String, script: String)

  def get(path: String, file: String, pathInfo: String) = Action {
    request =>
      readScript(path, file).map(runPhp(_, request)).getOrElse(NotFound)
  }

  def post(path: String, file: String, pathInfo: String) = Action {
    request =>
      readScript(path, file).map(runPhp(_, request)).getOrElse(NotFound)
  }

  private def readScript(path: String, file: String): Option[PhpScript] = {
    val resourceName = Option(path + "/" + file).map(name => if (name.startsWith("/")) name else ("/" + name)).get

    if (!new File(resourceName).getCanonicalPath.startsWith(new File(path).getCanonicalPath)) {
      None
    } else {
      val resource = Play.resource(resourceName)

      resource.flatMap {
        case url if new File(url.getFile).isDirectory => None
        case url =>
          Some(PhpScript(resourceName, Source.fromInputStream(url.openStream(), "UTF-8").mkString))
      }
    }
  }

  private def runPhp(phpScript: PhpScript, request: Request[AnyContent]) = {
    val prog = JbjParser(phpScript.fileName, phpScript.script)
    val bOut = new ByteArrayOutputStream()
    val out = new PrintStream(bOut, false, "UTF-8")
    implicit val ctx = GlobalContext(out, System.err)
    implicit val position = NoNodePosition

    handleCommonParameters(request)
    request.method match {
      case "GET" =>
        val requestArray = CgiEnvironment.decodeKeyValues(request.queryString.toSeq.flatMap {
          case (key, values) => values.map(key -> _)
        })
        ctx.defineVariable("_REQUEST", ValueRef(requestArray.copy))
        ctx.defineVariable("_GET", ValueRef(requestArray.copy))
      case "POST" =>
        val requestArray = request.body.asFormUrlEncoded.map {
          keyValues =>
            CgiEnvironment.decodeKeyValues(keyValues.toSeq.flatMap {
              case (key, values) => values.map(key -> _)
            })
        }.getOrElse(ArrayVal())
        ctx.defineVariable("_REQUEST", ValueRef(requestArray.copy))
        ctx.defineVariable("_POST", ValueRef(requestArray.copy))
    }

    prog.exec

    bOut.flush()
    bOut.close()
    Ok(bOut.toByteArray).as("text/html")
  }

  private def handleCommonParameters(request: Request[AnyContent])(implicit ctx: Context, position: NodePosition) {
    val serverArgv = ArrayVal(URLDecoder.decode(request.rawQueryString, "UTF-8").split(" ").map {
      str => None -> StringVal(str)
    }: _*)
    ctx.defineVariable("_SERVER", ValueRef(ArrayVal(
      Some(StringVal("PHP_SELF")) -> StringVal(request.uri),
      Some(StringVal("argv")) -> serverArgv,
      Some(StringVal("argc")) -> serverArgv.count,
      Some(StringVal("REQUEST_METHOD")) -> StringVal(request.method),
      Some(StringVal("QUERY_STRING")) -> StringVal(request.rawQueryString)
    )))
  }
}
