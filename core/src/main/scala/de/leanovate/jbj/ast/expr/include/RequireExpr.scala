package de.leanovate.jbj.ast.expr.include

import de.leanovate.jbj.ast.Expr
import de.leanovate.jbj.runtime.{ReturnExecResult, Context}
import de.leanovate.jbj.runtime.value.{NullVal, BooleanVal}
import de.leanovate.jbj.runtime.exception.FatalErrorJbjException
import java.io.PrintStream

case class RequireExpr(file: Expr) extends Expr {
  def eval(implicit ctx: Context) = {
    val filename = file.evalOld.toStr.asString

    ctx.global.include(filename) match {
      case Some((prog, _)) =>
        prog.exec match {
          case ReturnExecResult(returnExpr) => returnExpr.map(_.evalOld).getOrElse(NullVal)
          case _ => BooleanVal.TRUE
        }
      case _ =>
        throw new FatalErrorJbjException("require(): Failed opening required '%s' for inclusion (include_path='%s')".
          format(filename, position.fileName))
    }
  }

  override def dump(out: PrintStream, ident: String) {
    out.println(ident + getClass.getSimpleName + " " + position)
    file.dump(out, ident + "  ")
  }
}
