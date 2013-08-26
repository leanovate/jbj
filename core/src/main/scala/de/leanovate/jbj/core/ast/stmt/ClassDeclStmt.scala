package de.leanovate.jbj.core.ast.stmt

import de.leanovate.jbj.core.ast._
import de.leanovate.jbj.core.runtime._
import de.leanovate.jbj.core.runtime.SuccessExecResult
import scala.collection.mutable
import de.leanovate.jbj.core.runtime.value.ObjectVal
import java.io.PrintStream
import de.leanovate.jbj.core.runtime.exception.FatalErrorJbjException
import de.leanovate.jbj.core.runtime.context.{Context, InstanceContext}
import de.leanovate.jbj.core.ast.NamespaceName
import scala.Some
import scala.collection.immutable.List

case class ClassDeclStmt(classEntry: ClassEntry.Type, name: NamespaceName,
                         superClassName: Option[NamespaceName], implements: List[NamespaceName],
                         decls: List[ClassMemberDecl])
  extends Stmt with PClass {

  private val staticInitializers = decls.filter(_.isInstanceOf[StaticInitializer]).map(_.asInstanceOf[StaticInitializer])

  private var _superClass: Option[PClass] = None

  override def superClass = _superClass

  private lazy val instanceAssinments = decls.filter(_.isInstanceOf[ClassVarDecl])

  override def exec(implicit ctx: Context) = {
    if (ctx.global.findClass(name).isDefined)
      ctx.log.fatal("Cannot redeclare class %s".format(name))
    else {
      if (superClassName.isDefined) {
        _superClass = ctx.global.findClassOrAutoload(superClassName.get)
        if (!superClass.isDefined)
          throw new FatalErrorJbjException("Class '%s' not found".format(superClassName.get))
        else if (superClass.get.classEntry == ClassEntry.FINAL_CLASS)
          throw new FatalErrorJbjException(
            "Class %s may not inherit from final class (%s)".format(name.toString, superClassName.get.toString))
      }

      decls.filter(_.isInstanceOf[ClassMethodDecl]).map(_.asInstanceOf[ClassMethodDecl]).foreach {
        method =>
          ctx.currentPosition = method.position
          method.checkRules(this)
      }
      staticInitializers.foreach(_.initializeStatic(this))
      ctx.global.defineClass(this)
    }
    SuccessExecResult
  }

  override def newEmptyInstance(pClass: PClass)(implicit ctx: Context): ObjectVal =
    superClass.map(_.newEmptyInstance(pClass)(ctx)).getOrElse(ObjectVal(pClass))


  override def initializeInstance(instance: ObjectVal)(implicit ctx: Context) {
    implicit val classCtx = InstanceContext(instance, ctx)

    instanceAssinments.foreach(_.initializeInstance(instance, this))

    superClass.foreach(_.initializeInstance(instance)(ctx))
  }

  override def newInstance(parameters: List[Expr])(implicit ctx: Context) = {
    if (classEntry == ClassEntry.ABSTRACT_CLASS)
      throw new FatalErrorJbjException("Cannot instantiate abstract class %s".format(name.toString))(ctx)
    val instance = newEmptyInstance(this)(ctx)

    initializeInstance(instance)(ctx)
    findConstructor.foreach(_.invoke(ctx, instance, this, parameters))
    instance
  }

  override def destructInstance(instance: ObjectVal)(implicit ctx: Context) {
    findDestructor.foreach(_.invoke(ctx, instance, this, Nil))
    instance.cleanup()
  }

  override lazy val methods: Map[String, PMethod] = {
    val result = mutable.LinkedHashMap.empty[String, PMethod]

    superClass.foreach(result ++= _.methods)
    decls.foreach {
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
    decls.foreach {
      stmt =>
        stmt.dump(out, ident + "  ")
    }
  }

  private def findConstructor: Option[PMethod] =
    findMethod(name.toString).map(Some.apply).getOrElse(findMethod("__construct"))

  private def findDestructor: Option[PMethod] = findMethod("__destruct")

  override def visit[R](visitor: NodeVisitor[R]) = visitor(this).thenChildren(decls)
}
