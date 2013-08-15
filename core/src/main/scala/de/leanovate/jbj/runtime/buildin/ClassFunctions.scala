package de.leanovate.jbj.runtime.buildin

import de.leanovate.jbj.runtime.value._
import de.leanovate.jbj.runtime.Context
import de.leanovate.jbj.ast.{NamespaceName, NodePosition}
import de.leanovate.jbj.runtime.annotations.GlobalFunction

object ClassFunctions extends WrappedFunctions {

  @GlobalFunction
  def get_class(value: Value)(implicit ctx: Context, position: NodePosition): Value = value match {
    case obj: ObjectVal => StringVal(obj.pClass.name.toString)
    case _ =>
      ctx.log.warn(position, "get_class() expects parameter 1 to be object, string given")
      BooleanVal.FALSE
  }

  @GlobalFunction
  def class_exists(name: String)(implicit ctx: Context, position: NodePosition): Boolean = {
    ctx.global.findClassOrAutoload(NamespaceName(name)).isDefined
  }

  @GlobalFunction
  def get_class_methods(name: String)(implicit ctx: Context, position: NodePosition): Value = {
    ctx.global.findClassOrAutoload(NamespaceName(name)).map {
      pClass =>
        ArrayVal(pClass.methods.values.map {
          method => None -> StringVal(method.name)
        }.toSeq: _*)
    }.getOrElse(NullVal)
  }

  @GlobalFunction
  def is_subclass_of(value: Value, name: String)(implicit ctx: Context, position: NodePosition): Boolean = value match {
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
  def get_parent_class(value: Value)(implicit ctx: Context, position: NodePosition): Value = value match {
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
