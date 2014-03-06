package de.leanovate.jbj.transcode

import de.leanovate.jbj.core.parser.{ParseContext, JbjParser}
import de.leanovate.jbj.api.http.JbjSettings

class Transcoder(settings: JbjSettings) {
  def toCodeUnit(fileName: String, script: String) {
    val parser = new JbjParser(ParseContext(fileName, settings))
    val prog = parser.parse(script)
  }
}
