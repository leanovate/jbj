package de.leanovate.jbj.ast.expr

import de.leanovate.jbj.ast.ReferableExpr
import de.leanovate.jbj.runtime.{Reference, Context}
import de.leanovate.jbj.runtime.value.{PAny, PVar}
import java.io.PrintStream
import de.leanovate.jbj.runtime.exception.FatalErrorJbjException

case class AssignRefReferableExpr(reference: ReferableExpr, otherRef: ReferableExpr) extends ReferableExpr {

  override def eval(implicit ctx: Context) = evalRef.asVar.asVal

  override def evalRef(implicit ctx: Context) = new Reference {
    val resultRef = otherRef.evalRef

    def asVal = resultRef.asVal

    def asVar = resultRef.asVar match {
      case pVar: PVar =>
        reference.evalRef.assign(pVar)
        pVar
      case pAny =>
        ctx.log.strict(position, "Only variables should be assigned by reference")
        reference.evalRef.assign(pAny)
        pAny.asVal
    }

    def assign(pAny: PAny) = {
      resultRef.assign(pAny)
      pAny
    }

    def unset() {
      resultRef.unset()
      throw new FatalErrorJbjException("Can't use function return value in write context")
    }
  }

  override def dump(out: PrintStream, ident: String) {
    out.println(ident + getClass.getSimpleName + " " + position)
    reference.dump(out, ident + "  ")
    otherRef.dump(out, ident + "  ")
  }
}
