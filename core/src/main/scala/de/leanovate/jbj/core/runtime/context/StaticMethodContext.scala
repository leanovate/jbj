/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.core.runtime.context

import de.leanovate.jbj.core.runtime.value.{PVar, PVal}
import de.leanovate.jbj.core.ast.{NamespaceName, NodePosition}
import de.leanovate.jbj.core.runtime.{PMethod, PFunction}
import scala.collection.mutable
import scala.collection.immutable.Stack

case class StaticMethodContext(pMethod: PMethod, callerContext: Context) extends FunctionLikeContext {
  private val localVariables = mutable.Map.empty[String, PVar]

  private val identifier = "Method_" + pMethod.declaringClass.name.toString + "::" + pMethod.name

  def name = pMethod.declaringClass.name.toString

  def functionSignature = pMethod.declaringClass.name.toString + "::" + pMethod.name + "()"

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

  def findFunction(name: NamespaceName) = callerContext.findFunction(name)

  def defineFunction(function: PFunction) {
    callerContext.defineFunction(function)
  }

  def cleanup() {
    localVariables.values.foreach(_.release()(this))
  }
}
