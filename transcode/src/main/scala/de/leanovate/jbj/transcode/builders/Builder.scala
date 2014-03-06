package de.leanovate.jbj.transcode.builders

import scala.text.Document
import java.io.StringWriter


trait Builder {
  def build(): Document

  def writeTo(output: StringWriter) {
    build().format(1, output)
  }
}
