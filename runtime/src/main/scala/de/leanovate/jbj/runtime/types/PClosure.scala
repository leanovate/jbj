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
import de.leanovate.jbj.runtime.exception.{CatchableFatalError, FatalErrorJbjException}

sealed abstract class PClosure(instanceNum: Long, returnByRef: Boolean, parameterDecls: List[PParamDef],
                               position: NodePosition,
                               invoke: FunctionLikeContext => PAny)
  extends StdObjectVal(PClosure, instanceNum, new ExtendedLinkedHashMap[ObjectPropertyKey.Key]) {

  private val activeContexts = mutable.Set.empty[Context]

  def newFunctionContext(implicit callerCtx: Context): FunctionLikeContext

  override def setProperty(name: String, className: Option[String], value: PAny)(implicit ctx: Context) =
    CatchableFatalError("Closure object cannot have properties")

  override def call(params: List[PParam])(implicit callerCtx: Context): PAny = {
    val funcCtx = newFunctionContext

    activeContexts += funcCtx

    funcCtx.currentPosition = position

    funcCtx.setParameters(callerCtx, parameterDecls, params, detailedError = true)
    getProperty("static", None)(funcCtx).foreach {
      case array: ArrayVal =>
        array.keyValues.foreach {
          case (variableName: StringVal, pVar: PVar) =>
            funcCtx.defineVariable(variableName.asString, pVar)
          case (variableName: StringVal, pVal: PVal) =>
            funcCtx.defineVariable(variableName.asString, PVar(pVal))
          case _ =>
        }
      case _ =>
    }
    val result = invoke(funcCtx)
    activeContexts -= funcCtx
    result
  }

  override def cleanup()(implicit ctx: Context) {
    if (activeContexts.contains(ctx))
      throw new FatalErrorJbjException("Cannot destroy active lambda function")
    super.cleanup()
  }
}

class InstancePClosure(instanceNum: Long, returnByRef: Boolean, parameterDecls: List[PParamDef], position: NodePosition,
                       instance: ObjectVal, invoke: FunctionLikeContext => PAny)
  extends PClosure(instanceNum, returnByRef, parameterDecls, position, invoke) {

  override def newFunctionContext(implicit callerCtx: Context) = {
    val pMethod = new InstanceMethod(instance.pClass, "Closure::lambda-" + instanceNum, parameterDecls, isFinal = true) {
      def invoke(instance: ObjectVal, parameters: List[PParam])(implicit callerCtx: Context) = ???
    }
    MethodContext(instance, pMethod, callerCtx)
  }

  override def clone(implicit ctx: Context): PVal = {
    PClosure(returnByRef, parameterDecls, instance, getProperty("static", None).get.asInstanceOf[ArrayVal], invoke)
  }
}

class StaticPClosure(instanceNum: Long, returnByRef: Boolean, parameterDecls: List[PParamDef], position: NodePosition,
                     pClass: PClass, invoke: FunctionLikeContext => PAny)
  extends PClosure(instanceNum, returnByRef, parameterDecls, position, invoke) {

  override def newFunctionContext(implicit callerCtx: Context) = {
    val pMethod = new InstanceMethod(pClass, "Closure::lambda-" + instanceNum, parameterDecls, isFinal = true) {
      def invoke(instance: ObjectVal, parameters: List[PParam])(implicit callerCtx: Context) = ???
    }
    StaticMethodContext(pMethod, callerCtx, allowThis = false)
  }

  override def clone(implicit ctx: Context): PVal = {
    PClosure(returnByRef, parameterDecls, pClass, getProperty("static", None).get.asInstanceOf[ArrayVal], invoke)
  }
}

class GlobalPClosure(instanceNum: Long, returnByRef: Boolean, parameterDecls: List[PParamDef], position: NodePosition,
                     invoke: FunctionLikeContext => PAny)
  extends PClosure(instanceNum, returnByRef, parameterDecls, position, invoke) {

  override def newFunctionContext(implicit callerCtx: Context) = {
    FunctionContext(NamespaceName("Closure::lambda-" + instanceNum), callerCtx)
  }

  override def clone(implicit ctx: Context): PVal = {
    PClosure(returnByRef, parameterDecls, getProperty("static", None).get.asInstanceOf[ArrayVal], invoke)
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

  def apply(returnByRef: Boolean, parameterDecls: List[PParamDef], instance: ObjectVal, lexicalValues: ArrayVal,
            invoke: FunctionLikeContext => PAny)(implicit ctx: Context): ObjectVal = {
    val result = new InstancePClosure(ctx.global.instanceCounter.incrementAndGet(), returnByRef, parameterDecls,
      ctx.currentPosition, instance, invoke)
    result.definePublicProperty("static", lexicalValues)
    result.definePublicProperty("this", instance)
    ctx.poolAutoRelease(result)
    result
  }

  def apply(returnByRef: Boolean, parameterDecls: List[PParamDef], pClass: PClass, lexicalValues: ArrayVal,
            invoke: FunctionLikeContext => PAny)(implicit ctx: Context): ObjectVal = {
    val result = new StaticPClosure(ctx.global.instanceCounter.incrementAndGet(), returnByRef, parameterDecls,
      ctx.currentPosition, pClass, invoke)
    result.definePublicProperty("static", lexicalValues)
    ctx.poolAutoRelease(result)
    result
  }

  def apply(returnByRef: Boolean, parameterDecls: List[PParamDef], lexicalValues: ArrayVal,
            invoke: FunctionLikeContext => PAny)(implicit ctx: Context): ObjectVal = {
    val result = new GlobalPClosure(ctx.global.instanceCounter.incrementAndGet(), returnByRef, parameterDecls,
      ctx.currentPosition, invoke)
    result.definePublicProperty("static", lexicalValues)
    ctx.poolAutoRelease(result)
    result
  }
}
