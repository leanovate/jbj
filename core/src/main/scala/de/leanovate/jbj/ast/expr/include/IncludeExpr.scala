package de.leanovate.jbj.ast.expr.include

import de.leanovate.jbj.ast.Expr
import de.leanovate.jbj.runtime.{ReturnExecResult, Context}
import de.leanovate.jbj.runtime.value.BooleanVal
import java.io.PrintStream

case class IncludeExpr(file: Expr) extends Expr {
  override def eval(implicit ctx: Context) = {
    val filename = file.eval.toStr.asString

    ctx.global.include(filename) match {
      case Some((prog, _)) =>
        prog.exec match {
          case ReturnExecResult(v) => v
          case _ => BooleanVal.TRUE
        }
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
