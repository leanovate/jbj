/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.core.ast.stmt.loop

import de.leanovate.jbj.runtime.value.{PVar, ArrayVal, PVal, PAny}
import de.leanovate.jbj.core.ast.{NodeVisitor, Node, RefExpr}
import de.leanovate.jbj.runtime.exception.FatalErrorJbjException
import de.leanovate.jbj.core.ast.expr.ListRefExpr
import de.leanovate.jbj.runtime.context.Context

sealed trait ForeachAssignment extends Node {
  def hasValueRef: Boolean

  def assignKey(key: PVal)(implicit ctx: Context)

  def assignValue(value: PAny, key: PVal, array: ArrayVal)(implicit ctx: Context)
}

case class ValueForeachAssignment(reference: RefExpr) extends ForeachAssignment {
  override def hasValueRef = false

  override def assignKey(key: PVal)(implicit ctx: Context) {
    reference.evalRef.assign(key)
  }

  override def assignValue(value: PAny, key: PVal, array: ArrayVal)(implicit ctx: Context) {
    reference.evalRef.assign(value.asVal)
  }

}

case class RefForeachAssignment(reference: RefExpr) extends ForeachAssignment {
  override def hasValueRef = true

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

  override def visit[R](visitor: NodeVisitor[R]) = visitor(this).thenChild(reference)
}

case class ListForeachAssignment(reference: ListRefExpr) extends ForeachAssignment {
  override def hasValueRef = false

  override def assignKey(key: PVal)(implicit ctx: Context) {
    throw new FatalErrorJbjException("Cannot use list as key element")
  }

  override def assignValue(value: PAny, key: PVal, array: ArrayVal)(implicit ctx: Context) {
    reference.evalRef.assign(value)
  }

  override def visit[R](visitor: NodeVisitor[R]) = visitor(this).thenChild(reference)
}