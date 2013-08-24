package de.leanovate.jbj.core.ast.expr.include

import de.leanovate.jbj.core.ast.Expr
import de.leanovate.jbj.core.runtime.ReturnExecResult
import de.leanovate.jbj.core.runtime.value.{NullVal, BooleanVal}
import de.leanovate.jbj.core.runtime.exception.FatalErrorJbjException
import java.io.PrintStream
import de.leanovate.jbj.core.runtime.context.Context

case class RequireExpr(file: Expr) extends Expr {
  def eval(implicit ctx: Context) = {
    val filename = file.eval.asVal.toStr.asString

    ctx.global.include(filename) match {
      case Some((prog, _)) =>
        prog.exec match {
          case ReturnExecResult(returnExpr) => returnExpr.map(_.eval.asVal).getOrElse(NullVal)
          case _ => BooleanVal.TRUE
        }
      case _ =>
        throw new FatalErrorJbjException("require(): Failed opening required '%s' for inclusion (include_path='%s')".
          format(filename, ctx.currentPosition.fileName))
    }
  }

  override def dump(out: PrintStream, ident: String) {
    out.println(ident + getClass.getSimpleName)
    file.dump(out, ident + "  ")
  }
}
