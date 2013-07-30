package de.leanovate.jbj.ast.stmt

import de.leanovate.jbj.ast.{NamespaceName, ClassEntry, Stmt}
import de.leanovate.jbj.runtime.{SuccessExecResult, Context}

case class ClassDeclStmt(classEntry: ClassEntry.Type, className: String,
                         superClassName: Option[NamespaceName], implements: List[NamespaceName], stmts: List[Stmt])
  extends Stmt {

  def exec(ctx: Context) = {
    if (ctx.findClass(NamespaceName(className)).isDefined)
      ctx.log.fatal(position, "Cannot redeclare class %s".format(className))
    else if (superClassName.flatMap(ctx.findClass).isDefined)
      ctx.log.fatal(position, "Class '%s' not found".format(superClassName))
    else {
      def classCtx = ctx.defineClass(className)

      stmts.foreach(_.exec(classCtx))
    }
    SuccessExecResult()
  }
}
