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
      m.name -> m
  }.toMap

  override def exec(ctx: Context) = {
    if (ctx.findClass(name).isDefined)
      ctx.log.fatal(position, "Cannot redeclare class %s".format(name))
    else {
      if (superClassName.isDefined) {
        superClass = ctx.findClass(superClassName.get)
        if (!superClass.isDefined)
          throw new FatalErrorJbjException(ctx, position, "Class '%s' not found".format(superClassName))
        else if (superClass.get.classEntry == ClassEntry.FINAL_CLASS)
          throw new FatalErrorJbjException(ctx, position, "Class %s may not inherit from final class (%s)".format(name.toString, superClassName.get.toString))
      }
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

  override def dump(out: PrintStream, ident: String) {
    out.println(ident + getClass.getSimpleName + " " + name.toString + " " + position)
    out.println(ident + "  " + name.toString)
    stmts.foreach {
      stmt =>
        stmt.dump(out, ident + "  ")
    }
  }
}
