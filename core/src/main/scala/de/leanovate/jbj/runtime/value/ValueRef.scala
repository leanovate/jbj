package de.leanovate.jbj.runtime.value

class ValueRef(private var current: Option[Value] = None) extends ValueOrRef {
  private var _refCount = 0

  def toOutput: String = current.map(_.toOutput).getOrElse("")

  def value = current.getOrElse(NullVal)

  def value_=(v: Value) {
    current = Some(v)
  }

  def unset() {
    current = None
  }

  def refCount = _refCount

  override def incrRefCount() {
    _refCount += 1
  }

  override def decrRefCount() {
    _refCount -= 1
  }
}

object ValueRef {
  def apply(): ValueRef = new ValueRef(None)

  def apply(v: Value): ValueRef = new ValueRef(Some(v))

  def apply(optVal: Option[ValueOrRef]) = new ValueRef(optVal.map(_.value))
}
