package de.leanovate.jbj.ast.expr

import de.leanovate.jbj.ast.ReferableExpr
import de.leanovate.jbj.runtime.Context

case class AssignRefExpr(reference: ReferableExpr, otherRef: ReferableExpr) extends BinaryExpr {
  def left = reference

  def right = otherRef

  override def eval(implicit ctx: Context) = {
    val valueOrRef = otherRef.evalVar
    reference.assignVar(valueOrRef)
    valueOrRef.value
  }
}
