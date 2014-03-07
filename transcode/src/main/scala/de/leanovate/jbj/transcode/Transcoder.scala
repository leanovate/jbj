package de.leanovate.jbj.transcode

import de.leanovate.jbj.core.parser.{ParseContext, JbjParser}
import de.leanovate.jbj.api.http.JbjSettings
import java.io.{Writer, StringWriter}
import scala.text.Document
import de.leanovate.jbj.transcode.visitor.ProgVisitor

class Transcoder(settings: JbjSettings) {
  def toCodeUnit(fileName: String, script: String): Document = {
    val parser = new JbjParser(ParseContext(fileName, settings))
    val prog = parser.parse(script)

    prog.foldWith(new ProgVisitor(makeName(fileName)))
  }

  def toCodeUnit(fileName: String, script: String, output: Writer) {
    val document = toCodeUnit(fileName, script)
    writeTo(document, output)
  }

  def makeName(fileName: String): String = {
    if (fileName.toLowerCase().endsWith(".php"))
      fileName.substring(0, fileName.length - 4).replace('.', '_')
    else
      fileName.replace('.', '_')
  }

  def writeTo(document: Document, output: Writer) {
    document.format(1, output)
  }
}
