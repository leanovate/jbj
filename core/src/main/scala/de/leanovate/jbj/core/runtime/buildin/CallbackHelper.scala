package de.leanovate.jbj.core.runtime.buildin

import de.leanovate.jbj.core.runtime.value._
import de.leanovate.jbj.core.runtime.context.Context
import de.leanovate.jbj.core.ast.NamespaceName
import de.leanovate.jbj.core.ast.expr.value.ScalarExpr

object CallbackHelper {
  def isValidCallback(callable: PVal)(implicit ctx: Context): Boolean =
    callable match {
      case array: ArrayVal if array.keyValues.size != 2 => false
      case array: ArrayVal =>
        val objOrClassName = array.keyValues.head._2.asVal
        val methodName = array.keyValues.last._2.asVal.toStr.asString
        objOrClassName match {
          case obj: ObjectVal =>
            obj.pClass.findMethod(methodName).isDefined
          case name =>
            ctx.global.findClass(NamespaceName(name.toStr.asString)).exists {
              pClass =>
                pClass.findMethod(methodName).isDefined
            }
        }
      case name =>
        val functionName = name.toStr.asString
        ctx.findFunction(NamespaceName(functionName)).isDefined
    }

  def callCallabck(callable: PVal, parameters: PVal*)(implicit ctx: Context): PAny =
    callable match {
      case array: ArrayVal if array.keyValues.size != 2 =>
        NullVal
      case array: ArrayVal =>
        val objOrClassName = array.keyValues.head._2.asVal
        val methodName = array.keyValues.last._2.asVal.toStr.asString
        objOrClassName match {
          case obj: ObjectVal =>
            obj.pClass.findMethod(methodName).map {
              method =>
                method.invoke(ctx, obj, parameters.map(ScalarExpr.apply).toList)
            }.getOrElse {
              NullVal
            }
          case name =>
            ctx.global.findClass(NamespaceName(name.toStr.asString)).map {
              pClass =>
                pClass.findMethod(methodName).map {
                  method =>
                    method.invokeStatic(ctx, parameters.map(ScalarExpr.apply).toList)
                }.getOrElse {
                  NullVal
                }
            }.getOrElse {
              NullVal
            }
        }
      case name =>
        val functionName = name.toStr.asString
        ctx.findFunction(NamespaceName(functionName)).map {
          func =>
            func.call(parameters.map(ScalarExpr.apply).toList)
        }.getOrElse {
          NullVal
        }
    }
}
