/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.core.runtime.buildin

import de.leanovate.jbj.core.runtime.value._
import de.leanovate.jbj.core.ast.{NamespaceName, NodePosition}
import de.leanovate.jbj.core.runtime.annotations.GlobalFunction
import de.leanovate.jbj.core.runtime.context.Context

object ClassFunctions extends WrappedFunctions {

  @GlobalFunction
  def get_class(value: PVal)(implicit ctx: Context, position: NodePosition): PVal = value match {
    case obj: ObjectVal => StringVal(obj.pClass.name.toString)
    case _ =>
      ctx.log.warn("get_class() expects parameter 1 to be object, string given")
      BooleanVal.FALSE
  }

  @GlobalFunction
  def class_exists(name: String)(implicit ctx: Context, position: NodePosition): Boolean = {
    ctx.global.findClassOrAutoload(NamespaceName(name)).isDefined
  }

  @GlobalFunction
  def get_class_methods(name: String)(implicit ctx: Context, position: NodePosition): PVal = {
    ctx.global.findClassOrAutoload(NamespaceName(name)).map {
      pClass =>
        ArrayVal(pClass.methods.values.map {
          method => None -> StringVal(method.name)
        }.toSeq: _*)
    }.getOrElse(NullVal)
  }

  @GlobalFunction
  def is_subclass_of(value: PVal, name: String)(implicit ctx: Context, position: NodePosition): Boolean = value match {
    case obj: ObjectVal =>
      obj.pClass.superClass.flatMap {
        superClass =>
          ctx.global.findClassOrAutoload(NamespaceName(name)).map {
            pClass =>
              pClass.isAssignableFrom(superClass)
          }
      }.getOrElse(false)
    case _ => false
  }

  @GlobalFunction
  def get_parent_class(value: PVal)(implicit ctx: Context, position: NodePosition): PVal = value match {
    case obj: ObjectVal =>
      obj.pClass.superClass.map {
        superClass => StringVal(superClass.name.toString)
      }.getOrElse(BooleanVal.FALSE)
    case name =>
      ctx.global.findClassOrAutoload(NamespaceName(name.toStr.asString)).flatMap {
        pClass =>
          pClass.superClass.map {
            superClass => StringVal(superClass.name.toString)
          }
      }.getOrElse(BooleanVal.FALSE)
  }
}
