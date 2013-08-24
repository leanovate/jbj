package de.leanovate.jbj.core.ast

object ClassEntry extends Enumeration {
  type Type = Value
  val CLASS, ABSTRACT_CLASS, FINAL_CLASS, TRAIT = Value
}
