package de.leanovate.jbj.core.runtime.output

trait OutputTransformer {
  def name: String

  def transform(flags: Int, bytes: Array[Byte], offset: Int, length: Int): Array[Byte]
}
