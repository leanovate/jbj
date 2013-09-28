package de.leanovate.jbj.core.ast.decl

import de.leanovate.jbj.core.ast.DeclStmt
import de.leanovate.jbj.runtime.context.Context
import de.leanovate.jbj.runtime.{NamespaceName, SuccessExecResult}
import de.leanovate.jbj.runtime.exception.FatalErrorJbjException

case class UseDeclStmt(useAsDecls: List[UseAsDecl]) extends DeclStmt {
  def register(implicit ctx: Context) {
    addToContext(warnings = true)
  }

  def exec(implicit ctx: Context) = {
    addToContext(warnings = false)
    SuccessExecResult
  }

  private def addToContext(warnings: Boolean)(implicit ctx: Context) {
    val aliasBuilder = Map.newBuilder[String, NamespaceName]

    aliasBuilder ++= ctx.global.namespaceAliases

    useAsDecls.foreach {
      useAs =>
        useAs match {
          case UseAsDecl(name, None) if name.relative && name.path.length < 2 =>
            if (warnings)
              ctx.log.warn("The use statement with non-compound name '%s' has no effect".format(name.toString))
          case UseAsDecl(name, None) =>
            aliasBuilder += name.lastPath -> name.absolute
          case UseAsDecl(name, Some(alias)) =>
            if (ctx.global.findInterfaceOrClass(NamespaceName(alias), autoload = false).isDefined)
              throw new FatalErrorJbjException("Cannot use %s as %s because the name is already in use".format(name.toString, alias))
            aliasBuilder += alias -> name
        }
    }

    ctx.global.namespaceAliases = aliasBuilder.result()

  }
}
