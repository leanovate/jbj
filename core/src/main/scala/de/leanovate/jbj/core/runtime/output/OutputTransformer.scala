package de.leanovate.jbj.core.runtime.output

trait OutputTransformer {
  def transform(bytes: Array[Byte], offset:Int, length:Int): Array[Byte]
}
