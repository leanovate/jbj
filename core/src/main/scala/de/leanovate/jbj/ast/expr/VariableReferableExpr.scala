package de.leanovate.jbj.ast.expr

import de.leanovate.jbj.ast.{Name, ReferableExpr}
import de.leanovate.jbj.runtime.Context
import java.io.PrintStream
import de.leanovate.jbj.runtime.value.{PVal, PAny, PVar, NullVal}

case class VariableReferableExpr(variableName: Name) extends ReferableExpr {
  override def isDefined(implicit ctx: Context) = ctx.findVariable(variableName.evalName).isDefined

  override def eval(implicit ctx: Context) = {
    val name = variableName.evalName
    ctx.findVariable(name).map(_.value).getOrElse {
      ctx.log.notice(position, "Undefined variable: %s".format(name))
      NullVal
    }
  }

  override def evalVar(implicit ctx: Context) = {
    val name = variableName.evalName
    ctx.findVariable(name).getOrElse {
      val result = PVar()
      ctx.defineVariable(name, result)
      result
    }
  }

  override def assignVar(pAny: PAny)(implicit ctx: Context) {
    val name = variableName.evalName
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
  }

  override def unsetVar(implicit ctx: Context) {
    ctx.undefineVariable(variableName.evalName)
  }

  override def dump(out: PrintStream, ident: String) {
    super.dump(out, ident)
    variableName.dump(out, ident + "  ")
  }
}
