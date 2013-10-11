/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.runtime.types

import de.leanovate.jbj.runtime.{NodePosition, NamespaceName}
import de.leanovate.jbj.runtime.value._
import de.leanovate.jbj.runtime.context._
import de.leanovate.jbj.runtime.adapter.InstanceMethod
import de.leanovate.jbj.runtime.context.FunctionContext
import de.leanovate.jbj.runtime.context.MethodContext
import scala.collection.mutable
import de.leanovate.jbj.runtime.exception.FatalErrorJbjException

sealed abstract class PClosure(instanceNum: Long, returnByRef: Boolean, parameterDecls: List[PParamDef],
                               position: NodePosition, lexicalValues: Seq[(String, PAny)],
                               invoke: FunctionLikeContext => PAny)
  extends StdObjectVal(PClosure, instanceNum, new ExtendedLinkedHashMap[ObjectPropertyKey.Key]) {

  lexicalValues.foreach(_._2.retain())

  private val activeContexts = mutable.Set.empty[Context]

  def newFunctionContext(implicit callerCtx: Context): FunctionLikeContext

  override def call(params: List[PParam])(implicit callerCtx: Context): PAny = {
    val funcCtx = newFunctionContext

    activeContexts += funcCtx

    funcCtx.currentPosition = position

    funcCtx.setParameters(callerCtx, parameterDecls, params, detailedError = true)
    lexicalValues.foreach {
      case (variableName, pVar: PVar) =>
        funcCtx.defineVariable(variableName, pVar)
      case (variableName, pVal: PVal) =>
        funcCtx.defineVariable(variableName, PVar(pVal))
    }
    val result = invoke(funcCtx)
    activeContexts -= funcCtx
    result
  }

  override def cleanup()(implicit ctx: Context) {
    if (activeContexts.contains(ctx))
      throw new FatalErrorJbjException("Cannot destroy active lambda function")
    super.cleanup()
    lexicalValues.foreach(_._2.release())
  }
}

class InstancePClosure(instanceNum: Long, returnByRef: Boolean, parameterDecls: List[PParamDef], position: NodePosition,
                       instance: ObjectVal, lexicalValues: Seq[(String, PAny)], invoke: FunctionLikeContext => PAny)
  extends PClosure(instanceNum, returnByRef, parameterDecls, position, lexicalValues, invoke) {

  instance.retain()

  override def newFunctionContext(implicit callerCtx: Context) = {
    val pMethod = new InstanceMethod(instance.pClass, "Closure::lambda-" + instanceNum, parameterDecls, isFinal = true) {
      def invoke(instance: ObjectVal, parameters: List[PParam])(implicit callerCtx: Context) = ???
    }
    MethodContext(instance, pMethod, callerCtx)
  }

  override def cleanup()(implicit ctx: Context) {
    super.cleanup()
    instance.release()
  }
}

class StaticPClosure(instanceNum: Long, returnByRef: Boolean, parameterDecls: List[PParamDef], position: NodePosition,
                     pClass: PClass, lexicalValues: Seq[(String, PAny)], invoke: FunctionLikeContext => PAny)
  extends PClosure(instanceNum, returnByRef, parameterDecls, position, lexicalValues, invoke) {

  override def newFunctionContext(implicit callerCtx: Context) = {
    val pMethod = new InstanceMethod(pClass, "Closure::lambda-" + instanceNum, parameterDecls, isFinal = true) {
      def invoke(instance: ObjectVal, parameters: List[PParam])(implicit callerCtx: Context) = ???
    }
    StaticMethodContext(pMethod, callerCtx, allowThis = false)
  }
}

class GlobalPClosure(instanceNum: Long, returnByRef: Boolean, parameterDecls: List[PParamDef], position: NodePosition,
                     lexicalValues: Seq[(String, PAny)], invoke: FunctionLikeContext => PAny)
  extends PClosure(instanceNum, returnByRef, parameterDecls, position, lexicalValues, invoke) {

  override def newFunctionContext(implicit callerCtx: Context) = {
    FunctionContext(NamespaceName("Closure::lambda-" + instanceNum), callerCtx)
  }
}

object PClosure extends PClass {
  override def isAbstract = false

  override def isFinal = false

  override def name = NamespaceName(relative = false, prefixed = false, "Closure")

  override def superClass = None

  override def interfaces = Set.empty

  override def classConstants: Map[String, PVal] = Map.empty

  override def initializeStatic(staticContext: ObjectVal)(implicit ctx: Context) {}

  override def newInstance(parameters: List[PParam])(implicit ctx: Context) =
    new StdObjectVal(this, ctx.global.instanceCounter.incrementAndGet(), new ExtendedLinkedHashMap[ObjectPropertyKey.Key])

  override def destructInstance(instance: ObjectVal)(implicit ctx: Context) {
    instance.cleanup()
  }

  override def properties = Map.empty

  override def methods = Seq(
    new InstanceMethod(this, "__invoke") {
      def invoke(instance: ObjectVal, parameters: List[PParam])(implicit callerCtx: Context) = {
        instance.call(parameters)
      }
    }
  ).map {
    method =>
      method.name.toLowerCase -> method
  }.toMap

  def apply(returnByRef: Boolean, parameterDecls: List[PParamDef], instance: ObjectVal, lexicalValues: Seq[(String, PAny)],
            invoke: FunctionLikeContext => PAny)(implicit ctx: Context): ObjectVal = {
    val result = new InstancePClosure(ctx.global.instanceCounter.incrementAndGet(), returnByRef, parameterDecls,
      ctx.currentPosition, instance, lexicalValues, invoke)
    ctx.poolAutoRelease(result)
    result
  }

  def apply(returnByRef: Boolean, parameterDecls: List[PParamDef], pClass: PClass, lexicalValues: Seq[(String, PAny)],
            invoke: FunctionLikeContext => PAny)(implicit ctx: Context): ObjectVal = {
    val result = new StaticPClosure(ctx.global.instanceCounter.incrementAndGet(), returnByRef, parameterDecls,
      ctx.currentPosition, pClass, lexicalValues, invoke)
    ctx.poolAutoRelease(result)
    result
  }

  def apply(returnByRef: Boolean, parameterDecls: List[PParamDef], lexicalValues: Seq[(String, PAny)],
            invoke: FunctionLikeContext => PAny)(implicit ctx: Context): ObjectVal = {
    val result = new GlobalPClosure(ctx.global.instanceCounter.incrementAndGet(), returnByRef, parameterDecls,
      ctx.currentPosition, lexicalValues, invoke)
    ctx.poolAutoRelease(result)
    result
  }
}
