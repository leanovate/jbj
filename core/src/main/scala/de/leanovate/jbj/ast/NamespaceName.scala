package de.leanovate.jbj.ast

case class NamespaceName(path: String*) {
  override def toString = path.mkString("\\")
}
