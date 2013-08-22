package de.leanovate.jbj.ast.expr

import de.leanovate.jbj.ast.Expr
import de.leanovate.jbj.runtime.ReturnExecResult
import de.leanovate.jbj.parser.{ParseContext, JbjParser}
import de.leanovate.jbj.runtime.value.{BooleanVal, NullVal}
import de.leanovate.jbj.runtime.exception.ParseJbjException
import de.leanovate.jbj.runtime.context.Context

case class EvalExpr(scriptExpr: Expr) extends Expr {
  def eval(implicit ctx: Context) = {
    val script = scriptExpr.evalOld.toStr.asString

    try {
      val parser = new JbjParser(ParseContext("%s(%d) : eval()'d code".format(ctx.currentPosition.fileName, ctx.currentPosition.line), ctx.settings))
      val prog = parser.parseStmt(script)

      prog.exec match {
        case ReturnExecResult(returnExpr) => returnExpr.map(_.evalOld).getOrElse(NullVal)
        case _ => NullVal
      }
    } catch {
      case e: ParseJbjException =>
        ctx.log.parseError(e.pos, "syntax error, unexpected %s".format(e.msg))
        BooleanVal.FALSE
    }
  }
}
