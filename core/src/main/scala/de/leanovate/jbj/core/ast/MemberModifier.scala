package de.leanovate.jbj.core.ast

object MemberModifier extends Enumeration {
  type Type = Value
  val PUBLIC, PROTECTED, PRIVATE, STATIC, FINAL, ABSTRACT = Value
}
