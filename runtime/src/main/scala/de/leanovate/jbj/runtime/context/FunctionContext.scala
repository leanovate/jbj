/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.runtime.context

import scala.collection.mutable
import de.leanovate.jbj.runtime._
import de.leanovate.jbj.core.ast.{NodePosition, NamespaceName}
import scala.collection.immutable.Stack
import de.leanovate.jbj.runtime.value.PVar

case class FunctionContext(functionName: NamespaceName, callerContext: Context) extends FunctionLikeContext {
  private val localVariables = mutable.Map.empty[String, PVar]

  private val identifier = "Function_" + functionName.toString

  def name = ""

  def functionSignature = functionName.toString + "()"

  lazy val global = callerContext.global

  lazy val static = global.staticContext(identifier)

  lazy val settings = global.settings

  val out = callerContext.out

  val err = callerContext.err

  lazy val stack: Stack[NodePosition] = callerContext.stack.push(callerContext.currentPosition)

  defineVariable("GLOBALS", PVar(global.GLOBALS))

  override def findVariable(name: String): Option[PVar] = localVariables.get(name)

  override def defineVariable(name: String, variable: PVar) {
    variable.retain()
    localVariables.get(name).foreach(_.release()(this))
    localVariables.put(name, variable)

  }

  override def undefineVariable(name: String) {
    localVariables.remove(name).foreach(_.release()(this))
  }

  def findFunction(name: NamespaceName) = global.findFunction(name)

  def defineFunction(function: PFunction) {
    global.defineFunction(function)
  }

  def cleanup() {
    localVariables.values.foreach(_.release()(this))
  }
}
