package de.leanovate.jbj.core.runtime.context

import de.leanovate.jbj.core.runtime.value.PVar

trait StaticContext {
  var initialized = false

  def findOrDefineVariable(name:String): PVar = {
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

  def defineVariable(name: String, pVar: PVar)

  def undefineVariable(name: String)

  def cleanup()
}
