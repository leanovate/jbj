/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.core.ast.decl

import de.leanovate.jbj.core.ast._
import de.leanovate.jbj.runtime._
import scala.collection.mutable
import de.leanovate.jbj.runtime.value.{PVar, PVal, ConstVal, ObjectVal}
import de.leanovate.jbj.runtime.exception.FatalErrorJbjException
import de.leanovate.jbj.runtime.context._
import scala.collection.immutable.List
import de.leanovate.jbj.runtime.types._
import de.leanovate.jbj.runtime.value.ObjectPropertyKey.ProtectedKey
import de.leanovate.jbj.runtime.context.MethodContext
import de.leanovate.jbj.runtime.value.ObjectPropertyKey.PublicKey
import scala.Some
import de.leanovate.jbj.runtime.types.PProperty
import de.leanovate.jbj.runtime.context.InstanceContext
import de.leanovate.jbj.runtime.context.ClassContext
import scala.annotation.tailrec

case class ClassDeclStmt(classEntry: ClassEntry.Type, declaredName: NamespaceName,
                         superClassName: Option[NamespaceName], implements: List[NamespaceName],
                         decls: List[ClassMemberDecl])
  extends DeclStmt with PClass {

  private var _initialized = false
  private var _name = declaredName
  private var _methodsInitialized = false
  private var _staticInitialized = false
  protected[decl] val _classConstants = mutable.Map.empty[String, ConstVal]

  private var _superClass: Option[PClass] = None
  private var _interfaces: Set[PInterface] = Set.empty

  override def name = _name

  override def isAbstract = classEntry == ClassEntry.ABSTRACT_CLASS

  override def isFinal = classEntry == ClassEntry.FINAL_CLASS

  override def superClass = _superClass

  override def interfaces = _interfaces

  override def classConstants: Map[String, ConstVal] =
    superClass.map(_.classConstants).getOrElse(Map.empty) ++ _classConstants.toMap

  override def register(implicit ctx: Context) {
    initialize(autoload = false, ignoreErrors = true)
  }

  override def exec(implicit ctx: Context) = {
    if (!_initialized)
      initialize(autoload = true, ignoreErrors = false)
    SuccessExecResult
  }

  override def initializeStatic(staticClassObj: ObjectVal)(implicit ctx: Context) {
    if (!_staticInitialized) {
      _staticInitialized = true

      _superClass.foreach {
        parent =>
          val parentStaticObj = ctx.global.staticClassObject(parent)
          parentStaticObj.keyValues.foreach {
            case (PublicKey(key), value: PVal) =>
              val pVar = PVar(value)
              parentStaticObj.definePublicProperty(key, pVar)
              staticClassObj.definePublicProperty(key, pVar)
            case (PublicKey(key), value: PVar) =>
              staticClassObj.definePublicProperty(key, value)
            case (ProtectedKey(key), value: PVal) =>
              val pVar = PVar(value)
              parentStaticObj.defineProtectedProperty(key, pVar)
              staticClassObj.defineProtectedProperty(key, pVar)
            case (ProtectedKey(key), value: PVar) =>
              staticClassObj.defineProtectedProperty(key, value)
            case _ =>
          }
      }

      decls.foreach {
        decl =>
          val classCtx = ClassContext(this, ctx, decl.position)
          decl.initializeStatic(this, staticClassObj)(classCtx)
      }
    }
  }

  override def newEmptyInstance(pClass: PClass)(implicit ctx: Context): ObjectVal =
    superClass.map(_.newEmptyInstance(pClass)(ctx)).getOrElse(ObjectVal(pClass))


  override def initializeInstance(instance: ObjectVal)(implicit ctx: Context) {
    implicit val classCtx = InstanceContext(instance, this, ctx)

    decls.foreach(_.initializeInstance(instance, this))

    superClass.foreach(_.initializeInstance(instance)(ctx))
  }

  override def newInstance(parameters: List[PParam])(implicit ctx: Context) = {
    if (classEntry == ClassEntry.ABSTRACT_CLASS)
      throw new FatalErrorJbjException("Cannot instantiate abstract class %s".format(name.toString))(ctx)
    val instance = newEmptyInstance(this)(ctx)

    initializeInstance(instance)(ctx)
    findAndInvokeConstructor(this, instance, parameters)
    instance
  }

  override def destructInstance(instance: ObjectVal)(implicit ctx: Context) {
    ctx match {
      case MethodContext(inst, pMethod, _) if instance.pClass == this && pMethod.name == "__destruct" =>
      case _ =>
        findDestructor.foreach(_.invoke(instance, Nil))
        instance.cleanup()
    }
  }

  override lazy val properties: Map[String, PProperty] = {
    val result = mutable.LinkedHashMap.empty[String, PProperty]

    superClass.foreach(result ++= _.properties)
    decls.foreach {
      case varDecl: ClassVarDecl =>
        varDecl.getProperties(this).foreach {
          property =>
            result -= property.name
            result += property.name -> property
        }
      case _ =>
    }
    result.toMap
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

  @tailrec
  private def findAndInvokeConstructor(pClass: PClass, instance: ObjectVal, parameters: List[PParam])(implicit ctx: Context) {
    findConstructor(pClass) match {
      case Some(constructor) => constructor.invoke(instance, parameters)
      case None =>
        pClass.superClass match {
          case None =>
          case Some(c) => findAndInvokeConstructor(c, instance, parameters)
        }
    }
  }

  private def findConstructor(pClass: PClass): Option[PMethod] =
    pClass.findMethod(pClass.name.toString).map(Some.apply).getOrElse(pClass.findMethod("__construct"))

  private def findDestructor: Option[PMethod] = findMethod("__destruct")


  override def visit[R](visitor: NodeVisitor[R]) = visitor(this).thenChildren(decls)

  private def initialize(autoload: Boolean, ignoreErrors: Boolean)(implicit ctx: Context) {
    _name = declaredName.absolute
    if (ctx.global.findInterfaceOrClass(name, autoload = false).isDefined)
      if (ignoreErrors)
        return
      else
        throw new FatalErrorJbjException("Cannot redeclare class %s".format(name))
    else {
      if (superClassName.isDefined) {
        _superClass = ctx.global.findClass(superClassName.get, autoload)
        if (!superClass.isDefined) {
          if (ctx.global.findInterface(superClassName.get, autoload = false).isDefined) {
            if (ignoreErrors)
              return
            else
              throw new FatalErrorJbjException("Class %s cannot extend from interface %s".format(name.toString, superClassName.get))
          } else {
            if (ignoreErrors)
              return
            else
              throw new FatalErrorJbjException("Class '%s' not found".format(superClassName.get))
          }
        }
        else if (superClass.get.isFinal) {
          if (ignoreErrors)
            return
          else
            throw new FatalErrorJbjException(
              "Class %s may not inherit from final class (%s)".format(name.toString, superClassName.get.toString))
        }
      }
      val interfaceConstant = mutable.Map.empty[String, String]
      _interfaces = implements.reverse.flatMap {
        interfaceName =>
          ctx.global.findInterfaceOrClass(interfaceName, autoload) match {
            case Some(Left(interface)) =>
              val interfaces = interface :: interface.interfaces
              interfaces.foreach {
                interface =>
                  interface.declaredConstants.keys.foreach {
                    constantName =>
                      if (interfaceConstant.contains(constantName) && interfaceConstant(constantName) != interface.name.toString) {
                        if (ignoreErrors)
                          return
                        else
                          throw new FatalErrorJbjException("Cannot inherit previously-inherited or override constant %s from interface %s".
                            format(constantName, interfaceConstant(constantName)))
                      }
                      interfaceConstant(constantName) = interface.name.toString
                  }
              }
              interfaces
            case Some(Right(_)) =>
              if (ignoreErrors)
                return
              else
                throw new FatalErrorJbjException("%s cannot implement %s - it is not an interface".format(name.toString,
                  interfaceName.toString))
            case None =>
              if (ignoreErrors)
                return
              else
                throw new FatalErrorJbjException("Interface '%s' not found".format(interfaceName.toString))
          }
      }.toSet ++ _superClass.map(_.interfaces).getOrElse(List.empty)

      _interfaces.foreach {
        interface =>
          interface.initializeClass(this)
      }

      if (!_methodsInitialized) {
        decls.foreach {
          method =>
            ctx.currentPosition = method.position
            method.initializeClass(this)
        }
        ctx.currentPosition = position
        _methodsInitialized = true
      }
      if (!isAbstract) {
        val missingImplementations = _interfaces.flatMap {
          interface =>
            interface.methods.flatMap {
              case (methodName, method) if !methods.contains(methodName) =>
                Seq("%s::%s".format(interface.name.toString, method.name))
              case _ => Seq.empty
            }
        } ++ methods.values.filter(_.isAbstract).map {
          method =>
            "%s::%s".format(name.toString, method.name)
        }
        if (!missingImplementations.isEmpty) {
          if (ignoreErrors)
            return
          else
            throw new FatalErrorJbjException("Class %s contains %d abstract method and must therefore be declared abstract or implement the remaining methods (%s)".
              format(name.toString, missingImplementations.size, missingImplementations.mkString(", ")))
        }
      }

      ctx.global.defineClass(this)
    }

    _initialized = true
  }

}
