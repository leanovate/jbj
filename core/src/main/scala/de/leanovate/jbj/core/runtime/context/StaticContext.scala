/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.core.runtime.context

import de.leanovate.jbj.core.runtime.value.{PVal, PAny, NullVal, PVar}
import de.leanovate.jbj.core.runtime.Reference

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

  def getVariable(name: String)(implicit ctx: Context): Reference = new Reference {
    def isDefined = findVariable(name).exists(!_.value.isNull)

    def asVal = findVariable(name).map(_.value).getOrElse(NullVal)

    def asVar = findOrDefineVariable(name)

    def assign(pAny: PAny)(implicit ctx: Context): PAny = {
      pAny match {
        case pVar: PVar =>
          defineVariable(name, pVar)
        case pVal: PVal =>
          findOrDefineVariable(name).value = pVal
      }
      pAny
    }

    def unset() {
      undefineVariable(name)
    }
  }

  def variables:Map[String, PVar]

  def findVariable(name: String): Option[PVar]

  def defineVariable(name: String, pVar: PVar)(implicit ctx: Context)

  def undefineVariable(name: String)(implicit ctx: Context)

  def cleanup()(implicit ctx: Context)
}
