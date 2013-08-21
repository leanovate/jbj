package de.leanovate.jbj.ast.expr.include

import de.leanovate.jbj.ast.{HasNodePosition, Expr}
import de.leanovate.jbj.runtime.{ReturnExecResult}
import de.leanovate.jbj.runtime.value.{NullVal, BooleanVal}
import java.io.PrintStream
import de.leanovate.jbj.runtime.context.Context

case class IncludeExpr(file: Expr) extends Expr {
  override def eval(implicit ctx: Context) = {
    val filename = file.evalOld.toStr.asString

    ctx.global.include(filename) match {
      case Some((prog, _)) =>
        prog.exec match {
          case ReturnExecResult(returnExpr) => returnExpr.map(_.evalOld).getOrElse(NullVal)
          case _ => BooleanVal.TRUE
        }
      case _ =>
        ctx.log.warn("include(): Failed opening '%s' for inclusion (include_path='%s')".format(filename, ctx.currentPosition.fileName))
        BooleanVal.FALSE
    }
  }

  override def dump(out: PrintStream, ident: String) {
    out.println(ident + getClass.getSimpleName)
    file.dump(out, ident + "  ")
  }
}
