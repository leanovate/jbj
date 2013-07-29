package de.leanovate.jbj.ast.stmt

import de.leanovate.jbj.ast.{FilePosition, Stmt}
import de.leanovate.jbj.runtime.{SuccessExecResult, Context}

case class ClassDeclStmt(position: FilePosition, className: String, superClassName: Option[String], body: BlockStmt)
  extends Stmt {

  def exec(ctx: Context) = {
    if (ctx.findClass(className).isDefined)
      ctx.log.fatal(position, "Cannot redeclare class %s".format(className))
    else if (superClassName.flatMap(ctx.findClass).isDefined)
      ctx.log.fatal(position, "Class '%s' not found".format(superClassName))
    else {
      def classCtx = ctx.defineClass(className)

      body.stmts.foreach(_.exec(classCtx))
    }
    SuccessExecResult()
  }
}
