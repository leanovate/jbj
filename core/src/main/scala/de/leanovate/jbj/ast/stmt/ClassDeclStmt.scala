package de.leanovate.jbj.ast.stmt

import de.leanovate.jbj.ast.{NodePosition, ClassEntry, Stmt}
import de.leanovate.jbj.runtime._
import de.leanovate.jbj.runtime.SuccessExecResult
import de.leanovate.jbj.ast.NamespaceName
import scala.collection.mutable
import de.leanovate.jbj.runtime.value.{UndefinedVal, ObjectVal}
import java.io.PrintStream
import de.leanovate.jbj.runtime.exception.FatalErrorJbjException

case class ClassDeclStmt(classEntry: ClassEntry.Type, name: NamespaceName,
                         superClassName: Option[NamespaceName], implements: List[NamespaceName], stmts: List[Stmt])
  extends Stmt with PClass {

  private var superClass: Option[PClass] = None

  private lazy val methodMap = stmts.filter(_.isInstanceOf[PMethod]).map(_.asInstanceOf[PMethod]).map {
    m =>
      m.name.toLowerCase -> m
  }.toMap

  override def exec(implicit ctx: Context) = {
    if (ctx.findClass(name).isDefined)
      ctx.log.fatal(position, "Cannot redeclare class %s".format(name))
    else {
      if (superClassName.isDefined) {
        superClass = ctx.findClass(superClassName.get)
        if (!superClass.isDefined)
          throw new FatalErrorJbjException(ctx, position, "Class '%s' not found".format(superClassName))
        else if (superClass.get.classEntry == ClassEntry.FINAL_CLASS)
          throw new FatalErrorJbjException(ctx, position,
            "Class %s may not inherit from final class (%s)".format(name.toString, superClassName.get.toString))
      }
      ctx.defineClass(this)
    }
    SuccessExecResult()
  }

  override def newInstance(ctx: Context, callerPosition: NodePosition, parameters: List[Value]): Value = {
    if (classEntry == ClassEntry.ABSTRACT_CLASS)
      throw new FatalErrorJbjException(ctx, callerPosition, "Cannot instantiate abstract class %s".format(name.toString))
    val instance = new ObjectVal(this, mutable.LinkedHashMap.empty[ArrayKey, Value])

    findConstructor.foreach(_.call(ctx, callerPosition, instance, parameters))
    instance
  }

  override def invokeMethod(ctx: Context, callerPosition: NodePosition, instance: ObjectVal, methodName: String,
                            parameters: List[Value]) = {
    findMethod(methodName) match {
      case Some(method) =>
        method.call(ctx, callerPosition, instance, parameters)
      case None =>
        throw new FatalErrorJbjException(ctx, callerPosition, "Call to undefined method %s::%s()".format(name.toString, methodName))
    }
  }

  override def findMethod(methodName: String): Option[PMethod] =
    methodMap.get(methodName.toLowerCase).map(Some.apply).getOrElse(superClass.flatMap(_.findMethod(methodName)))

  override def dump(out: PrintStream, ident: String) {
    out.println(ident + getClass.getSimpleName + " " + name.toString + " " + position)
    out.println(ident + "  " + name.toString)
    stmts.foreach {
      stmt =>
        stmt.dump(out, ident + "  ")
    }
  }

  private def findConstructor : Option[PMethod] =
    findMethod(name.toString).map(Some.apply).getOrElse(findMethod("__construct"))
}
