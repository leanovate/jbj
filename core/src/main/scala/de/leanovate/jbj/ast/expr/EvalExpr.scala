package de.leanovate.jbj.ast.expr

import de.leanovate.jbj.ast.Expr
import de.leanovate.jbj.runtime.{ReturnExecResult, Context}
import de.leanovate.jbj.parser.{ParseContext, JbjParser}
import de.leanovate.jbj.runtime.value.{BooleanVal, NullVal}
import de.leanovate.jbj.runtime.exception.ParseJbjException

case class EvalExpr(scriptExpr: Expr) extends Expr {
  def eval(implicit ctx: Context) = {
    val script = scriptExpr.eval.toStr.asString

    try {
      val parser = new JbjParser(ParseContext("%s(%d) : eval()'d code".format(position.fileName, position.line), ctx.settings))
      val prog = parser.parseStmt(script)

      prog.exec match {
        case ReturnExecResult(returnExpr) => returnExpr.map(_.eval).getOrElse(NullVal)
        case _ => NullVal
      }
    } catch {
      case e: ParseJbjException =>
        ctx.log.parseError(e.pos, "syntax error, unexpected %s".format(e.msg))
        BooleanVal.FALSE
    }
  }
}
