package de.leanovate.jbj.runtime.context

import de.leanovate.jbj.runtime.value.{PVal, NullVal, PVar}

case class Variable(name: String, owner: Context) extends PVar {
  private var defined = false

  def isDefined = defined

  override def value = {
    if (!defined) {
      owner.log.notice("Undefined variable: %s".format(name))
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