package de.leanovate.jbj.core.ast.decl

import de.leanovate.jbj.runtime.{SuccessExecResult, NamespaceName}
import de.leanovate.jbj.core.ast.DeclStmt
import de.leanovate.jbj.runtime.context.Context

case class SetNamespaceDeclStmt(name: NamespaceName) extends DeclStmt {
  def register(implicit ctx: Context) {
    ctx.global.resetCurrentNamepsace()
    ctx.global.currentNamespace = name
  }

  def exec(implicit ctx: Context) = {
    ctx.global.resetCurrentNamepsace()
    ctx.global.currentNamespace = name

    SuccessExecResult
  }
}
