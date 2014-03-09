package de.leanovate.jbj.converter.builders

import scala.text.Document
import java.io.StringWriter


trait Builder {
  def build(): Document
}
