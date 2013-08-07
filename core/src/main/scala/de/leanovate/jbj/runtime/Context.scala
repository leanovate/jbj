package de.leanovate.jbj.runtime

import java.io.PrintStream
import de.leanovate.jbj.runtime.context.{StaticContext, GlobalContext}
import de.leanovate.jbj.ast.{NodePosition, NamespaceName}
import scala.collection.immutable.Stack

trait Context {
  def global: GlobalContext

  def static: StaticContext

  def settings: Settings

  def out: PrintStream

  def err: PrintStream

  lazy val log: Log = new Log(settings, out, err)

  def stack: Stack[NodePosition]

  def findConstant(name: String): Option[Value]

  def defineConstant(name: String, value: Value, caseInsensitive: Boolean)

  def findVariable(name: String)(implicit position: NodePosition): Option[ValueRef]

  def defineVariable(name: String, valueRef: ValueRef)(implicit position: NodePosition)

  def undefineVariable(name: String)

  def findFunction(name: NamespaceName): Option[PFunction]

  def defineFunction(function: PFunction)
}
