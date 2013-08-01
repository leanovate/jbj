package de.leanovate.jbj.ast

trait NodePosition {
  def fileName: String

  def line: Int
}

case class FileNodePosition(fileName: String, line: Int) extends NodePosition {
  override def toString = "<file:%s line:%d>".format(fileName, line)
}

object NoNodePosition extends NodePosition {
  val fileName = ""
  val line = 0

  override def toString = "<no position>"
}
