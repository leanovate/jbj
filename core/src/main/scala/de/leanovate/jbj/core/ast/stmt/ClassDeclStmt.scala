/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.core.ast.stmt

import de.leanovate.jbj.core.ast._
import de.leanovate.jbj.core.runtime._
import de.leanovate.jbj.core.runtime.SuccessExecResult
import scala.collection.mutable
import de.leanovate.jbj.core.runtime.value.{ConstVal, ObjectVal}
import de.leanovate.jbj.core.runtime.exception.FatalErrorJbjException
import de.leanovate.jbj.core.runtime.context._
import de.leanovate.jbj.core.ast.NamespaceName
import scala.collection.immutable.List
import de.leanovate.jbj.core.runtime.context.MethodContext
import scala.Some
import de.leanovate.jbj.core.runtime.context.InstanceContext
import de.leanovate.jbj.core.runtime.context.ClassContext

case class ClassDeclStmt(classEntry: ClassEntry.Type, name: NamespaceName,
                         superClassName: Option[NamespaceName], implements: List[NamespaceName],
                         decls: List[ClassMemberDecl])
  extends Stmt with PClass {

  private val staticInitializers = decls.filter(_.isInstanceOf[StaticInitializer]).map(_.asInstanceOf[StaticInitializer])

  private var _staticInitialized = false

  protected[stmt] val _classConstants = mutable.Map.empty[String, ConstVal]

  private lazy val instanceAssinments = decls.filter(_.isInstanceOf[ClassVarDecl])

  private var _superClass: Option[PClass] = None

  override def superClass = _superClass

  override def classConstants: Map[String, ConstVal] = _classConstants.toMap

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

        _classConstants ++= _superClass.get.classConstants
      }

      decls.foreach {
        method =>
          ctx.currentPosition = method.position
          method.initializeClass(this)
      }

      ctx.global.defineClass(this)
    }
    SuccessExecResult
  }

  override def initializeStatic(staticContext: StaticContext)(implicit ctx: Context) {
    if (!_staticInitialized) {
      _staticInitialized = true

      _superClass.foreach {
        parent =>
          val parentStaticCtx = ctx.global.staticContext(parent)
          parentStaticCtx.variables.foreach {
            case (name, value) =>
              staticContext.defineVariable(name, value)
          }
      }

      staticInitializers.foreach {
        staticInitializer =>
          val classCtx = ClassContext(this, ctx, staticInitializer.position)
          staticInitializer.initializeStatic(staticContext)(classCtx)
      }
    }
  }

  override def newEmptyInstance(pClass: PClass)(implicit ctx: Context): ObjectVal =
    superClass.map(_.newEmptyInstance(pClass)(ctx)).getOrElse(ObjectVal(pClass))


  override def initializeInstance(instance: ObjectVal)(implicit ctx: Context) {
    implicit val classCtx = InstanceContext(instance, this, ctx)

    instanceAssinments.foreach(_.initializeInstance(instance, this))

    superClass.foreach(_.initializeInstance(instance)(ctx))
  }

  override def newInstance(parameters: List[Expr])(implicit ctx: Context) = {
    if (classEntry == ClassEntry.ABSTRACT_CLASS)
      throw new FatalErrorJbjException("Cannot instantiate abstract class %s".format(name.toString))(ctx)
    val instance = newEmptyInstance(this)(ctx)

    initializeInstance(instance)(ctx)
    findConstructor.foreach(_.invoke(ctx, instance, parameters))
    instance
  }

  override def destructInstance(instance: ObjectVal)(implicit ctx: Context) {
    ctx match {
      case MethodContext(inst, pMethod, _) if instance.pClass == this && pMethod.name == "__destruct" =>
      case _ =>
        findDestructor.foreach(_.invoke(ctx, instance, Nil))
    }
    instance.cleanup()
  }

  override lazy val methods: Map[String, PMethod] = {
    val result = mutable.LinkedHashMap.empty[String, PMethod]

    superClass.foreach(result ++= _.methods)
    decls.foreach {
      case method: ClassMethodDecl =>
        method.declaringClass = this
        result -= method.name.toLowerCase
        result += method.name.toLowerCase -> method
      case _ =>
    }
    result.toMap
  }

  private def findConstructor: Option[PMethod] =
    findMethod(name.toString).map(Some.apply).getOrElse(findMethod("__construct"))

  private def findDestructor: Option[PMethod] = findMethod("__destruct")


  override def visit[R](visitor: NodeVisitor[R]) = visitor(this).thenChildren(decls)
}
