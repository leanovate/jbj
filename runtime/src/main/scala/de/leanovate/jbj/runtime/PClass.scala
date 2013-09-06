/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.runtime

import de.leanovate.jbj.runtime.value._
import de.leanovate.jbj.runtime.exception.FatalErrorJbjException
import scala.annotation.tailrec
import de.leanovate.jbj.runtime.context._
import de.leanovate.jbj.runtime.context.MethodContext
import scala.Some

trait PClass {
  def isAbstract: Boolean

  def isFinal: Boolean

  def name: NamespaceName

  def superClass: Option[PClass]

  def interfaces: Set[PInterface]

  def classConstants: Map[String, ConstVal]

  def initializeStatic(staticContext: ObjectVal)(implicit ctx: Context)

  def newEmptyInstance(pClass: PClass)(implicit ctx: Context): ObjectVal = ObjectVal(pClass)

  def initializeInstance(instance: ObjectVal)(implicit ctx: Context) {}

  def newInstance(parameters: List[PParam])(implicit ctx: Context): ObjectVal

  def destructInstance(instance: ObjectVal)(implicit ctx: Context)

  def isCallable(ctx: Context, optInstance: Option[ObjectVal], methodName: String): Boolean = {
    findMethod(methodName) match {
      case Some(method) => true
      case None if optInstance.isDefined && (!ctx.isInstanceOf[MethodContext] ||
        ctx.asInstanceOf[MethodContext].pMethod.name != "__call" ||
        ctx.asInstanceOf[MethodContext].instance.pClass != this) => true
      case None => false
    }
  }

  def invokeMethod(ctx: Context, optInstance: Option[ObjectVal], methodName: String,
                   parameters: List[PParam]): PAny = {
    findMethod(methodName) match {
      case Some(method) =>
        optInstance.map {
          instance =>
            method.invoke(ctx, instance, parameters)
        }.getOrElse {
          method.invokeStatic(ctx, parameters)
        }
      case None if optInstance.isDefined && (!ctx.isInstanceOf[MethodContext] ||
        ctx.asInstanceOf[MethodContext].pMethod.name != "__call" ||
        ctx.asInstanceOf[MethodContext].instance.pClass != this) =>
        optInstance.get.pClass.findMethod("__call") match {
          case Some(method) =>
            val parameterArray = ArrayVal(parameters.map {
              param =>
                None -> param.byVal.copy
            }: _*)(ctx)
            if (method.isStatic)
              method.invokeStatic(ctx, PValParam(StringVal(methodName)(ctx))(ctx) :: PValParam(parameterArray)(ctx) :: Nil)
            else
              method.invoke(ctx, optInstance.get,
                PValParam(StringVal(methodName)(ctx))(ctx) :: PValParam(parameterArray)(ctx) :: Nil)
          case None =>
            throw new FatalErrorJbjException("Call to undefined method %s::%s()".format(name.toString, methodName))(ctx)

        }
      case None =>
        throw new FatalErrorJbjException("Call to undefined method %s::%s()".format(name.toString, methodName))(ctx)
    }
  }

  def properties: Map[String, PProperty]

  def methods: Map[String, PMethod]

  def findMethod(methodName: String): Option[PMethod] = methods.get(methodName.toLowerCase)

  @tailrec
  final def isAssignableFrom(other: PClass): Boolean = this == other || (other.superClass match {
    case None => false
    case Some(s) => isAssignableFrom(s)
  })
}