/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.converter

import de.leanovate.jbj.core.parser.{ParseContext, JbjParser}
import de.leanovate.jbj.api.http.JbjSettings
import java.io.{Writer, StringWriter}
import scala.text.Document
import de.leanovate.jbj.converter.visitor.ProgVisitor

class Transcoder(settings: JbjSettings) {
  def toCodeUnit(fileName: String, script: String, packageName: Option[String]): Document = {
    val parser = new JbjParser(ParseContext(fileName, settings))
    val prog = parser.parse(script)

    prog.foldWith(new ProgVisitor(makeName(fileName), packageName))
  }

  def toCodeUnit(fileName: String, script: String, packageName: Option[String], output: Writer) {
    val document = toCodeUnit(fileName, script, packageName)
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
    output.flush()
    output.close()
  }
}
