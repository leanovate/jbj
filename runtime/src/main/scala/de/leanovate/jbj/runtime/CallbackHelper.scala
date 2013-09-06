/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.runtime

import de.leanovate.jbj.runtime.value._
import de.leanovate.jbj.runtime.context.Context

object CallbackHelper {
  def isValidCallback(callable: PVal)(implicit ctx: Context): Boolean =
    callable match {
      case array: ArrayVal if array.keyValues.size != 2 => false
      case array: ArrayVal =>
        val objOrClassName = array.keyValues.head._2.asVal
        val methodName = array.keyValues.last._2.asVal.toStr.asString
        objOrClassName match {
          case obj: ObjectVal =>
            val optMethod = if (methodName.contains("::")) {
              val classAndMethod = methodName.split("::")
              ctx.global.findClass(NamespaceName(classAndMethod(0)), autoload = false).flatMap {
                pClass =>
                  pClass.findMethod(classAndMethod(1))
              }
            } else {
              obj.pClass.findMethod(methodName)
            }
            optMethod.isDefined
          case name =>
            ctx.global.findClass(NamespaceName(name.toStr.asString), autoload = false).exists {
              pClass =>
                pClass.findMethod(methodName).isDefined
            }
        }
      case name =>
        val functionName = name.toStr.asString
        ctx.findFunction(NamespaceName(functionName)).isDefined
    }

  def callbackName(callable: PVal)(implicit ctx: Context): Option[String] =
    callable match {
      case array: ArrayVal if array.keyValues.size != 2 => None
      case array: ArrayVal =>
        val objOrClassName = array.keyValues.head._2.asVal
        val methodName = array.keyValues.last._2.asVal.toStr.asString
        objOrClassName match {
          case obj: ObjectVal =>
            val optMethod = if (methodName.contains("::")) {
              val classAndMethod = methodName.split("::")
              ctx.global.findClass(NamespaceName(classAndMethod(0)), autoload = false).flatMap {
                pClass =>
                  pClass.findMethod(classAndMethod(1))
              }
            } else {
              obj.pClass.findMethod(methodName)
            }
            optMethod.map(_ => methodName)
          case name =>
            ctx.global.findClass(NamespaceName(name.toStr.asString), autoload = false).flatMap {
              pClass =>
                pClass.findMethod(methodName).map(_ => methodName)
            }
        }
      case name =>
        val functionName = name.toStr.asString
        ctx.findFunction(NamespaceName(functionName)).map(_ => functionName)
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
            val optMethod = if (methodName.contains("::")) {
              val classAndMethod = methodName.split("::")
              ctx.global.findClass(NamespaceName(classAndMethod(0)), autoload = false).flatMap {
                pClass =>
                  pClass.findMethod(classAndMethod(1))
              }
            } else {
              obj.pClass.findMethod(methodName)
            }
            optMethod.map {
              method =>
                method.invoke(ctx, obj, parameters.map(PValParam.apply).toList)
            }.getOrElse {
              NullVal
            }
          case name =>
            ctx.global.findClass(NamespaceName(name.toStr.asString), autoload = false).map {
              pClass =>
                pClass.findMethod(methodName).map {
                  method =>
                    method.invokeStatic(ctx, parameters.map(PValParam.apply).toList)
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
            func.call(parameters.map(PValParam.apply).toList)
        }.getOrElse {
          NullVal
        }
    }
}
