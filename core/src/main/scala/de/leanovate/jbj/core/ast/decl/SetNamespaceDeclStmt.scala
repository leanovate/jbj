package de.leanovate.jbj.core.ast.decl

import de.leanovate.jbj.runtime.{SuccessExecResult, NamespaceName}
import de.leanovate.jbj.core.ast.DeclStmt
import de.leanovate.jbj.runtime.context.Context
import de.leanovate.jbj.runtime.exception.FatalErrorJbjException

case class SetNamespaceDeclStmt(name: NamespaceName) extends DeclStmt {
  def register(implicit ctx: Context) {
    if (ctx.global.namespaceExclusive)
      throw new FatalErrorJbjException("Namespace declarations cannot be nested")

    ctx.global.resetCurrentNamepsace()
    ctx.global.currentNamespace = name
  }

  def exec(implicit ctx: Context) = {
    if (ctx.global.namespaceExclusive)
      throw new FatalErrorJbjException("Namespace declarations cannot be nested")

    ctx.global.resetCurrentNamepsace()
    ctx.global.currentNamespace = name

    SuccessExecResult
  }
}
