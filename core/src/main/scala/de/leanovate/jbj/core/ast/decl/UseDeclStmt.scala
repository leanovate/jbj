package de.leanovate.jbj.core.ast.decl

import de.leanovate.jbj.core.ast.DeclStmt
import de.leanovate.jbj.runtime.context.Context
import de.leanovate.jbj.runtime.{SuccessExecResult, NamespaceName}
import de.leanovate.jbj.runtime.exception.FatalErrorJbjException

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
      useAs =>
        val (alias, name) = useAs match {
          case UseAsDecl(n, None) => n.lastPath -> n.absolute
          case UseAsDecl(n, Some(a)) => a -> n.absolute
        }
        if (ctx.global.findInterfaceOrClass(NamespaceName(alias), autoload = false).isDefined)
          throw new FatalErrorJbjException("Cannot use %s as %s because the name is already in use".format(name.toString, alias))
        aliasBuilder += alias -> name
    }

    ctx.global.namespaceAliases = aliasBuilder.result()

  }
}
