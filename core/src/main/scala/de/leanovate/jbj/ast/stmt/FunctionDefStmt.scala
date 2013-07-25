package de.leanovate.jbj.ast.stmt

import de.leanovate.jbj.ast.{FilePosition, Stmt}
import de.leanovate.jbj.runtime.{SuccessExecResult, Context}
import de.leanovate.jbj.runtime.func.UserFunction

case class FunctionDefStmt(position:FilePosition,name: String, parameters: List[ParameterDef], body: BlockStmt) extends Stmt {
  def exec(ctx: Context) = {
    ctx.defineFunction(UserFunction(name, parameters, body.stmts))
    SuccessExecResult()
  }
}
