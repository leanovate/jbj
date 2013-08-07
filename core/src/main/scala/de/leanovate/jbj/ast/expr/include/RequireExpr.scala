package de.leanovate.jbj.ast.expr.include

import de.leanovate.jbj.ast.Expr
import de.leanovate.jbj.runtime.{ReturnExecResult, Context}
import de.leanovate.jbj.runtime.value.BooleanVal
import de.leanovate.jbj.runtime.exception.FatalErrorJbjException

case class RequireExpr(file: Expr) extends Expr {
  def eval(implicit ctx: Context) = {
    val filename = file.eval.toStr.value

    ctx.global.include(filename) match {
      case Some((prog, _)) =>
        prog.exec match {
          case ReturnExecResult(v) => v
          case _ => BooleanVal.TRUE
        }
      case _ =>
        throw new FatalErrorJbjException("require(): Failed opening required '%s' for inclusion (include_path='%s')".
          format(filename, position.fileName))
    }
  }
}
