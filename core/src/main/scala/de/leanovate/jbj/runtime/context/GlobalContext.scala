package de.leanovate.jbj.runtime.context

import java.io.PrintStream
import scala.collection.mutable
import de.leanovate.jbj.runtime._
import de.leanovate.jbj.runtime.buildin
import scala.Some
import de.leanovate.jbj.ast.NamespaceName

case class GlobalContext(out: PrintStream, err: PrintStream) extends Context {
  private val classes = mutable.Map.empty[NamespaceName, PClass]

  private val constants = mutable.Map.empty[ConstantKey, Value]

  private val variables = mutable.Map.empty[String, ValueRef]

  private val functions = mutable.Map.empty[NamespaceName, PFunction]

  private val staticContexts = mutable.Map.empty[String, StaticContext]

  def global = this

  def static = staticContext("global")

  def findClass(name: NamespaceName): Option[PClass] = classes.get(name)

  def defineClass(pClass: PClass) {
    classes.put(pClass.name, pClass)
  }

  def findConstant(name: String): Option[Value] =
    buildin.buildinConstants.get(name.toUpperCase).map(Some(_)).getOrElse {
      constants.get(CaseSensitiveConstantKey(name)).map(Some(_)).getOrElse {
        constants.get(CaseInsensitiveConstantKey(name.toLowerCase))
      }
    }

  def defineConstant(name: String, value: Value, caseInsensitive: Boolean) {
    if (caseInsensitive)
      constants.put(CaseInsensitiveConstantKey(name.toLowerCase), value)
    else
      constants.put(CaseSensitiveConstantKey(name), value)
  }

  def findVariable(name: String): Option[ValueRef] = variables.get(name)

  def defineVariable(name: String, valueRef: ValueRef) {
    variables.put(name, valueRef)
  }

  def undefineVariable(name: String) {
    variables.remove(name)
  }

  def findFunction(name: NamespaceName) =
    if (name.path.size == 1)
      buildin.buildinFunctions.get(name.path.head).map(Some(_)).getOrElse(functions.get(name))
    else
      functions.get(name)

  def defineFunction(function: PFunction) {
    functions.put(NamespaceName(function.name), function)
  }

  def staticContext(identifier: String): StaticContext =
    staticContexts.getOrElseUpdate(identifier, new StaticContext(this))
}
