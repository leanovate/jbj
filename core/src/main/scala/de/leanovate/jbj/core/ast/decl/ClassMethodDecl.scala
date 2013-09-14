/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.core.ast.decl

import de.leanovate.jbj.core.ast._
import de.leanovate.jbj.runtime.value._
import de.leanovate.jbj.runtime.context.{GlobalContext, Context, MethodContext, StaticMethodContext}
import de.leanovate.jbj.runtime.exception.FatalErrorJbjException
import de.leanovate.jbj.core.ast.stmt.FunctionLike
import de.leanovate.jbj.runtime.types.{PStdClass, PParam, PMethod, PClass}

case class ClassMethodDecl(modifieres: Set[MemberModifier.Type], name: String, returnByRef: Boolean, parameterDecls: List[ParameterDecl],
                           stmts: Option[List[Stmt]]) extends ClassMemberDecl with PMethod with BlockLike with FunctionLike {
  private var _declaringClass: PClass = PStdClass
  private var _activeModifiers: Set[MemberModifier.Type] = modifieres

  private lazy val staticInitializers = StaticInitializer.collect(stmts.getOrElse(Nil): _*)

  lazy val isPrivate = activeModifieres.contains(MemberModifier.PRIVATE)

  lazy val isProtected = activeModifieres.contains(MemberModifier.PROTECTED)

  lazy val isStatic = activeModifieres.contains(MemberModifier.STATIC)

  lazy val isAbstract = activeModifieres.contains(MemberModifier.ABSTRACT)

  lazy val isFinal = activeModifieres.contains(MemberModifier.FINAL)

  override def declaringClass = _declaringClass

  override def parameters = parameterDecls.toSeq

  protected[decl] def declaringClass_=(pClass: PClass) {
    _declaringClass = pClass
  }

  def activeModifieres = _activeModifiers

  override def invoke(instance: ObjectVal, parameters: List[PParam])(implicit callerCtx: Context): PAny = {
    if (isAbstract) {
      throw new FatalErrorJbjException("Cannot call abstract method %s::%s()".format(declaringClass.name.toString, name))
    }
    if (isPrivate) {
      callerCtx match {
        case global: GlobalContext if global.inShutdown =>
          global.log.warn("Call to private %s::%s() from context '%s' during shutdown ignored".format(instance.pClass.name.toString, name, callerCtx.name))
          return NullVal
        case MethodContext(_, pMethod, _) if declaringClass == pMethod.declaringClass =>
        case MethodContext(_, _, _) if name == "__construct" =>
          throw new FatalErrorJbjException("Cannot call private %s::%s()".format(declaringClass.name.toString, name))
        case StaticMethodContext(pMethod, _) if declaringClass == pMethod.declaringClass =>
        case _ =>
          if (name == "__construct")
            throw new FatalErrorJbjException("Call to private %s::%s() from invalid context".format(declaringClass.name.toString, name))
          else if (name.startsWith("__"))
            throw new FatalErrorJbjException("Call to private %s::%s() from context '%s'".format(instance.pClass.name.toString, name, callerCtx.name))
          else
            throw new FatalErrorJbjException("Call to private method %s::%s() from context '%s'".format(declaringClass.name.toString, name, callerCtx.name))
      }
    }
    if (isProtected) {
      callerCtx match {
        case global: GlobalContext if global.inShutdown =>
          global.log.warn("Call to protected %s::%s() from context '%s' during shutdown ignored".format(declaringClass.name.toString, name, callerCtx.name))
          return NullVal
        case MethodContext(_, pMethod, _) if declaringClass.isAssignableFrom(pMethod.declaringClass) =>
        case StaticMethodContext(pMethod, _) if declaringClass.isAssignableFrom(pMethod.declaringClass) =>
        case _ =>
          if (name == "__construct")
            throw new FatalErrorJbjException("Call to protected %s::%s() from invalid context".format(declaringClass.name.toString, name))
          else if (name.startsWith("__"))
            throw new FatalErrorJbjException("Call to protected %s::%s() from context '%s'".format(declaringClass.name.toString, name, callerCtx.name))
          else
            throw new FatalErrorJbjException("Call to protected method %s::%s() from context '%s'".format(declaringClass.name.toString, name, callerCtx.name))
      }
    }

    implicit val methodCtx = MethodContext(instance, this, callerCtx)

    methodCtx.currentPosition = position
    if (!methodCtx.static.initialized) {
      staticInitializers.foreach(_.initializeStatic(methodCtx.static))
      methodCtx.static.initialized = true
    }

    setParameters(methodCtx, callerCtx, parameters)
    perform(methodCtx, returnByRef, stmts.getOrElse(Nil))
  }

  override def invokeStatic(parameters: List[PParam])(implicit callerCtx: Context) = {
    if (isPrivate) {
      callerCtx match {
        case global: GlobalContext if global.inShutdown =>
          global.log.warn("Call to private %s::%s() from context '%s' during shutdown ignored".format(declaringClass.name.toString, name, callerCtx.name))
        case MethodContext(_, pMethod, _) if declaringClass == pMethod.declaringClass =>
        case StaticMethodContext(pMethod, _) if declaringClass == pMethod.declaringClass =>
        case _ =>
          throw new FatalErrorJbjException("Call to private method %s::%s() from context '%s'".format(declaringClass.name.toString, name, callerCtx.name))
      }
    }
    if (isProtected) {
      callerCtx match {
        case global: GlobalContext if global.inShutdown =>
          global.log.warn("Call to protected %s::%s() from context '%s' during shutdown ignored".format(declaringClass.name.toString, name, callerCtx.name))
        case MethodContext(_, pMethod, _) if declaringClass.isAssignableFrom(pMethod.declaringClass) =>
        case StaticMethodContext(pMethod, _) if declaringClass.isAssignableFrom(pMethod.declaringClass) =>
        case _ =>
          throw new FatalErrorJbjException("Call to protected method %s::%s() from context '%s'".format(declaringClass.name.toString, name, callerCtx.name))
      }
    }

    implicit val methodCtx = StaticMethodContext(this, callerCtx)

    methodCtx.currentPosition = position

    if (!methodCtx.static.initialized) {
      staticInitializers.foreach(_.initializeStatic(methodCtx.static))
      methodCtx.static.initialized = true
    }

    setParameters(methodCtx, callerCtx, parameters)

    if (!isStatic)
      callerCtx.log.strict("Non-static method %s::%s() should not be called statically".format(declaringClass.name.toString, name))

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
    parameterDecls.foreach(_.check)
  }

  override def initializeClass(pClass: ClassDeclStmt)(implicit ctx: Context) {
    if (isAbstract) {
      if (isFinal) {
        throw new FatalErrorJbjException("Cannot use the final modifier on an abstract class member")
      }
      if (isStatic) {
        ctx.log.strict("Static function %s::%s() should not be abstract".format(pClass.name.toString, name))
      }
    } else {
      val parentMethod = pClass.superClass.flatMap(_.methods.get(name))
      if (!isStatic && parentMethod.exists(_.isStatic)) {
        throw new FatalErrorJbjException("Cannot make static method %s::%s() non static in class %s".format(pClass.superClass.get.name.toString, name, pClass.name.toString))
      } else if (isStatic && parentMethod.exists(!_.isStatic)) {
        throw new FatalErrorJbjException("Cannot make non static method %s::%s() static in class %s".format(pClass.superClass.get.name.toString, name, pClass.name.toString))
      } else if (parentMethod.exists(_.isFinal)) {
        throw new FatalErrorJbjException("Cannot override final method %s::%s()".format(parentMethod.get.declaringClass.name.toString, name))
      }
    }
    pClass.interfaces.foreach {
      interface =>
        interface.methods.get(name).foreach {
          otherMethod =>
            if (!isCompatibleWith(otherMethod) || !otherMethod.isCompatibleWith(this)) {
              throw new FatalErrorJbjException("Declaration of %s::%s() must be compatible with %s::%s(%s)".
                format(pClass.name.toString, name, interface.name.toString, name, otherMethod.parameters.map("$" + _.name).mkString(", ")))
            }
        }
    }
    pClass.superClass.foreach {
      superClass =>
        superClass.methods.get(name).foreach {
          otherMethod =>
            if (!isCompatibleWith(otherMethod) || !otherMethod.isCompatibleWith(this)) {
              ctx.log.strict("Declaration of %s::%s() should be compatible with %s::%s(%s)".
                format(pClass.name.toString, name, superClass.name.toString, name, otherMethod.parameters.map("$" + _.name).mkString(", ")))
            }
        }
    }

    name match {
      case "__call" =>
        if (parameterDecls.size != 2)
          throw new FatalErrorJbjException("Method %s::__call() must take exactly 2 arguments".format(pClass.name.toString))
        if (modifieres.contains(MemberModifier.STATIC) || modifieres.contains(MemberModifier.PRIVATE) || modifieres.contains(MemberModifier.PROTECTED)) {
          ctx.log.warn("The magic method __call() must have public visibility and cannot be static")
          // We just remove private and protected as this would affect execution. Actually a static __call is still supposed to work
          _activeModifiers = modifieres - MemberModifier.PRIVATE - MemberModifier.PROTECTED
        }
      case "__get" =>
        if (parameterDecls.size != 1)
          throw new FatalErrorJbjException("Method %s::__get() must take exactly 1 argument".format(pClass.name.toString))
      case "__set" =>
        if (parameterDecls.size != 2)
          throw new FatalErrorJbjException("Method %s::__set() must take exactly 2 arguments".format(pClass.name.toString))
      case _ =>
    }
    parameterDecls.foreach(_.check)
  }

  override def visit[R](visitor: NodeVisitor[R]) = visitor(this).thenChildren(parameterDecls).thenChildren(stmts.getOrElse(Nil))
}
