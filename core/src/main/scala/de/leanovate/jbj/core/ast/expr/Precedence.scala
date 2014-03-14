package de.leanovate.jbj.core.ast.expr

object Precedence extends Enumeration {
  type Type = Value
  val BoolOr1, BoolXor1, BoolAnd1,
  BoolOr2, BoolAnd2,
  BitOr, BitXor, BitAnd,
  Eq, Compare, BitShift, AddSub, MulDiv, Term = Value
}
