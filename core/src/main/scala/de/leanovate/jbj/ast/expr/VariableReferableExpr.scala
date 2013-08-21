package de.leanovate.jbj.ast.expr

import de.leanovate.jbj.ast.{Name, ReferableExpr}
import de.leanovate.jbj.runtime.{Reference}
import java.io.PrintStream
import de.leanovate.jbj.runtime.value.{PVal, PAny, PVar, NullVal}
import de.leanovate.jbj.runtime.context.Context

case class VariableReferableExpr(variableName: Name) extends ReferableExpr {
  override def isDefined(implicit ctx: Context) = ctx.getVariable(variableName.evalName).isDefined

  override def eval(implicit ctx: Context) = {
    val name = variableName.evalName
    ctx.getVariable(name)
  }

  override def evalRef(implicit ctx: Context) = new Reference {
    val name = variableName.evalName

    def asVal = ctx.getVariable(name).value

    def asVar = ctx.getVariable(name)

    def assign(pAny: PAny): PAny = {
      pAny match {
        case pVar: PVar =>
          ctx.getVariable(name).ref = pVar
        case pVal: PVal =>
          ctx.getVariable(name).value = pVal
      }
      pAny
    }

    def unset() {
      ctx.getVariable(name).unset()
    }
  }

  override def dump(out: PrintStream, ident: String) {
    super.dump(out, ident)
    variableName.dump(out, ident + "  ")
  }
}
