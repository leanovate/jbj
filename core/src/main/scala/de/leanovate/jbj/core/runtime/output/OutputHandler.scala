package de.leanovate.jbj.core.runtime.output

trait OutputHandler {
  def handleOutput(bytes: Array[Byte]): Array[Byte]
}
