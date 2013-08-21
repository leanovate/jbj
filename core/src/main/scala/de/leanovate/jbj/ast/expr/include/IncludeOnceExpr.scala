package de.leanovate.jbj.ast.expr.include

import de.leanovate.jbj.ast.Expr
import de.leanovate.jbj.runtime.{ReturnExecResult, Context}
import de.leanovate.jbj.runtime.value.BooleanVal
import java.io.PrintStream

case class IncludeOnceExpr(file: Expr) extends Expr {
  def eval(implicit ctx: Context) = {
    val filename = file.evalOld.toStr.asString

    ctx.global.include(filename) match {
      case Some((prog, true)) =>
        prog.exec
        BooleanVal.TRUE
      case Some((_, false)) =>
        BooleanVal.FALSE
      case _ =>
        ctx.log.warn(position,
          "include(): Failed opening '%s' for inclusion (include_path='%s')".format(filename, position.fileName))
        BooleanVal.FALSE
    }
  }

  override def dump(out: PrintStream, ident: String) {
    out.println(ident + getClass.getSimpleName + " " + position)
    file.dump(out, ident + "  ")
  }
}
