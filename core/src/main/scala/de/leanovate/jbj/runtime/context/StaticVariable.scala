package de.leanovate.jbj.runtime.context

import de.leanovate.jbj.runtime.value.PVar

case class StaticVariable(name: String, owner: StaticContext) extends PVar {
}
