package de.leanovate.jbj.runtime

import java.io.PrintStream
import de.leanovate.jbj.runtime.context.{StaticContext, GlobalContext}
import de.leanovate.jbj.ast.NamespaceName

trait Context {
  def global: GlobalContext

  def static: StaticContext

  def out: PrintStream

  def err: PrintStream

  lazy val log: Log = new Log(out, err)

  def findClass(name: NamespaceName): Option[PClass]

  def defineClass(pClass: PClass)

  def findConstant(name: String): Option[Value]

  def defineConstant(name: String, value: Value, caseInsensitive: Boolean)

  def findVariable(name: String): Option[ValueRef]

  def defineVariable(name: String, valueRef: ValueRef)

  def undefineVariable(name: String)

  def findFunction(name: NamespaceName): Option[PFunction]

  def defineFunction(function: PFunction)
}
