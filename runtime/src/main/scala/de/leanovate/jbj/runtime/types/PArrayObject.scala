/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.runtime.types

import de.leanovate.jbj.runtime.NamespaceName
import de.leanovate.jbj.runtime.value._
import de.leanovate.jbj.runtime.context.Context
import de.leanovate.jbj.runtime.adapter.InstanceMethod

object PArrayObject extends PClass {
  override def isAbstract = false

  override def isFinal = false

  override def name = NamespaceName(relative = false, "ArrayObject")

  override def superClass = None

  override def interfaces = Set(PArrayAccess)

  override def classConstants: Map[String, ConstVal] = Map.empty

  override def initializeStatic(staticContext: ObjectVal)(implicit ctx: Context) {}

  override def initializeInstance(instance: ObjectVal)(implicit ctx: Context) {
    instance.definePrivateProperty("storage", name.toString, StringVal(Array.emptyByteArray))
  }

  override def newInstance(parameters: List[PParam])(implicit ctx: Context) =
    new StdObjectVal(this, ctx.global.instanceCounter.incrementAndGet(), new ExtendedLinkedHashMap[ObjectPropertyKey.Key])

  override def destructInstance(instance: ObjectVal)(implicit ctx: Context) {}

  override def properties = Map.empty

  override def methods = Seq(
    new InstanceMethod(this, "offsetExists") {
      def invoke(instance: ObjectVal, parameters: List[PParam])(implicit callerCtx: Context) = {
        if (parameters.length != 1) {
          callerCtx.log.warn("PArrayObject::offsetExists() expects exactly 1 parameter, %d given".format(parameters.length))
          NullVal
        } else {
          instance.getProperty("storage", Some(name.toString)).map {
            case array: ArrayVal =>
              BooleanVal(array.getAt(parameters(0).byVal).isDefined)
            case _ =>
              BooleanVal.FALSE
          }.getOrElse(BooleanVal.FALSE)
        }
      }
    },
    new InstanceMethod(this, "offsetGet") {
      def invoke(instance: ObjectVal, parameters: List[PParam])(implicit callerCtx: Context) = {
        if (parameters.length != 1) {
          callerCtx.log.warn("PArrayObject::offsetGet() expects exactly 1 parameter, %d given".format(parameters.length))
          NullVal
        } else {
          instance.getProperty("storage", Some(name.toString)).map {
            case array: ArrayVal =>
              array.getAt(parameters(0).byVal).getOrElse(NullVal)
            case _ =>
              NullVal
          }.getOrElse(NullVal)
        }
      }
    },
    new InstanceMethod(this, "offsetSet") {
      def invoke(instance: ObjectVal, parameters: List[PParam])(implicit callerCtx: Context) = {
        if (parameters.length != 2) {
          callerCtx.log.warn("PArrayObject::offsetGet() expects exactly 2 parameters, %d given".format(parameters.length))
          NullVal
        } else {
          instance.getProperty("storage", Some(name.toString)).map {
            case array: ArrayVal =>
              array.setAt(parameters(0).byVal, parameters(1).byVal)
              parameters(1).byVal
            case _ =>
              NullVal
          }.getOrElse(NullVal)
        }
      }
    },
    new InstanceMethod(this, "offsetUnset") {
      def invoke(instance: ObjectVal, parameters: List[PParam])(implicit callerCtx: Context) = {
        if (parameters.length != 1) {
          callerCtx.log.warn("PArrayObject::offsetGet() expects exactly 1 parameter, %d given".format(parameters.length))
        } else {
          instance.getProperty("storage", Some(name.toString)).foreach {
            case array: ArrayVal =>
              array.unsetAt(parameters(0).byVal)
            case _ =>
          }
        }
        NullVal
      }
    }
  ).map {
    method => method.name.toLowerCase -> method
  }.toMap
}
