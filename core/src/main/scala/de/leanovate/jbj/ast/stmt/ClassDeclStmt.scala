package de.leanovate.jbj.ast.stmt

import de.leanovate.jbj.ast.{NodePosition, NamespaceName, ClassEntry, Stmt}
import de.leanovate.jbj.runtime._
import de.leanovate.jbj.runtime.SuccessExecResult
import de.leanovate.jbj.ast.NamespaceName

case class ClassDeclStmt(classEntry: ClassEntry.Type, className: String,
                         superClassName: Option[NamespaceName], implements: List[NamespaceName], stmts: List[Stmt])
  extends Stmt with PClass {

  override def exec(ctx: Context) = {
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

  override def newInstance(ctx: Context, callerPosition: NodePosition, arguments: List[Value]) {

  }

  override def invokemethod(ctx: Context, callerPosition: NodePosition, instance: Value, parameters: List[Value]) = ???
}
