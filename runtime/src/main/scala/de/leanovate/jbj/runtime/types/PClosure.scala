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
import de.leanovate.jbj.runtime.adapter.{StaticMethod, InstanceMethod}
import de.leanovate.jbj.runtime.context.FunctionContext
import de.leanovate.jbj.runtime.context.MethodContext
import scala.collection.mutable
import de.leanovate.jbj.runtime.exception.{CatchableFatalError, FatalErrorJbjException}
import de.leanovate.jbj.runtime.value.ObjectPropertyKey.PublicKey

sealed abstract class PClosure(instanceNum: Long, returnByRef: Boolean, parameterDecls: List[PParamDef],
                               position: NodePosition, invoke: FunctionLikeContext => PAny)
  extends StdObjectVal(PClosure, instanceNum, new ExtendedLinkedHashMap[ObjectPropertyKey.Key]) {

  private val activeContexts = mutable.Set.empty[Context]

  def newFunctionContext(implicit callerCtx: Context): FunctionLikeContext

  override def setProperty(name: String, className: Option[String], value: PAny)(implicit ctx: Context) =
    CatchableFatalError("Closure object cannot have properties")

  override def getProperty(name: String, className: Option[String])(implicit ctx: Context) = {
    CatchableFatalError("Closure object cannot have properties")
    Some(NullVal)
  }

  override def call(params: List[PParam])(implicit callerCtx: Context): PAny = {
    val funcCtx = newFunctionContext

    activeContexts += funcCtx

    funcCtx.currentPosition = position

    funcCtx.setParameters(callerCtx, parameterDecls, params, detailedError = true)
    keyValueMap.get(PublicKey("static")).foreach {
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

  def bindTo(newInstance: ObjectVal)(implicit ctx: Context): ObjectVal = {
    PClosure(returnByRef, parameterDecls, newInstance.pClass, newInstance: ObjectVal,
      keyValueMap.get(PublicKey("static")).map(_.asInstanceOf[ArrayVal]), invoke)
  }

  override def cleanup()(implicit ctx: Context) {
    if (activeContexts.contains(ctx))
      throw new FatalErrorJbjException("Cannot destroy active lambda function")
    super.cleanup()
  }
}

class InstancePClosure(instanceNum: Long, returnByRef: Boolean, parameterDecls: List[PParamDef], position: NodePosition,
                       pClass:PClass, instance: ObjectVal, invoke: FunctionLikeContext => PAny)
  extends PClosure(instanceNum, returnByRef, parameterDecls, position, invoke) {

  override def newFunctionContext(implicit callerCtx: Context) = {
    val pMethod = new InstanceMethod(pClass, "{closure}", parameterDecls, isFinal = true) {
      def invoke(instance: ObjectVal, parameters: List[PParam])(implicit callerCtx: Context) = ???
    }
    new MethodContext(instance, pMethod, callerCtx) {
      override lazy val static = global.staticContext("Closure::lambda-" + instanceNum)
    }
  }

  override def clone(implicit ctx: Context): PVal = {
    PClosure(returnByRef, parameterDecls, pClass, instance, getProperty("static", None).map(_.asInstanceOf[ArrayVal]), invoke)
  }
}

class StaticPClosure(instanceNum: Long, returnByRef: Boolean, parameterDecls: List[PParamDef], position: NodePosition,
                     pClass: PClass, invoke: FunctionLikeContext => PAny)
  extends PClosure(instanceNum, returnByRef, parameterDecls, position, invoke) {

  override def newFunctionContext(implicit callerCtx: Context) = {
    val pMethod = new InstanceMethod(pClass, "{closure}", parameterDecls, isFinal = true) {
      def invoke(instance: ObjectVal, parameters: List[PParam])(implicit callerCtx: Context) = ???
    }
    new StaticMethodContext(pMethod, callerCtx, allowThis = false) {
      override lazy val static = global.staticContext("Closure::lambda-" + instanceNum)
    }
  }

  override def clone(implicit ctx: Context): PVal = {
    PClosure(returnByRef, parameterDecls, pClass, keyValueMap.get(PublicKey("static")).map(_.asInstanceOf[ArrayVal]), invoke)
  }
}

class GlobalPClosure(instanceNum: Long, returnByRef: Boolean, parameterDecls: List[PParamDef], position: NodePosition,
                     invoke: FunctionLikeContext => PAny)
  extends PClosure(instanceNum, returnByRef, parameterDecls, position, invoke) {

  override def newFunctionContext(implicit callerCtx: Context) = {
    new FunctionContext(NamespaceName("{closure}"), callerCtx) {
      override lazy val static = global.staticContext("Closure::lambda-" + instanceNum)
    }
  }

  override def clone(implicit ctx: Context): PVal = {
    PClosure(returnByRef, parameterDecls, keyValueMap.get(PublicKey("static")).map(_.asInstanceOf[ArrayVal]), invoke)
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
    },
    new InstanceMethod(this, "bindTo") {
      def invoke(instance: ObjectVal, parameters: List[PParam])(implicit callerCtx: Context) = {
        if (parameters.length != 1) {
          throw new FatalErrorJbjException("Closure::bindTo() expects exactly 1 argument")
        }
        val newInstance = parameters(0).byVal.concrete match {
          case obj: ObjectVal => obj
          case _ =>
            throw new FatalErrorJbjException("Closure::bindTo() expects argument 1 to be an object")
        }
        instance match {
          case closure: PClosure =>
            closure.bindTo(newInstance)
          case _ =>
            throw new FatalErrorJbjException("Call Closure::bindTo() on non-closure")
        }
      }
    },
    new StaticMethod(this, "bind") {
      def invokeStatic(parameters: List[PParam])(implicit callerCtx: Context) = {
        if (parameters.length != 2) {
          throw new FatalErrorJbjException("Closure::bind() expects exactly 2 arguments")
        }
        val closure = parameters(0).byVal.concrete match {
          case c: PClosure => c
          case _ =>
            throw new FatalErrorJbjException("Closure::bind() expects argument 1 to be a closure")
        }
        val newInstance = parameters(1).byVal.concrete match {
          case obj: ObjectVal => obj
          case _ =>
            throw new FatalErrorJbjException("Closure::bind() expects argument 2 to be an object")
        }
        closure.bindTo(newInstance)
      }
    }
  ).map {
    method =>
      method.name.toLowerCase -> method
  }.toMap

  def apply(returnByRef: Boolean, parameterDecls: List[PParamDef], pClass:PClass, instance: ObjectVal, lexicalValues: Option[ArrayVal],
            invoke: FunctionLikeContext => PAny)(implicit ctx: Context): ObjectVal = {
    val result = new InstancePClosure(ctx.global.instanceCounter.incrementAndGet(), returnByRef, parameterDecls,
      ctx.currentPosition, pClass, instance, invoke)
    lexicalValues.foreach(result.definePublicProperty("static", _))
    result.definePublicProperty("this", instance)
    ctx.poolAutoRelease(result)
    result
  }

  def apply(returnByRef: Boolean, parameterDecls: List[PParamDef], pClass: PClass, lexicalValues: Option[ArrayVal],
            invoke: FunctionLikeContext => PAny)(implicit ctx: Context): ObjectVal = {
    val result = new StaticPClosure(ctx.global.instanceCounter.incrementAndGet(), returnByRef, parameterDecls,
      ctx.currentPosition, pClass, invoke)
    lexicalValues.foreach(result.definePublicProperty("static", _))
    ctx.poolAutoRelease(result)
    result
  }

  def apply(returnByRef: Boolean, parameterDecls: List[PParamDef], lexicalValues: Option[ArrayVal],
            invoke: FunctionLikeContext => PAny)(implicit ctx: Context): ObjectVal = {
    val result = new GlobalPClosure(ctx.global.instanceCounter.incrementAndGet(), returnByRef, parameterDecls,
      ctx.currentPosition, invoke)
    lexicalValues.foreach(result.definePublicProperty("static", _))
    ctx.poolAutoRelease(result)
    result
  }
}
