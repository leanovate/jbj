package de.leanovate.jbj.runtime.context

import de.leanovate.jbj.runtime.value.{PVal, NullVal, PVar}

case class Variable(name: String,
                    owner: Context,
                    private var defined: Boolean = false) extends PVar {

  def isDefined = defined

  override def asVal = {
    if (!defined) {
      Thread.dumpStack()
      owner.log.notice("Undefined variable: %s".format(name))
      NullVal
    } else {
      value
    }
  }

  override protected def set(pVal: Option[PVal]) {
    if (!defined) {
      owner.defineVariableInt(name, this)
      defined = true
    }
    super.set(pVal)
  }

  override def unset() {
    super.unset()
    owner.undefineVariableInt(name)
    defined = false
  }

  override def toString = {
    "Variable(" + name + ", " + super.toString + ")"
  }
}