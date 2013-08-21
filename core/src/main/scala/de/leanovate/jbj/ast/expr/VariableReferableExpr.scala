package de.leanovate.jbj.ast.expr

import de.leanovate.jbj.ast.{Name, ReferableExpr}
import de.leanovate.jbj.runtime.{Reference}
import java.io.PrintStream
import de.leanovate.jbj.runtime.value.{PVal, PAny, PVar, NullVal}
import de.leanovate.jbj.runtime.context.Context

case class VariableReferableExpr(variableName: Name) extends ReferableExpr {
  override def isDefined(implicit ctx: Context) = ctx.findVariable(variableName.evalName).isDefined

  override def eval(implicit ctx: Context) = {
    val name = variableName.evalName
    ctx.findVariable(name).getOrElse {
      ctx.log.notice("Undefined variable: %s".format(name))
      NullVal
    }
  }

  override def evalRef(implicit ctx: Context) = new Reference {
    val name = variableName.evalName

    def asVal = ctx.findVariable(name).map(_.value).getOrElse(NullVal)

    def asVar = ctx.findVariable(name).getOrElse {
      val result = PVar()
      ctx.defineVariable(name, result)
      result
    }

    def assign(pAny: PAny): PAny = {
      pAny match {
        case pVar: PVar =>
          ctx.defineVariable(name, pVar)
        case pVal: PVal =>
          ctx.findVariable(name) match {
            case Some(valueRef) => valueRef.value = pVal
            case None => ctx.defineVariable(name, PVar(pVal))
            case _ =>
          }
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
