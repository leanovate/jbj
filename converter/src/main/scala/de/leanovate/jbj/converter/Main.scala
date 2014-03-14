/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.converter

import java.io.{FileWriter, File}
import scala.io.Source
import de.leanovate.jbj.api.http.JbjSettings
import org.apache.commons.io.filefilter.{FalseFileFilter, WildcardFileFilter}
import org.apache.commons.io.FileUtils
import scala.collection.JavaConversions._

object Main extends App {

  case class Config(outputDir: File = new File("."), packageName: Option[String] = None, files: Seq[File] = Seq.empty)

  val cmdLineParser = new scopt.OptionParser[Config]("transcoder") {
    head("JBJ Transcoder", "0.1")
    opt[File]('o', "output-dir") valueName ("<file>") action {
      (x, c) =>
        c.copy(outputDir = x)
    } text ("output directory")
    opt[String]('p', "package-name") valueName ("<package>") action {
      (x, c) =>
        c.copy(packageName = Some(x))
    } text ("package name")
    help("help") text ("prints this usage text")
    arg[String]("<file>...") unbounded() action {
      (x, c) =>
        if (!x.contains('*')) {
          c.copy(files = c.files :+ new File(x))
        } else {
          val idx = x.lastIndexOf(File.separator)
          val (dir, pattern) = if (idx >= 0) {
            x.substring(0, idx) -> x.substring(idx + 1)
          } else {
            "" -> x
          }
          c.copy(files = c.files ++ FileUtils.listFiles(new File(dir), new WildcardFileFilter(pattern), FalseFileFilter.FALSE))
        }
    } text ("Files to convert")
  }

  cmdLineParser.parse(args, Config()) map {
    config =>
      val settings = new JbjSettings
      val transcoder = new Transcoder(settings)
      config.outputDir.mkdirs()
      config.files.foreach {
        case file =>
          println(s"Transcodeing $file")
          val script = Source.fromFile(file, "UTF-8").mkString
          val out = new File(config.outputDir, transcoder.makeName(file.getName) + ".scala")

          transcoder.toCodeUnit(file.getName, script, config.packageName, new FileWriter(out))
      }
  }
}
