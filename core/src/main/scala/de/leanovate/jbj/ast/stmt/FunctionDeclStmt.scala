package de.leanovate.jbj.ast.stmt

import de.leanovate.jbj.ast.{NodePosition, Stmt}
import de.leanovate.jbj.runtime.{SuccessExecResult, Context}
import de.leanovate.jbj.runtime.func.UserFunction

case class FunctionDeclStmt(name: String, parameters: List[ParameterDef], body: List[Stmt]) extends Stmt {
  def exec(ctx: Context) = {
    ctx.defineFunction(UserFunction(name, parameters, body))
    SuccessExecResult()
  }
}
