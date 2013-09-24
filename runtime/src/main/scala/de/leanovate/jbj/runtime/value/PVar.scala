/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.runtime.value

import de.leanovate.jbj.runtime.context.Context

class PVar(private var current: Option[PConcreteVal] = None) extends PAny {
  current.foreach(_.retain())

  private var _refCount = 0

  def refCount = _refCount

  def toOutput(implicit ctx: Context): String = current.map(_.toOutput).getOrElse("")

  protected def set(pVal: Option[PConcreteVal]) {
    current = pVal
  }

  def value = current.getOrElse(NullVal)

  def value_=(v: PVal)(implicit ctx: Context) {
    v.retain()
    current.foreach(_.release())
    current = Some(v.concrete)
  }

  def unset(implicit ctx: Context) {
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

  override def foreachByVal[R](f: (PVal, PAny) => Option[R], fixedEntries: Boolean)(implicit ctx: Context): Option[R] = {
    if (refCount > 1)
      value.foreachByVar(f)
    else
      value.foreachByVal(f, fixedEntries)
  }

  override def foreachByVar[R](f: (PVal, PVar) => Option[R])(implicit ctx: Context): Option[R] = {
    value.foreachByVar(f)
  }

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
}

object PVar {
  def apply(): PVar = new PVar(None)

  def apply(v: PVal): PVar = new PVar(Some(v.concrete))

  def apply(optVal: Option[PAny]) = new PVar(optVal.map(_.asVal.concrete))
}
