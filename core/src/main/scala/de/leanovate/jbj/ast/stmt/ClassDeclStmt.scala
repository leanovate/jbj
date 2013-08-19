package de.leanovate.jbj.ast.stmt

import de.leanovate.jbj.ast._
import de.leanovate.jbj.runtime._
import de.leanovate.jbj.runtime.SuccessExecResult
import scala.collection.mutable
import de.leanovate.jbj.runtime.value.{PVal, ObjectVal}
import java.io.PrintStream
import de.leanovate.jbj.runtime.exception.FatalErrorJbjException
import de.leanovate.jbj.runtime.context.InstanceContext
import de.leanovate.jbj.ast.NamespaceName
import scala.Some
import scala.collection.immutable.List

case class ClassDeclStmt(classEntry: ClassEntry.Type, name: NamespaceName,
                         superClassName: Option[NamespaceName], implements: List[NamespaceName], stmts: List[Stmt])
  extends Stmt with PClass {

  private val staticInitializers = stmts.filter(_.isInstanceOf[StaticInitializer]).map(_.asInstanceOf[StaticInitializer])

  private var _superClass: Option[PClass] = None

  override def superClass = _superClass

  private lazy val instanceAssinments = stmts.filter(_.isInstanceOf[ClassVarDeclStmt])

  override def exec(implicit ctx: Context) = {
    if (ctx.global.findClass(name).isDefined)
      ctx.log.fatal(position, "Cannot redeclare class %s".format(name))
    else {
      if (superClassName.isDefined) {
        _superClass = ctx.global.findClassOrAutoload(superClassName.get)
        if (!superClass.isDefined)
          throw new FatalErrorJbjException("Class '%s' not found".format(superClassName.get))
        else if (superClass.get.classEntry == ClassEntry.FINAL_CLASS)
          throw new FatalErrorJbjException(
            "Class %s may not inherit from final class (%s)".format(name.toString, superClassName.get.toString))
      }

      methods.values.foreach(_.checkRules(this))
      staticInitializers.foreach(_.initializeStatic(this))
      ctx.global.defineClass(this)
    }
    SuccessExecResult
  }

  override def newEmptyInstance(pClass: PClass)(implicit ctx: Context, callerPosition: NodePosition): ObjectVal =
    superClass.map(_.newEmptyInstance(pClass)(ctx, callerPosition)).getOrElse(ObjectVal(pClass))


  override def initializeInstance(instance: ObjectVal)(implicit ctx: Context, callerPosition: NodePosition) {
    implicit val classCtx = InstanceContext(instance, callerPosition, ctx)

    instanceAssinments.foreach(_.exec)

    superClass.foreach(_.initializeInstance(instance)(ctx, callerPosition))
  }

  override def newInstance(parameters: List[Expr])(implicit ctx: Context, callerPosition: NodePosition) = {
    if (classEntry == ClassEntry.ABSTRACT_CLASS)
      throw new FatalErrorJbjException("Cannot instantiate abstract class %s".format(name.toString))(ctx, callerPosition)
    val instance = newEmptyInstance(this)(ctx, callerPosition)

    initializeInstance(instance)(ctx, callerPosition)
    findConstructor.foreach(_.invoke(ctx, callerPosition, instance, parameters))
    instance
  }

  override lazy val methods: Map[String, PMethod] = {
    val result = mutable.LinkedHashMap.empty[String, PMethod]

    superClass.foreach(result ++= _.methods)
    stmts.foreach {
      case method: PMethod =>
        result -= method.name.toLowerCase
        result += method.name.toLowerCase -> method
      case _ =>
    }
    result.toMap
  }

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
