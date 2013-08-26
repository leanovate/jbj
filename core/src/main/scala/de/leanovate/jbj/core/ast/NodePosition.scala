package de.leanovate.jbj.core.ast

trait NodePosition {
  def fileName: String

  def line: Int
}

case class FileNodePosition(fileName: String, line: Int) extends NodePosition {
  override def toString = "<file:%s line:%d>".format(fileName, line)
}

object NoNodePosition extends NodePosition {
  val fileName = "Unknown"
  val line = 0

  override def toString = "<no position>"
}
