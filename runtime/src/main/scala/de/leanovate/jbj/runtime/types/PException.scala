/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.runtime.types

import de.leanovate.jbj.runtime.value._
import de.leanovate.jbj.runtime.context.Context
import de.leanovate.jbj.runtime.value.IntegerVal
import de.leanovate.jbj.runtime.NamespaceName
import de.leanovate.jbj.runtime.adapter.InstanceMethod

object PException extends PClass {
  override def isAbstract = false

  override def isFinal = false

  override def name = NamespaceName(relative = false, prefixed = false, "Exception")

  override def superClass = None

  override def interfaces = Set.empty

  override def classConstants: Map[String, PVal] = Map.empty

  override def initializeStatic(staticContext: ObjectVal)(implicit ctx: Context) {}

  override def initializeInstance(instance: ObjectVal)(implicit ctx: Context) {
    instance.definePublicProperty("message", StringVal(Array.emptyByteArray))
    instance.definePublicProperty("code", IntegerVal(0))
    instance.definePublicProperty("previous", NullVal)
    instance.definePublicProperty("file", StringVal(ctx.currentPosition.fileName))
    instance.definePublicProperty("line", IntegerVal(ctx.currentPosition.line))
  }

  override def newInstance(parameters: List[PParam])(implicit ctx: Context) = {
    val instance = newEmptyInstance(this)

    initializeInstance(instance)
    parameters.map(_.byVal) match {
      case msg :: Nil =>
        instance.definePublicProperty("message", msg.toStr)
      case msg :: c :: Nil => (msg.toStr, c.toInteger, NullVal)
        instance.definePublicProperty("message", msg.toStr)
        instance.definePublicProperty("code", c.toInteger)
      case msg :: c :: prev :: tail => (msg.toStr, c.toInteger, prev)
        instance.definePublicProperty("message", msg.toStr)
        instance.definePublicProperty("code", c.toInteger)
        instance.definePublicProperty("previous", prev)
      case _ =>
    }
    instance
  }

  override def destructInstance(instance: ObjectVal)(implicit ctx: Context) {}

  override def properties = Map.empty

  override def methods = Seq(
    new InstanceMethod(this, "getMessage") {
      def invoke(instance: ObjectVal, parameters: List[PParam])(implicit callerCtx: Context) = {
        if (parameters.length > 0) {
          callerCtx.log.warn("Exception::getMessage() expects exactly 0 parameters, %d given".format(parameters.length))
          NullVal
        } else {
          instance.getProperty("message", None).getOrElse(NullVal)
        }
      }
    },
    new InstanceMethod(this, "getLine") {
      def invoke(instance: ObjectVal, parameters: List[PParam])(implicit callerCtx: Context) = {
        if (parameters.length > 0) {
          callerCtx.log.warn("Exception::getLine() expects exactly 0 parameters, %d given".format(parameters.length))
          NullVal
        } else {
          instance.getProperty("line", None).getOrElse(NullVal)
        }
      }
    }
  ).map {
    method => method.name.toLowerCase -> method
  }.toMap
}
