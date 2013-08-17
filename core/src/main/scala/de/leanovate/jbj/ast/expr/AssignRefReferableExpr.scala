package de.leanovate.jbj.ast.expr

import de.leanovate.jbj.ast.ReferableExpr
import de.leanovate.jbj.runtime.Context
import de.leanovate.jbj.runtime.value.{PAny, PVar}
import java.io.PrintStream

case class AssignRefReferableExpr(reference: ReferableExpr, otherRef: ReferableExpr) extends ReferableExpr {

  override def eval(implicit ctx: Context) = evalVar.asVal

  override def evalVar(implicit ctx: Context) = {
    otherRef.evalVar match {
      case pVar: PVar =>
        reference.assignVar(pVar)
        pVar
      case pAny =>
        ctx.log.strict(position, "Only variables should be assigned by reference")
        reference.assignVar(pAny)
        pAny.asVal
    }
  }

  override def assignVar(pAny: PAny)(implicit ctx: Context) {
    reference.assignVar(pAny)
  }

  override def unsetVar(implicit ctx: Context) {
    reference.unsetVar
  }

  override def dump(out: PrintStream, ident: String) {
    out.println(ident + getClass.getSimpleName + " " + position)
    reference.dump(out, ident + "  ")
    otherRef.dump(out, ident + "  ")
  }
}
