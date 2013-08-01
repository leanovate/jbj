package de.leanovate.jbj.ast.stmt

import de.leanovate.jbj.ast.{NodePosition, ClassEntry, Stmt}
import de.leanovate.jbj.runtime._
import de.leanovate.jbj.runtime.SuccessExecResult
import de.leanovate.jbj.ast.NamespaceName
import scala.collection.mutable
import de.leanovate.jbj.runtime.value.{UndefinedVal, ObjectVal}

case class ClassDeclStmt(classEntry: ClassEntry.Type, name: NamespaceName,
                         superClassName: Option[NamespaceName], implements: List[NamespaceName], stmts: List[Stmt])
  extends Stmt with PClass {

  private lazy val methodMap = stmts.filter(_.isInstanceOf[PMethod]).map(_.asInstanceOf[PMethod]).map {
    m =>
      m.name -> m
  }.toMap

  override def exec(ctx: Context) = {
    if (ctx.findClass(name).isDefined)
      ctx.log.fatal(position, "Cannot redeclare class %s".format(name))
    else if (superClassName.flatMap(ctx.findClass).isDefined)
      ctx.log.fatal(position, "Class '%s' not found".format(superClassName))
    else {
      ctx.defineClass(this)
    }
    SuccessExecResult()
  }

  override def newInstance(ctx: Context, callerPosition: NodePosition, arguments: List[Value]): Value = {
    new ObjectVal(this, mutable.LinkedHashMap.empty[ArrayKey, Value])
  }

  override def invokeMethod(ctx: Context, callerPosition: NodePosition, instance: ObjectVal, methodName: String, parameters: List[Value]) = {
    methodMap.get(methodName).map(_.call(ctx, callerPosition, instance, parameters)).getOrElse(Left(UndefinedVal))
  }
}
