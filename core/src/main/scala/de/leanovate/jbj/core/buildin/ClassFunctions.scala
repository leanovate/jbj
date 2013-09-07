/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.core.buildin

import de.leanovate.jbj.runtime.value._
import de.leanovate.jbj.runtime.annotations.GlobalFunction
import de.leanovate.jbj.runtime.context.Context
import de.leanovate.jbj.runtime.{NodePosition, NamespaceName}

object ClassFunctions extends WrappedFunctions {

  @GlobalFunction
  def get_class(value: PVal)(implicit ctx: Context): PVal = value match {
    case obj: ObjectVal => StringVal(obj.pClass.name.toString)
    case _ =>
      ctx.log.warn("get_class() expects parameter 1 to be object, string given")
      BooleanVal.FALSE
  }

  @GlobalFunction
  def class_exists(name: String, autoload: Option[Boolean])(implicit ctx: Context): Boolean = {
    ctx.global.findClass(NamespaceName(name), autoload.getOrElse(true)).isDefined
  }

  @GlobalFunction
  def interface_exists(name: String, autoload: Option[Boolean])(implicit ctx: Context): Boolean = {
    ctx.global.findInterface(NamespaceName(name), autoload.getOrElse(true)).isDefined
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
  def get_parent_class(value: PVal)(implicit ctx: Context): PVal = value match {
    case obj: ObjectVal =>
      obj.pClass.superClass.map {
        superClass => StringVal(superClass.name.toString)
      }.getOrElse(BooleanVal.FALSE)
    case name =>
      ctx.global.findClass(NamespaceName(name.toStr.asString), autoload = true).flatMap {
        pClass =>
          pClass.superClass.map {
            superClass => StringVal(superClass.name.toString)
          }
      }.getOrElse(BooleanVal.FALSE)
  }
}
