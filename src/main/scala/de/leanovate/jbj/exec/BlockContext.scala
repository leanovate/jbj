package de.leanovate.jbj.exec

import de.leanovate.jbj.ast.Value
import scala.collection.mutable
import scala.annotation.tailrec
import de.leanovate.jbj.ast.value.ValueRef

case class BlockContext(identifier:String, callerCtx: Context) extends Context {
  val localVariables = mutable.Map.empty[String, ValueRef]

  val out = callerCtx.out

  def findVariable(name: String): Option[ValueRef] =
    localVariables.get(name) match {
      case value@Some(_) => value
      case None => callerCtx.findVariable(name)
    }

  def defineVariable(name: String, static: Boolean, value: Value) = value match {
    case valueRef: ValueRef => localVariables.put(name, valueRef)
    case v => localVariables.put(name, new ValueRef(v))
  }

  def findFunction(name: String) = callerCtx.findFunction(name)
}
