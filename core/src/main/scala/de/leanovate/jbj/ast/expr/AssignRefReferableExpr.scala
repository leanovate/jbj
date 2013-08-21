package de.leanovate.jbj.ast.expr

import de.leanovate.jbj.ast.{NodePosition, ReferableExpr}
import de.leanovate.jbj.runtime.{Reference, Context}
import de.leanovate.jbj.runtime.value.{PAny, PVar}
import java.io.PrintStream

case class AssignRefReferableExpr(reference: ReferableExpr, otherRef: ReferableExpr) extends ReferableExpr {
  override def position_=(pos: NodePosition) {
    super.position_=(pos)

    reference.position = pos
  }

  override def eval(implicit ctx: Context) = evalRef.asVal

  override def evalRef(implicit ctx: Context) = new Reference {
    val resultRef = reference.evalRef
    val result = otherRef.evalRef.asVar match {
      case pVar: PVar =>
        resultRef.assign(pVar)
      case pAny =>
        ctx.log.strict("Only variables should be assigned by reference")
        resultRef.assign(pAny)
    }

    def asVal = result.asVal

    def asVar = result

    def assign(pAny: PAny) = resultRef.assign(pAny)

    def unset() {
      resultRef.unset()
    }
  }

  override def dump(out: PrintStream, ident: String) {
    out.println(ident + getClass.getSimpleName + " " + position)
    reference.dump(out, ident + "  ")
    otherRef.dump(out, ident + "  ")
  }
}
