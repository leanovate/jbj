package de.leanovate.jbj.ast.expr

import de.leanovate.jbj.ast.Reference
import de.leanovate.jbj.runtime.Context

case class AssignRefExpr(reference: Reference, otherRef: Reference) extends BinaryExpr {
  def left = reference

  def right = otherRef

  override def eval(implicit ctx: Context) = {
    val valueOrRef = otherRef.evalRef
    reference.assignRef(valueOrRef)
    valueOrRef.value
  }
}
