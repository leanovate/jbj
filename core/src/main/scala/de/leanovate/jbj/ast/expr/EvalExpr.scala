package de.leanovate.jbj.ast.expr

import de.leanovate.jbj.ast.Expr
import de.leanovate.jbj.runtime.{ReturnExecResult, Context}
import de.leanovate.jbj.parser.{ParseContext, JbjParser}
import de.leanovate.jbj.runtime.value.{BooleanVal, NullVal}

case class EvalExpr(scriptExpr: Expr) extends Expr {
  def eval(implicit ctx: Context) = {
    val script = scriptExpr.eval.toStr.value

    try {
      val parser = new JbjParser(ParseContext(position.fileName))
      val prog = parser.parseStmt(script)

      prog.exec match {
        case ReturnExecResult(v) => v
        case _ => NullVal
      }
    } catch {
      case e: Throwable =>
        BooleanVal.FALSE
    }
  }
}
