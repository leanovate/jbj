/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.core.runtime.context

import de.leanovate.jbj.core.ast.{NodePosition, NamespaceName}
import de.leanovate.jbj.core.runtime.value.{PVar, PVal, ObjectVal}
import de.leanovate.jbj.core.runtime._
import scala.collection.immutable.Stack

case class InstanceContext(instance: ObjectVal, pClass:PClass, callerCtx: Context) extends Context {
  def name = instance.pClass.name.toString

  lazy val global = callerCtx.global

  lazy val static = global.staticContext(instance.pClass)

  lazy val settings = global.settings

  val out = callerCtx.out

  val err = callerCtx.err

  lazy val stack: Stack[NodePosition] = callerCtx.stack.push(callerCtx.currentPosition)

  override def findVariable(name: String) = None

  override def defineVariable(name: String, variable: PVar) {}

  override def undefineVariable(name: String) {}

  override def findFunction(name: NamespaceName) = callerCtx.findFunction(name)

  override def defineFunction(function: PFunction) {
    callerCtx.defineFunction(function)
  }

  def cleanup() {}
}
