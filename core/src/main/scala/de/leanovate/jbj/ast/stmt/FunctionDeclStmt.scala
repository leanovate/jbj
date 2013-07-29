package de.leanovate.jbj.ast.stmt

import de.leanovate.jbj.ast.{FilePosition, Stmt}
import de.leanovate.jbj.runtime.{SuccessExecResult, Context}
import de.leanovate.jbj.runtime.func.UserFunction

case class FunctionDeclStmt(position: FilePosition, name: String, parameters: List[ParameterDef], body: List[Stmt]) extends Stmt {
  def exec(ctx: Context) = {
    ctx.defineFunction(UserFunction(name, parameters, body))
    SuccessExecResult()
  }
}
