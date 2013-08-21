package de.leanovate.jbj.runtime.context

import de.leanovate.jbj.runtime.value.{PVal, NullVal, PVar}

case class StaticVariable(name: String, owner: StaticContext) extends PVar {
  private var defined = false

  def isDefined = defined

  override def value = {
    if (!defined) {
      NullVal
    } else {
      super.value
    }
  }

  override def value_=(v: PVal) {
    if (!defined) {
      owner.defineVariableInt(name, this)
      defined = true
    }
    super.value_=(v)
  }

  override def ref_=(v: PVar) {
    if (!defined) {
      owner.defineVariableInt(name, this)
      defined = true
    }
    super.ref_=(v)
  }

  override def unset() {
    super.unset()
    owner.undefineVariable(name)
    defined = false
  }
}
