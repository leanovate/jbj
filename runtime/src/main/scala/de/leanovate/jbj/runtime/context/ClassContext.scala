/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.runtime.context

import de.leanovate.jbj.runtime.{NodePosition, NamespaceName}
import scala.collection.immutable.Stack
import de.leanovate.jbj.runtime.value.PVar
import de.leanovate.jbj.runtime.types.{PFunction, PClass}

case class ClassContext(pClass: PClass, callerCtx: Context, override val currentPosition: NodePosition) extends Context {
  def name = pClass.name.toString

  lazy val global = callerCtx.global

  lazy val static = global.staticContext("Class_" + pClass.name.toString)

  lazy val settings = global.settings

  val out = callerCtx.out

  val err = callerCtx.err

  val filesystem = callerCtx.filesystem

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
