package de.leanovate.jbj.runtime.buildin

import de.leanovate.jbj.ast.{Expr, NamespaceName, NodePosition}
import de.leanovate.jbj.runtime.value._
import de.leanovate.jbj.runtime.Context
import de.leanovate.jbj.runtime.annotations.GlobalFunction

object FunctionFunctions extends WrappedFunctions {
  @GlobalFunction
  def call_user_func(callable: PVal, parameters: Expr*)(implicit ctx: Context,
                                                          position: NodePosition): PAny = callable match {
    case array: ArrayVal if array.keyValues.size != 2 =>
      ctx.log.warn(position, "call_user_func() expects parameter 1 to be a valid callback, array must have exactly two members")
      NullVal
    case array: ArrayVal =>
      val objOrClassName = array.keyValues.head._2.value
      val methodName = array.keyValues.last._2.value.toStr.asString
      objOrClassName match {
        case obj: ObjectVal =>
          obj.pClass.findMethod(methodName).map {
            method =>
              method.invoke(ctx, position, obj, parameters.toList)
          }.getOrElse {
            ctx.log.warn(position, "call_user_func() expects parameter 1 to be a valid callback, class '%s' does not have a method '%s'".format(obj.pClass.name.toString, methodName))
            NullVal
          }
        case name =>
          ctx.global.findClass(NamespaceName(name.toStr.asString)).map {
            pClass =>
              pClass.findMethod(methodName).map {
                method =>
                  method.invokeStatic(ctx, position, pClass, parameters.toList)
              }.getOrElse {
                ctx.log.warn(position, "call_user_func() expects parameter 1 to be a valid callback, class '%s' does not have a method '%s'".format(pClass.name.toString, methodName))
                NullVal
              }
          }.getOrElse {
            ctx.log.warn(position, "call_user_func() expects parameter 1 to be a valid callback, first array member is not a valid class name or object")
            NullVal
          }
      }
    case name =>
      val functionName = name.toStr.asString
      ctx.findFunction(NamespaceName(functionName)).map {
        func =>
          func.call(parameters.toList)
      }.getOrElse {
        ctx.log.warn(position, "call_user_func() expects parameter 1 to be a valid callback, function '%s' not found or invalid function name".format(functionName))
        NullVal
      }
  }
}
