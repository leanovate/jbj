package de.leanovate.jbj.ast

case class NamespaceName(path: String*) {
  lazy val lowercase = path.map(_.toLowerCase)

  override def toString = path.mkString("\\")
}