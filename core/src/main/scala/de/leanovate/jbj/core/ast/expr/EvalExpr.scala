/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.core.ast.expr

import de.leanovate.jbj.core.ast.Expr
import de.leanovate.jbj.runtime.ReturnExecResult
import de.leanovate.jbj.core.parser.{ParseContext, JbjParser}
import de.leanovate.jbj.runtime.value.{BooleanVal, NullVal}
import de.leanovate.jbj.runtime.exception.ParseJbjException
import de.leanovate.jbj.runtime.context.Context

case class EvalExpr(scriptExpr: Expr) extends Expr {
  override def eval(implicit ctx: Context) = {
    val script = scriptExpr.eval.asVal.toStr.asString

    try {
      val parser = new JbjParser(ParseContext("%s(%d) : eval()'d code".format(ctx.currentPosition.fileName, ctx.currentPosition.line), ctx.settings))
      val prog = parser.parseStmt(script)

      prog.exec match {
        case ReturnExecResult(returnExpr) => returnExpr.map(_.byVal).getOrElse(NullVal)
        case _ => NullVal
      }
    } catch {
      case e: ParseJbjException =>
        ctx.log.parseError(e.pos, "syntax error, unexpected %s".format(e.msg))
        BooleanVal.FALSE
    }
  }

  override def phpStr = "eval(" + scriptExpr.phpStr + ")"
}
