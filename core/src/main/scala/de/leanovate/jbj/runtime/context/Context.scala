package de.leanovate.jbj.runtime.context

import java.io.PrintStream
import de.leanovate.jbj.ast.{NoNodePosition, NodePosition, NamespaceName}
import scala.collection.immutable.Stack
import de.leanovate.jbj.runtime.value.{PVar, PVal}
import de.leanovate.jbj.runtime.{PFunction, Log, Settings}

trait Context {
  def global: GlobalContext

  def static: StaticContext

  def settings: Settings

  def out: PrintStream

  def err: PrintStream

  var currentPosition:NodePosition = NoNodePosition

  lazy val log: Log = new Log(this, out, err)

  def stack: Stack[NodePosition]

  def findConstant(name: String): Option[PVal]

  def defineConstant(name: String, value: PVal, caseInsensitive: Boolean)

  def findVariable(name: String): Option[PVar]

  def defineVariable(name: String, valueRef: PVar)

  def undefineVariable(name: String)

  def findFunction(name: NamespaceName): Option[PFunction]

  def defineFunction(function: PFunction)

  protected[context] def defineVariableInt(name:String, variable:Variable) {}
}
