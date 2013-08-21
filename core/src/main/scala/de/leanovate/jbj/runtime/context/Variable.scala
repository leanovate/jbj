package de.leanovate.jbj.runtime.context

import de.leanovate.jbj.runtime.value.{PVal, NullVal, PVar}

case class Variable(name: String, owner: Context) extends PVar {
  private var undefined = true

  override def value = {
    if (undefined) {
      owner.log.notice("Undefined variable: %s".format(name))
      NullVal
    } else {
      super.value
    }
  }

  override def value_=(v: PVal) {
    if (undefined) {
      owner.defineVariableInt(name, this)
      undefined = false
    }
    super.value_=(v)
  }

  override def ref_=(v: PVar) {
    if (undefined) {
      owner.defineVariableInt(name, this)
      undefined = false
    }
    super.ref_=(v)
  }

  override def unset() {
    super.unset()
    owner.undefineVariable(name)
    undefined = true
  }
}