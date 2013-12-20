/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.runtime.context

import de.leanovate.jbj.runtime.value.PVar
import de.leanovate.jbj.runtime.{NodePosition, NamespaceName}
import scala.collection.mutable
import scala.collection.immutable.Stack
import de.leanovate.jbj.runtime.types.{PClass, PFunction, PMethod}

case class StaticMethodContext(pMethod: PMethod, pClass: PClass, callerContext: Context, allowThis: Boolean) extends FunctionLikeContext {
  private val localVariables = mutable.Map.empty[String, PVar]

  private val identifier = "Method_" + pMethod.implementingClass.name.toString + "::" + pMethod.name

  def name = pMethod.implementingClass.name.toString

  def functionSignature = pMethod.implementingClass.name.toString + "::" + pMethod.name + "()"

  lazy val global = callerContext.global

  lazy val static = global.staticContext(identifier)

  lazy val settings = global.settings

  val out = callerContext.out

  val httpResponseContext = callerContext.httpResponseContext

  val err = callerContext.err

  val filesystem = callerContext.filesystem

  lazy val stack: Stack[NodePosition] = callerContext.stack.push(callerContext.currentPosition)

  defineVariable("GLOBALS", PVar(global.GLOBALS))
  defineVariable("_SERVER", PVar(global._SERVER))

  override def findVariable(name: String): Option[PVar] = localVariables.get(name)

  override def defineVariable(name: String, variable: PVar) {
    variable.retain()
    localVariables.get(name).foreach(_.release()(this))
    localVariables.put(name, variable)
  }

  override def undefineVariable(name: String) {
    localVariables.remove(name).foreach(_.release()(this))
  }

  def findFunction(name: NamespaceName) = callerContext.findFunction(name)

  def defineFunction(function: PFunction) {
    callerContext.defineFunction(function)
  }

  def cleanup() {
    localVariables.values.foreach(_.release()(this))
  }
}
