package de.leanovate.jbj.exec

import de.leanovate.jbj.ast.Value
import scala.collection.mutable
import scala.annotation.tailrec

case class FunctionContext(callerCtx: Context) extends Context {
  val localVariables = mutable.Map.empty[String, Value]

  val out = callerCtx.out

  lazy val parentCtx = findNoFunctionParent(callerCtx)

  def getVariable(name: String) = localVariables.getOrElse(name, parentCtx.getVariable(name))

  def setVariable(name: String, value: Value) {
    if (parentCtx.getVariable(name).isUndefined)
      localVariables.put(name, value)
    else
      parentCtx.setVariable(name, value)
  }

  def findFunction(name: String) = parentCtx.findFunction(name)

  @tailrec
  private def findNoFunctionParent(ctx: Context): Context =
    if (ctx.isInstanceOf[FunctionContext]) findNoFunctionParent(ctx.asInstanceOf[FunctionContext].callerCtx) else ctx

}
