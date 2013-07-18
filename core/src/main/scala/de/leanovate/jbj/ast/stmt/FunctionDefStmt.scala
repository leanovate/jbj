package de.leanovate.jbj.ast.stmt

import de.leanovate.jbj.ast.Stmt
import de.leanovate.jbj.runtime.{SuccessExecResult, Context}
import de.leanovate.jbj.runtime.func.UserFunction

case class FunctionDefStmt(name: String, parameters: List[ParameterDef], body: BlockStmt) extends Stmt {
  def exec(ctx: Context) = {
    ctx.defineFunction(UserFunction(name, parameters, body.stmts), static = false)
    SuccessExecResult()
  }
}
