/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.runtime.value

import de.leanovate.jbj.runtime.context.Context
import de.leanovate.jbj.runtime.types.PParam
import de.leanovate.jbj.runtime.Reference

class PVar(private var current: Option[PConcreteVal] = None) extends PAny with Reference {
  current.foreach(_.retain())

  private var _refCount = 0

  def refCount = _refCount

  def toOutput(implicit ctx: Context): String = current.map(_.toOutput).getOrElse("")

  def value = current.getOrElse(NullVal)

  def value_=(v: PVal)(implicit ctx: Context): PAny = {
    v.retain()
    current.foreach(_.release())
    current = Some(v.concrete)
    this
  }

  def unset()(implicit ctx: Context) {
    current.foreach(_.release())
    current = None
  }

  override def retain() {
    _refCount += 1
  }

  override def release()(implicit ctx: Context) {
    _refCount -= 1
    if (_refCount < 1) {
      current.foreach(_.release())
      current = None
    }
  }

  override def asVal = value

  def asLazyVal = new LazyVal {
    def value = current.getOrElse(NullVal)
  }

  override def asVar = this

  override def isCallable(implicit ctx: Context) = asVal.isCallable

  override def call(params: List[PParam])(implicit ctx: Context) = asVal.call(params)

  override def toString: String = {
    val builder = new StringBuilder("PVar@")
    builder.append(hashCode())
    builder.append("(")
    builder.append(value)
    builder.append(", ")
    builder.append(refCount)
    builder.append(")")
    builder.result()
  }

  override def assign(pAny: PAny)(implicit ctx: Context) = {
    value_=(pAny.asVal)
    this
  }

  override def byVar = this

  override def byVal = value

  override def isDefined = current.isDefined

  override def isConstant = false
}

object PVar {
  def apply(): PVar = new PVar(None)

  def apply(v: PVal): PVar = new PVar(Some(v.concrete))

  def apply(optVal: Option[PAny]) = new PVar(optVal.map(_.asVal.concrete))
}
