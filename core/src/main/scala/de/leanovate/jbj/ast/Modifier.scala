package de.leanovate.jbj.ast

object Modifier extends Enumeration {
  type Type = Value
  val PUBLIC, PROTECTED, PRIVATE, STATIC, GLOBAL = Value
}
