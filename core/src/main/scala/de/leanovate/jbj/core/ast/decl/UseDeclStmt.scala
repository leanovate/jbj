package de.leanovate.jbj.core.ast.decl

import de.leanovate.jbj.core.ast.DeclStmt
import de.leanovate.jbj.runtime.context.Context
import de.leanovate.jbj.runtime.{SuccessExecResult, NamespaceName}

case class UseDeclStmt(useAsDecls: List[UseAsDecl]) extends DeclStmt {
  def register(implicit ctx: Context) {
    addToContext
  }

  def exec(implicit ctx: Context) = {
    addToContext
    SuccessExecResult
  }

  private def addToContext(implicit ctx: Context) {
    val aliasBuilder = Map.newBuilder[String, NamespaceName]

    aliasBuilder ++= ctx.global.namespaceAliases

    useAsDecls.foreach {
      case UseAsDecl(name, None) =>
        aliasBuilder += name.lastPath -> name.absolute
      case UseAsDecl(name, Some(alias)) =>
        aliasBuilder += alias -> name.absolute
    }
    ctx.global.namespaceAliases = aliasBuilder.result()

  }
}
