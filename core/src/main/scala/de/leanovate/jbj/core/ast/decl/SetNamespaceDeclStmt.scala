package de.leanovate.jbj.core.ast.decl

import de.leanovate.jbj.runtime.{SuccessExecResult, NamespaceName}
import de.leanovate.jbj.core.ast.DeclStmt
import de.leanovate.jbj.runtime.context.Context
import de.leanovate.jbj.runtime.exception.FatalErrorJbjException

case class SetNamespaceDeclStmt(name: NamespaceName) extends DeclStmt {
  def register(implicit ctx: Context) {
    if (ctx.global.namespaceExclusive)
      throw new FatalErrorJbjException("Cannot mix bracketed namespace declarations with unbracketed namespace declarations")

    ctx.global.resetCurrentNamepsace()
    ctx.global.currentNamespace = name
  }

  def exec(implicit ctx: Context) = {
    if (ctx.global.namespaceExclusive)
      throw new FatalErrorJbjException("Cannot mix bracketed namespace declarations with unbracketed namespace declarations")

    ctx.global.resetCurrentNamepsace()
    ctx.global.currentNamespace = name

    SuccessExecResult
  }
}
