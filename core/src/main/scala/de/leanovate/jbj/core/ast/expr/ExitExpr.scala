package de.leanovate.jbj.core.ast.expr

import de.leanovate.jbj.core.ast.Expr
import de.leanovate.jbj.runtime.context.Context
import de.leanovate.jbj.runtime.exception.ExitJbjException
import de.leanovate.jbj.runtime.value.IntegerVal

case class ExitExpr(expr: Option[Expr]) extends Expr {
  override def eval(implicit ctx: Context) = {
    expr.map(_.eval.concrete).map {
      case IntegerVal(exitCode) =>
        throw new ExitJbjException(exitCode.toInt, None)
      case msg =>
        throw new ExitJbjException(-1, Some(msg.toStr.asString))
    }.getOrElse {
      throw new ExitJbjException(-1, None)
    }
  }

  override def phpStr = "exit " + expr.map(_.phpStr).getOrElse("")
}
