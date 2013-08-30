/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.core.ast.stmt.decl

import de.leanovate.jbj.core.ast._
import de.leanovate.jbj.core.runtime._
import de.leanovate.jbj.core.runtime.value._
import de.leanovate.jbj.core.runtime.context.{GlobalContext, Context, MethodContext, StaticMethodContext}
import de.leanovate.jbj.core.runtime.exception.FatalErrorJbjException
import de.leanovate.jbj.core.runtime.buildin.StdClass
import de.leanovate.jbj.core.ast.stmt.{FunctionLike, BlockLike, ParameterDecl}

case class ClassMethodDecl(modifieres: Set[MemberModifier.Type], name: String, returnByRef: Boolean, parameterDecls: List[ParameterDecl],
                           stmts: Option[List[Stmt]]) extends ClassMemberDecl with PMethod with BlockLike with FunctionLike {
  private var _declaringClass: PClass = StdClass
  private var _activeModifiers: Set[MemberModifier.Type] = modifieres

  private lazy val staticInitializers = StaticInitializer.collect(stmts.getOrElse(Nil): _*)

  override def declaringClass = _declaringClass

  protected[stmt] def declaringClass_=(pClass: PClass) {
    _declaringClass = pClass
  }

  override def activeModifieres = _activeModifiers

  override def invoke(ctx: Context, instance: ObjectVal, parameters: List[Expr]) = {
    if (isPrivate) {
      ctx match {
        case global: GlobalContext if global.inShutdown =>
          global.log.warn("Call to private %s::%s() from context '%s' during shutdown ignored".format(declaringClass.name.toString, name, ctx.name))
        case MethodContext(_, pMethod, _) if declaringClass == pMethod.declaringClass =>
        case StaticMethodContext(pMethod, _) if declaringClass == pMethod.declaringClass =>
        case _ =>
          if (name == "__construct")
            throw new FatalErrorJbjException("Call to private %s::%s() from invalid context".format(declaringClass.name.toString, name))(ctx)
          else if (name.startsWith("__"))
            throw new FatalErrorJbjException("Call to private %s::%s() from context '%s'".format(declaringClass.name.toString, name, ctx.name))(ctx)
          else
            throw new FatalErrorJbjException("Call to private method %s::%s() from context '%s'".format(declaringClass.name.toString, name, ctx.name))(ctx)
      }
    }
    if (isProtected) {
      ctx match {
        case global: GlobalContext if global.inShutdown =>
          global.log.warn("Call to protected %s::%s() from context '%s' during shutdown ignored".format(declaringClass.name.toString, name, ctx.name))
        case MethodContext(_, pMethod, _) if declaringClass.isAssignableFrom(pMethod.declaringClass) =>
        case StaticMethodContext(pMethod, _) if declaringClass.isAssignableFrom(pMethod.declaringClass) =>
        case _ =>
          if (name == "__construct")
            throw new FatalErrorJbjException("Call to protected %s::%s() from invalid context".format(declaringClass.name.toString, name))(ctx)
          else if (name.startsWith("__"))
            throw new FatalErrorJbjException("Call to protected %s::%s() from context '%s'".format(declaringClass.name.toString, name, ctx.name))(ctx)
          else
            throw new FatalErrorJbjException("Call to protected method %s::%s() from context '%s'".format(declaringClass.name.toString, name, ctx.name))(ctx)
      }
    }

    implicit val methodCtx = MethodContext(instance, this, ctx)

    if (!methodCtx.static.initialized) {
      staticInitializers.foreach(_.initializeStatic(methodCtx.static))
      methodCtx.static.initialized = true
    }

    setParameters(methodCtx, ctx, parameters)
    perform(methodCtx, returnByRef, stmts.getOrElse(Nil))
  }

  override def invokeStatic(ctx: Context, parameters: List[Expr]) = {
    if (isPrivate) {
      ctx match {
        case global: GlobalContext if global.inShutdown =>
          global.log.warn("Call to private %s::%s() from context '%s' during shutdown ignored".format(declaringClass.name.toString, name, ctx.name))
        case MethodContext(_, pMethod, _) if declaringClass == pMethod.declaringClass =>
        case StaticMethodContext(pMethod, _) if declaringClass == pMethod.declaringClass =>
        case _ =>
          throw new FatalErrorJbjException("Call to private method %s::%s() from context '%s'".format(declaringClass.name.toString, name, ctx.name))(ctx)
      }
    }
    if (isProtected) {
      ctx match {
        case global: GlobalContext if global.inShutdown =>
          global.log.warn("Call to protected %s::%s() from context '%s' during shutdown ignored".format(declaringClass.name.toString, name, ctx.name))
        case MethodContext(_, pMethod, _) if declaringClass.isAssignableFrom(pMethod.declaringClass) =>
        case StaticMethodContext(pMethod, _) if declaringClass.isAssignableFrom(pMethod.declaringClass) =>
        case _ =>
          throw new FatalErrorJbjException("Call to protected method %s::%s() from context '%s'".format(declaringClass.name.toString, name, ctx.name))(ctx)
      }
    }

    implicit val methodCtx = StaticMethodContext(this, ctx)

    if (!methodCtx.static.initialized) {
      staticInitializers.foreach(_.initializeStatic(methodCtx.static))
      methodCtx.static.initialized = true
    }

    setParameters(methodCtx, ctx, parameters)

    if (!isStatic)
      ctx.log.strict("Non-static method %s::%s() should not be called statically".format(declaringClass.name.toString, name))

    perform(methodCtx, returnByRef, stmts.getOrElse(Nil))
  }


  override def initializeInterface(pInterface: InterfaceDeclStmt)(implicit ctx: Context) {
    if (isPrivate) {
      throw new FatalErrorJbjException("Access type for interface method %s::%s() must be omitted".
        format(pInterface.name.toString, name))
    }
    if (isFinal) {
      throw new FatalErrorJbjException("Cannot use the final modifier on an abstract class member")
    }
    if (stmts.isDefined) {
      throw new FatalErrorJbjException("Interface function %s::%s() cannot contain body".
        format(pInterface.name.toString, name))
    }
  }

  override def initializeClass(pClass: ClassDeclStmt)(implicit ctx: Context) {
    if (isAbstract && isFinal) {
      throw new FatalErrorJbjException("Cannot use the final modifier on an abstract class member")
    }

    name match {
      case "__call" =>
        if (parameterDecls.size != 2)
          throw new FatalErrorJbjException("Method %s::__call() must take exactly 2 arguments".format(pClass.name.toString))
        if (modifieres.contains(MemberModifier.STATIC) || modifieres.contains(MemberModifier.PRIVATE) || modifieres.contains(MemberModifier.PROTECTED)) {
          ctx.log.warn("The magic method __call() must have public visibility and cannot be static")
          _activeModifiers = modifieres - MemberModifier.STATIC - MemberModifier.PRIVATE - MemberModifier.PROTECTED
        }
      case "__get" =>
        if (parameterDecls.size != 1)
          throw new FatalErrorJbjException("Method %s::__get() must take exactly 1 argument".format(pClass.name.toString))
      case "__set" =>
        if (parameterDecls.size != 2)
          throw new FatalErrorJbjException("Method %s::__set() must take exactly 2 arguments".format(pClass.name.toString))
      case _ =>
    }
  }

  override def visit[R](visitor: NodeVisitor[R]) = visitor(this).thenChildren(parameterDecls).thenChildren(stmts.getOrElse(Nil))
}
