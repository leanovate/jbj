/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.runtime

import de.leanovate.jbj.runtime.value.{ObjectVal, PAny, PVal}
import de.leanovate.jbj.runtime.context.Context

trait PArrayAccess {
  def offsetExists(idx: PVal)(implicit ctx: Context): Boolean

  def offsetGet(idx: PVal)(implicit ctx: Context): PAny

  def offsetSet(idx: PVal, value: PVal)(implicit ctx: Context)

  def offsetUnset(idx: PVal)(implicit ctx: Context)
}

object PArrayAccess extends PInterface {
  def name = NamespaceName(relative = false, "ArrayAccess")

  def interfaces = List.empty

  def methods = Seq(
    PInterfaceMethod("offsetExists", this),
    PInterfaceMethod("offsetGet", this),
    PInterfaceMethod("offsetSet", this),
    PInterfaceMethod("offsetUnset", this)
  ).map {
    method => method.name.toLowerCase -> method
  }.toMap

  def cast(obj: ObjectVal): PArrayAccess = new PArrayAccess {
    def offsetUnset(idx: PVal)(implicit ctx: Context) {
      obj.pClass.invokeMethod(ctx, Some(obj), "offsetUnset", PValParam(idx) :: Nil)
    }

    def offsetExists(idx: PVal)(implicit ctx: Context) =
      obj.pClass.invokeMethod(ctx, Some(obj), "offsetExists", PValParam(idx) :: Nil).asVal.toBool.asBoolean


    def offsetGet(idx: PVal)(implicit ctx: Context) =
      obj.pClass.invokeMethod(ctx, Some(obj), "offsetGet", PValParam(idx) :: Nil)


    def offsetSet(idx: PVal, value: PVal)(implicit ctx: Context) {
      obj.pClass.invokeMethod(ctx, Some(obj), "offsetUnset", PValParam(idx) :: PValParam(value) :: Nil)
    }
  }
}
