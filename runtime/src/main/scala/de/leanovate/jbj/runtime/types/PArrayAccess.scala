/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.runtime.types

import de.leanovate.jbj.runtime.value.{ObjectVal, PAny, PVal}
import de.leanovate.jbj.runtime.context.Context
import de.leanovate.jbj.runtime.NamespaceName

trait PArrayAccess {
  def obj: ObjectVal

  def offsetExists(idx: PVal)(implicit ctx: Context): Boolean

  def offsetGet(idx: PVal)(implicit ctx: Context): PAny

  def offsetSet(idx: PVal, value: PVal)(implicit ctx: Context): PAny

  def offsetUnset(idx: PVal)(implicit ctx: Context)
}

object PArrayAccess extends PInterface {
  override def name = NamespaceName(relative = false, "ArrayAccess")

  override def interfaces = List.empty

  override def declaredConstants = Map.empty

  override def methods = Seq(
    PInterfaceMethod(this, "offsetExists"),
    PInterfaceMethod(this, "offsetGet"),
    PInterfaceMethod(this, "offsetSet"),
    PInterfaceMethod(this, "offsetUnset")
  ).map {
    method => method.name.toLowerCase -> method
  }.toMap

  def cast(_obj: ObjectVal): PArrayAccess = new PArrayAccess {
    def obj = _obj

    def offsetUnset(idx: PVal)(implicit ctx: Context) {
      _obj.pClass.invokeMethod(Some(obj), "offsetUnset", PValParam(idx) :: Nil)
    }

    def offsetExists(idx: PVal)(implicit ctx: Context) =
      _obj.pClass.invokeMethod(Some(obj), "offsetExists", PValParam(idx) :: Nil).asVal.toBool.asBoolean


    def offsetGet(idx: PVal)(implicit ctx: Context) =
      _obj.pClass.invokeMethod(Some(obj), "offsetGet", PValParam(idx) :: Nil)


    def offsetSet(idx: PVal, value: PVal)(implicit ctx: Context) = {
      _obj.pClass.invokeMethod(Some(obj), "offsetSet", PValParam(idx) :: PValParam(value) :: Nil)
    }
  }
}
