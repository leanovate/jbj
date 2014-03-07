package de.leanovate.jbj.transcode

import java.io.{FileWriter, FileOutputStream, File}
import scala.io.Source
import de.leanovate.jbj.api.http.JbjSettings

object Main extends App {

  case class Config(outputDir: File = new File("."), files: Seq[File] = Seq.empty)

  val cmdLineParser = new scopt.OptionParser[Config]("transcoder") {
    head("JBJ Transcoder", "0.1")
    opt[File]('o', "output-dir") valueName ("<file>") action {
      (x, c) =>
        c.copy(outputDir = x)
    } text ("output directory")
    help("help") text ("prints this usage text")
    arg[File]("<file>...") unbounded() action {
      (x, c) =>
        c.copy(files = c.files :+ x)
    } text ("Files to convert")
  }

  cmdLineParser.parse(args, Config()) map {
    config =>
      val settings = new JbjSettings
      val transcoder = new Transcoder(settings)
      config.outputDir.mkdirs()
      config.files.foreach {
        file =>
          println(s"Transcodeing $file")
          val script = Source.fromFile(file, "UTF-8").mkString
          val out = new File(config.outputDir, transcoder.makeName(file.getName) + ".scala")

          transcoder.toCodeUnit(file.getName, script, new FileWriter(out))
      }
  }

  def transcodeFile(file: File, outputDir: File) {
    val script = Source.fromFile(file, "UTF-8").mkString

  }

}
