package controllers

import play.api.mvc.{Action, Controller}
import java.io.{InputStream, PrintStream, ByteArrayOutputStream, File}
import play.api.Play
import play.api.Play.current
import de.leanovate.jbj.parser.JbjParser
import de.leanovate.jbj.runtime.context.GlobalContext
import scala.io.Source

object JbjPhp extends Controller {
  def get(path: String, file: String) = Action {
    request =>
      val resourceName = Option(path + "/" + file).map(name => if (name.startsWith("/")) name else ("/" + name)).get

      if (!new File(resourceName).getCanonicalPath.startsWith(new File(path).getCanonicalPath)) {
        NotFound
      } else {
        val resource = Play.resource(resourceName)

        resource.map {
          case url if new File(url.getFile).isDirectory => NotFound
          case url =>
            runPhp(resourceName, url.openStream())
        }.getOrElse(NotFound)
      }
  }

  def post(path: String, file: String) = Action {
    request =>
      Ok("Bla")
  }

  private def runPhp(fileName: String, in: InputStream) = {
    val script = Source.fromInputStream(in, "UTF-8").mkString

    val prog = JbjParser(fileName, script)
    val bOut = new ByteArrayOutputStream()
    val out = new PrintStream(bOut, false, "UTF-8")
    val context = GlobalContext(out, System.err)

    prog.exec(context)
    bOut.flush()
    bOut.close()
    Ok(bOut.toByteArray).as("text/html")
  }
}
