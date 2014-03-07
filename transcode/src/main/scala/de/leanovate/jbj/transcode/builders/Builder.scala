package de.leanovate.jbj.transcode.builders

import scala.text.Document
import java.io.StringWriter


trait Builder {
  def build(): Document
}
