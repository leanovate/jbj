package de.leanovate.jbj.ast.stmt

import de.leanovate.jbj.ast.{NodePosition, ClassEntry, Stmt}
import de.leanovate.jbj.runtime._
import de.leanovate.jbj.runtime.SuccessExecResult
import de.leanovate.jbj.ast.NamespaceName
import scala.collection.mutable
import de.leanovate.jbj.runtime.value.ObjectVal
import java.io.PrintStream
import de.leanovate.jbj.runtime.exception.FatalErrorJbjException
import de.leanovate.jbj.runtime.context.ClassContext

case class ClassDeclStmt(classEntry: ClassEntry.Type, name: NamespaceName,
                         superClassName: Option[NamespaceName], implements: List[NamespaceName], stmts: List[Stmt])
  extends Stmt with PClass {

  private var superClass: Option[PClass] = None

  private lazy val instanceAssinments = stmts.filter(_.isInstanceOf[ClassVarDeclStmt])

  private lazy val methodMap = stmts.filter(_.isInstanceOf[PMethod]).map(_.asInstanceOf[PMethod]).map {
    m =>
      m.name.toLowerCase -> m
  }.toMap

  override def exec(implicit ctx: Context) = {
    if (ctx.global.findClass(name).isDefined)
      ctx.log.fatal(position, "Cannot redeclare class %s".format(name))
    else {
      if (superClassName.isDefined) {
        superClass = ctx.global.findClass(superClassName.get)
        if (!superClass.isDefined)
          throw new FatalErrorJbjException("Class '%s' not found".format(superClassName))
        else if (superClass.get.classEntry == ClassEntry.FINAL_CLASS)
          throw new FatalErrorJbjException(
            "Class %s may not inherit from final class (%s)".format(name.toString, superClassName.get.toString))
      }
      ctx.global.defineClass(this)
    }
    SuccessExecResult
  }

  override def newInstance(ctx: Context, callerPosition: NodePosition, parameters: List[Value]): Value = {
    if (classEntry == ClassEntry.ABSTRACT_CLASS)
      throw new FatalErrorJbjException("Cannot instantiate abstract class %s".format(name.toString))(ctx, callerPosition)
    val instance = new ObjectVal(this, instanceCounter.incrementAndGet(), mutable.LinkedHashMap.empty[ArrayKey, Value])

    implicit val classCtx = ClassContext(instance, callerPosition, ctx)

    instanceAssinments.foreach(_.exec)

    findConstructor.foreach(_.call(ctx, callerPosition, instance, parameters))
    instance
  }

  override def invokeMethod(ctx: Context, callerPosition: NodePosition, instance: ObjectVal, methodName: String,
                            parameters: List[Value]) = {
    findMethod(methodName) match {
      case Some(method) =>
        method.call(ctx, callerPosition, instance, parameters)
      case None =>
        throw new FatalErrorJbjException("Call to undefined method %s::%s()".format(name.toString, methodName))(ctx, callerPosition)
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

  private def findConstructor: Option[PMethod] =
    findMethod(name.toString).map(Some.apply).getOrElse(findMethod("__construct"))
}
