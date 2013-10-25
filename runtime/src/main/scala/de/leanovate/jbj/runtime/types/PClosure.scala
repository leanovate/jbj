/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.runtime.types

import de.leanovate.jbj.runtime.{NamespaceName, NodePosition}
import de.leanovate.jbj.runtime.value._
import de.leanovate.jbj.runtime.context._
import de.leanovate.jbj.runtime.adapter.{StaticMethod, InstanceMethod}
import de.leanovate.jbj.runtime.context.FunctionContext
import de.leanovate.jbj.runtime.context.MethodContext
import scala.collection.mutable
import de.leanovate.jbj.runtime.exception.{CatchableFatalError, FatalErrorJbjException}
import de.leanovate.jbj.runtime.value.ObjectPropertyKey.PublicKey

class PClosure(instanceNum: Long, returnByRef: Boolean, parameterDecls: List[PParamDef],
               isStatic: Boolean, optScope: Option[PClass], optThis: Option[ObjectVal],
               position: NodePosition, invoke: FunctionLikeContext => PAny)
  extends StdObjectVal(PClosure, instanceNum, new ExtendedLinkedHashMap[ObjectPropertyKey.Key]) {

  private val activeContexts = mutable.Set.empty[Context]

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

  def newFunctionContext(implicit callerCtx: Context) = {
    optThis match {
      case Some(instance) if !isStatic =>
        val pMethod = new InstanceMethod(optScope.getOrElse(PStdClass), "{closure}", parameterDecls, isFinal = true) {
          def invoke(instance: ObjectVal, parameters: List[PParam])(implicit callerCtx: Context) = ???
        }
        new MethodContext(instance, pMethod, callerCtx) {
          override lazy val static = global.staticContext("Closure::lambda-" + instanceNum)
        }
      case _ =>
        optScope match {
          case Some(scopeClass) =>
            val pMethod = new InstanceMethod(scopeClass, "{closure}", parameterDecls, isFinal = true) {
              def invoke(instance: ObjectVal, parameters: List[PParam])(implicit callerCtx: Context) = ???
            }
            new StaticMethodContext(pMethod, scopeClass, callerCtx, allowThis = false) {
              override lazy val static = global.staticContext("Closure::lambda-" + instanceNum)
            }
          case None =>
            new FunctionContext(NamespaceName("{closure}"), callerCtx) {
              override lazy val static = global.staticContext("Closure::lambda-" + instanceNum)
            }
        }
    }
  }

  def bindTo(newThis: Option[ObjectVal], newScope: Option[PClass])(implicit ctx: Context): ObjectVal = {
    if (isStatic && newThis.isDefined)
      ctx.log.warn("Cannot bind an instance to a static closure")
    PClosure(returnByRef, parameterDecls, isStatic || !newThis.isDefined,
      newScope.map(Some.apply).getOrElse(optScope), newThis,
      keyValueMap.get(PublicKey("static")).map(_.asInstanceOf[ArrayVal]), invoke)
  }

  override def clone(implicit ctx: Context): PVal = {
    PClosure(returnByRef, parameterDecls, isStatic, optScope, optThis, keyValueMap.get(PublicKey("static")).map(_.asInstanceOf[ArrayVal]), invoke)
  }

  override def cleanup()(implicit ctx: Context) {
    if (activeContexts.contains(ctx))
      throw new FatalErrorJbjException("Cannot destroy active lambda function")
    super.cleanup()
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
      def invoke(instance: ObjectVal, parameters: List[PParam])(implicit callerCtx: Context): PAny = {
        if (parameters.length == 0) {
          callerCtx.log.warn("Closure::bindTo() expects at least 1 parameter, 0 given")
          return NullVal
        } else if (parameters.length > 2) {
          callerCtx.log.warn("Closure::bindTo() expects at most 2 parameters, %d given".format(parameters.length))
          return NullVal
        }
        val newInstance = parameters(0).byVal.concrete match {
          case obj: ObjectVal => Some(obj)
          case NullVal => None
          case pVal =>
            callerCtx.log.warn("Closure::bindTo() expects parameter 1 to be object, %s given".format(pVal.typeName()))
            return NullVal
        }
        val newScopeName = if (parameters.length > 1) {
          parameters(1).byVal.concrete match {
            case obj: ObjectVal => Some(obj.pClass.name)
            case StringVal("static") => None
            case StringVal(n) => Some(NamespaceName(n))
            case NullVal => Some(NamespaceName("stdClass"))
            case array: ArrayVal =>
              callerCtx.log.notice("Array to string conversion")
              Some(NamespaceName("Array"))
            case pVal =>
              Some(NamespaceName(pVal.toStr.asString))
          }
        } else {
          None
        }
        val newScope = newScopeName.flatMap {
          n =>
            callerCtx.global.findClass(n, autoload = false).map(Some.apply).getOrElse {
              callerCtx.log.warn("Class '%s' not found".format(n.toString))
              return NullVal
            }
        }

        instance match {
          case closure: PClosure =>
            closure.bindTo(newInstance, newScope)
          case _ =>
            throw new FatalErrorJbjException("Call Closure::bindTo() on non-closure")
        }
      }
    },
    new StaticMethod(this, "bind") {
      def invokeStatic(parameters: List[PParam], strict: Boolean = true)(implicit callerCtx: Context) = {
        if (parameters.length < 2) {
          throw new FatalErrorJbjException("Closure::bind() expects at least 2 arguments")
        }
        val closure = parameters(0).byVal.concrete match {
          case c: PClosure => c
          case _ =>
            throw new FatalErrorJbjException("Closure::bind() expects argument 1 to be a closure")
        }
        val newInstance = parameters(1).byVal.concrete match {
          case obj: ObjectVal => Some(obj)
          case NullVal => None
          case pVal =>
            callerCtx.log.warn("Closure::bindTo() expects parameter 2 to be object, %s given".format(pVal.typeName()))
            None
        }
        val newScopeName = if (parameters.length > 1) {
          parameters(1).byVal.concrete match {
            case obj: ObjectVal => Some(obj.pClass.name)
            case StringVal("static") => None
            case StringVal(n) => Some(NamespaceName(n))
            case NullVal => Some(NamespaceName("stdClass"))
            case array: ArrayVal =>
              callerCtx.log.notice("Array to string conversion")
              Some(NamespaceName("Array"))
            case pVal =>
              Some(NamespaceName(pVal.toStr.asString))
          }
        } else {
          None
        }
        val newScope = newScopeName.flatMap {
          n =>
            callerCtx.global.findClass(n, autoload = false).map(Some.apply).getOrElse {
              callerCtx.log.warn("Class '%s' not found".format(n.toString))
              None
            }
        }

        closure.bindTo(newInstance, newScope)
      }
    }
  ).map {
    method =>
      method.name.toLowerCase -> method
  }.toMap

  def apply(returnByRef: Boolean, parameterDecls: List[PParamDef], isStatic: Boolean, optScope: Option[PClass], optThis: Option[ObjectVal],
            lexicalValues: Option[ArrayVal], invoke: FunctionLikeContext => PAny)(implicit ctx: Context): ObjectVal = {
    val result = new PClosure(ctx.global.instanceCounter.incrementAndGet(), returnByRef, parameterDecls,
      isStatic, optScope, optThis, ctx.currentPosition, invoke)
    lexicalValues.foreach(result.definePublicProperty("static", _))
    optThis.map(result.definePublicProperty("this", _))
    ctx.poolAutoRelease(result)
    result
  }
}
