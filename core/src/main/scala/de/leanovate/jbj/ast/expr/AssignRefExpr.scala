package de.leanovate.jbj.ast.expr

import de.leanovate.jbj.ast.ReferableExpr
import de.leanovate.jbj.runtime.Context
import de.leanovate.jbj.runtime.value.PVar

case class AssignRefExpr(reference: ReferableExpr, otherRef: ReferableExpr) extends BinaryExpr {
  def left = reference

  def right = otherRef

  override def eval(implicit ctx: Context) = {
    otherRef.evalVar match {
      case pVar: PVar =>
        reference.assignVar(pVar)
        pVar.value
      case pAny =>
        ctx.log.strict(position, "Only variables should be assigned by reference")
        reference.assignVar(pAny)
        pAny.value
    }
  }
}
