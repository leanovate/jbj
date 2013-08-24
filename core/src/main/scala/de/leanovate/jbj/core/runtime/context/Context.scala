package de.leanovate.jbj.core.runtime.context

import java.io.PrintStream
import de.leanovate.jbj.core.ast.{NoNodePosition, NodePosition, NamespaceName}
import scala.collection.immutable.Stack
import de.leanovate.jbj.core.runtime.value.{PAny, NullVal, PVar, PVal}
import de.leanovate.jbj.core.runtime.{Reference, PFunction, Log, Settings}

trait Context {
  def global: GlobalContext

  def static: StaticContext

  def settings: Settings

  def out: PrintStream

  def err: PrintStream

  var currentPosition: NodePosition = NoNodePosition

  lazy val log: Log = new Log(this, out, err)

  def stack: Stack[NodePosition]

  def findConstant(name: String): Option[PVal]

  def defineConstant(name: String, value: PVal, caseInsensitive: Boolean)

  def findFunction(name: NamespaceName): Option[PFunction]

  def defineFunction(function: PFunction)

  def getVariable(name: String): Reference = new Reference {
    def isDefined = findVariable(name).exists(!_.value.isNull)

    def asVal = findVariable(name).map(_.value).getOrElse(NullVal)

    def asVar = findOrDefineVariable(name)

    def assign(pAny: PAny): PAny = {
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

  def findOrDefineVariable(name: String): PVar = {
    val optVar = findVariable(name)
    if (optVar.isDefined)
      optVar.get
    else {
      val pVar = PVar()
      defineVariable(name, pVar)
      pVar
    }
  }

  def findVariable(name: String): Option[PVar]

  def defineVariable(name: String, variable: PVar)

  def undefineVariable(name: String)

  def cleanup()
}
