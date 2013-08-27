package de.leanovate.jbj.core.runtime.output

trait OutputHandler {
  def clean()

  def contents: Option[Array[Byte]]
}
