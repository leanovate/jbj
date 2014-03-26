/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.buildins.functions

import de.leanovate.jbj.runtime.value._
import de.leanovate.jbj.runtime.annotations.{ParameterMode, GlobalFunction}
import de.leanovate.jbj.runtime.context.{StaticMethodContext, MethodContext, Context}
import de.leanovate.jbj.runtime.NamespaceName
import de.leanovate.jbj.runtime.adapter.GlobalFunctions

trait ClassFunctions {
  @GlobalFunction(parameterMode = ParameterMode.STRICT_WARN, warnResult = NullVal)
  def class_exists(name: String, autoload: Option[Boolean])(implicit ctx: Context): Boolean = {
    ctx.global.findClass(NamespaceName(name), autoload.getOrElse(true)).isDefined
  }

  @GlobalFunction(parameterMode = ParameterMode.STRICT_WARN, warnResult = NullVal)
  def interface_exists(name: String, autoload: Option[Boolean])(implicit ctx: Context): Boolean = {
    ctx.global.findInterface(NamespaceName(name), autoload.getOrElse(true)).isDefined
  }

  @GlobalFunction
  def get_declared_classes()(implicit ctx: Context): PVal = {
    ArrayVal(ctx.global.declaredClasses.map {
      pClass =>
        None -> StringVal(pClass.name.toString)
    }: _*)
  }

  @GlobalFunction
  def get_class(value: Option[PVal])(implicit ctx: Context): PVal = value match {
    case Some(obj: ObjectVal) => StringVal(obj.pClass.name.toString)
    case Some(_) =>
      ctx.log.warn("get_class() expects parameter 1 to be object, string given")
      BooleanVal.FALSE
    case None =>
      ctx match {
        case MethodContext(_, pMethod, _) => StringVal(pMethod.implementingClass.name.toString)
        case StaticMethodContext(pMethod, _, _, _) => StringVal(pMethod.implementingClass.name.toString)
        case _ =>
          ctx.log.warn("get_class() called without object from outside a class")
          BooleanVal.FALSE
      }
  }

  @GlobalFunction
  def get_class_methods(name: String)(implicit ctx: Context): PVal = {
    ctx.global.findClass(NamespaceName(name), autoload = true).map {
      pClass =>
        ArrayVal(pClass.methods.values.map {
          method => None -> StringVal(method.name)
        }.toSeq: _*)
    }.getOrElse(NullVal)
  }

  @GlobalFunction
  def is_a(value: PVal, name: String)(implicit ctx: Context): Boolean = value match {
    case obj: ObjectVal =>
      ctx.global.findInterfaceOrClass(NamespaceName(name), autoload = false).exists {
        case Left(pInterface) =>
          pInterface.isAssignableFrom(obj.pClass)
        case Right(pClass) =>
          pClass.isAssignableFrom(obj.pClass)
      }
    case _ =>
      false
  }

  @GlobalFunction
  def is_subclass_of(value: PVal, name: String)(implicit ctx: Context): Boolean = value match {
    case obj: ObjectVal =>
      obj.pClass.superClass.flatMap {
        superClass =>
          ctx.global.findClass(NamespaceName(name), autoload = true).map {
            pClass =>
              pClass.isAssignableFrom(superClass)
          }
      }.getOrElse(false)
    case _ => false
  }

  @GlobalFunction
  def get_parent_class(value: Option[PVal])(implicit ctx: Context): PVal = value match {
    case Some(obj: ObjectVal) =>
      obj.pClass.superClass.map {
        superClass => StringVal(superClass.name.toString)
      }.getOrElse(BooleanVal.FALSE)
    case Some(name) =>
      ctx.global.findClass(NamespaceName(name.toStr.asString), autoload = true).flatMap {
        pClass =>
          pClass.superClass.map {
            superClass => StringVal(superClass.name.toString)
          }
      }.getOrElse(BooleanVal.FALSE)
    case None =>
      ctx match {
        case MethodContext(_, pMethod, _) =>
          pMethod.implementingClass.superClass.map {
            superClass => StringVal(superClass.name.toString)
          }.getOrElse(BooleanVal.FALSE)
        case StaticMethodContext(pMethod, _, _, _) =>
          pMethod.implementingClass.superClass.map {
            superClass => StringVal(superClass.name.toString)
          }.getOrElse(BooleanVal.FALSE)
        case _ =>
          ctx.log.warn("get_parent_class() called without object from outside a class")
          BooleanVal.FALSE
      }
  }

  @GlobalFunction(parameterMode = ParameterMode.EXACTLY_WARN, warnResult = NullVal)
  def property_exists(value: PVal, name: String)(implicit ctx: Context): PVal = {
    value.concrete match {
      case obj: ObjectVal =>
        BooleanVal(obj.pClass.properties.contains(name))
      case StringVal(className) =>
        ctx.global.findClass(NamespaceName(className), autoload = false).map {
          pClass =>
            BooleanVal(pClass.properties.contains(name))
        }.getOrElse(BooleanVal.FALSE)
      case _ =>
        ctx.log.warn("First parameter must either be an object or the name of an existing class")
        NullVal
    }
  }
}

object ClassFunctions extends ClassFunctions {
  val functions = GlobalFunctions.generatePFunctions[ClassFunctions]
}