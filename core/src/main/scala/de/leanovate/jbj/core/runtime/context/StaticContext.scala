package de.leanovate.jbj.core.runtime.context

import de.leanovate.jbj.core.runtime.value.PVar

trait StaticContext {
  var initialized = false

  def findOrDefineVariable(name: String)(implicit ctx: Context): PVar = {
    val optVar = findVariable(name)
    if (optVar.isDefined)
      optVar.get
    else {
      val pVar = PVar()
      defineVariable(name, pVar)
      pVar
    }
  }

  def findVariable(name: String): Option[PVar]

  def defineVariable(name: String, pVar: PVar)(implicit ctx: Context)

  def undefineVariable(name: String)(implicit ctx: Context)

  def cleanup()(implicit ctx: Context)
}
