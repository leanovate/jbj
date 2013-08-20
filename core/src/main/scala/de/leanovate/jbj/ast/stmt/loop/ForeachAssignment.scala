package de.leanovate.jbj.ast.stmt.loop

import de.leanovate.jbj.runtime.value.{PVar, ArrayVal, PVal, PAny}
import de.leanovate.jbj.ast.{NodePosition, Node, ReferableExpr}
import de.leanovate.jbj.runtime.exception.FatalErrorJbjException
import de.leanovate.jbj.runtime.Context
import de.leanovate.jbj.ast.expr.ListReferableExpr

sealed trait ForeachAssignment extends Node {
  def assignKey(key: PVal)(implicit ctx: Context)

  def assignValue(value: PAny, key: PVal, array: ArrayVal)(implicit ctx: Context)
}

case class ValueForeachAssignment(reference: ReferableExpr) extends ForeachAssignment {
  override def position_=(pos: NodePosition) {
    super.position_=(pos)

    reference.position = pos
  }

  override def assignKey(key: PVal)(implicit ctx: Context) {
    reference.evalRef.assign(key)
  }

  override def assignValue(value: PAny, key: PVal, array: ArrayVal)(implicit ctx: Context) {
    reference.evalRef.assign(value.asVal)
  }
}

case class RefForeachAssignment(reference: ReferableExpr) extends ForeachAssignment {
  override def position_=(pos: NodePosition) {
    super.position_=(pos)

    reference.position = pos
  }

  override def assignKey(key: PVal)(implicit ctx: Context) {
    throw new FatalErrorJbjException("Key element cannot be a reference")
  }

  override def assignValue(value: PAny, key: PVal, array: ArrayVal)(implicit ctx: Context) {
    value match {
      case pVar: PVar =>
        reference.evalRef.assign(pVar)
      case pVal: PVal =>
        val pVar = PVar(pVal)
        array.setAt(key, pVar)
        reference.evalRef.assign(pVar)
    }
  }
}

case class ListForeachAssignment(reference: ListReferableExpr) extends ForeachAssignment {
  def assignKey(key: PVal)(implicit ctx: Context) {
    throw new FatalErrorJbjException("Cannot use list as key element")
  }

  def assignValue(value: PAny, key: PVal, array: ArrayVal)(implicit ctx: Context) {
    reference.evalRef.assign(value)
  }
}