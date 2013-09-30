package de.leanovate.jbj.runtime.value

import de.leanovate.jbj.runtime.context.Context

trait DelegateObjectVal extends ObjectVal {
  def delegate: ObjectVal

  override def pClass = delegate.pClass

  override def instanceNum = delegate.instanceNum

  override protected[value] def keyValueMap = delegate.keyValueMap

  override def refCount = delegate.refCount

  override def retain() {
    delegate.retain()
  }

  override def release()(implicit ctx: Context) {
    delegate.release()
  }
}
