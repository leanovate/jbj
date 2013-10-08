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
import de.leanovate.jbj.runtime.exception.FatalErrorJbjException
import de.leanovate.jbj.runtime.adapter.InstanceMethod
import de.leanovate.jbj.runtime.context.FunctionContext
import de.leanovate.jbj.runtime.context.MethodContext

sealed trait PClosure

class InstancePClosure(instanceNum: Long, returnByRef: Boolean, parameterDecls: List[PParamDef], position: NodePosition,
                       instance: ObjectVal, lexicalValues: Seq[(String, PVar)], invoke: FunctionLikeContext => PAny)
  extends StdObjectVal(PClosure, instanceNum, new ExtendedLinkedHashMap[ObjectPropertyKey.Key]) with PClosure {

  instance.retain()
  lexicalValues.foreach(_._2.retain())

  override def call(params: List[PParam])(implicit callerCtx: Context): PAny = {
    val pMethod = new InstanceMethod(instance.pClass, "lambda-" + instanceNum, parameterDecls, isFinal = true) {
      def invoke(instance: ObjectVal, parameters: List[PParam])(implicit callerCtx: Context) = ???
    }
    val funcCtx = MethodContext(instance, pMethod, callerCtx)

    funcCtx.currentPosition = position

    funcCtx.setParameters(callerCtx, parameterDecls, params)
    lexicalValues.foreach {
      case (variableName, pVar) =>
        funcCtx.defineVariable(variableName, pVar)
    }
    invoke(funcCtx)
  }

  override def cleanup()(implicit ctx: Context) {
    super.cleanup()
    lexicalValues.foreach(_._2.release())
    instance.release()
  }
}

class StaticPClosure(instanceNum: Long, returnByRef: Boolean, parameterDecls: List[PParamDef], position: NodePosition,
                     pClass: PClass, lexicalValues: Seq[(String, PVar)], invoke: FunctionLikeContext => PAny)
  extends StdObjectVal(PClosure, instanceNum, new ExtendedLinkedHashMap[ObjectPropertyKey.Key]) with PClosure {

  lexicalValues.foreach(_._2.retain())

  override def call(params: List[PParam])(implicit callerCtx: Context): PAny = {
    val pMethod = new InstanceMethod(pClass, "lambda-" + instanceNum, parameterDecls, isFinal = true) {
      def invoke(instance: ObjectVal, parameters: List[PParam])(implicit callerCtx: Context) = ???
    }
    val funcCtx = StaticMethodContext(pMethod, callerCtx, allowThis = false)

    funcCtx.currentPosition = position

    funcCtx.setParameters(callerCtx, parameterDecls, params)
    lexicalValues.foreach {
      case (variableName, pVar) =>
        funcCtx.defineVariable(variableName, pVar)
    }
    invoke(funcCtx)
  }

  override def cleanup()(implicit ctx: Context) {
    super.cleanup()
    lexicalValues.foreach(_._2.release())
  }
}

class GlobalPClosure(instanceNum: Long, returnByRef: Boolean, parameterDecls: List[PParamDef], position: NodePosition,
                     lexicalValues: Seq[(String, PVar)], invoke: FunctionLikeContext => PAny)
  extends StdObjectVal(PClosure, instanceNum, new ExtendedLinkedHashMap[ObjectPropertyKey.Key]) with PClosure {

  lexicalValues.foreach(_._2.retain())

  override def call(params: List[PParam])(implicit callerCtx: Context): PAny = {
    val funcCtx = FunctionContext(NamespaceName("lambda-" + instanceNum), callerCtx)

    funcCtx.currentPosition = position

    funcCtx.setParameters(callerCtx, parameterDecls, params)
    lexicalValues.foreach {
      case (variableName, pVar) =>
        funcCtx.defineVariable(variableName, pVar)
    }
    invoke(funcCtx)
  }

  override def cleanup()(implicit ctx: Context) {
    super.cleanup()
    lexicalValues.foreach(_._2.release())
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

  override def methods = Map.empty

  def apply(returnByRef: Boolean, parameterDecls: List[PParamDef], instance: ObjectVal, lexicalValues: Seq[(String, PVar)],
            invoke: FunctionLikeContext => PAny)(implicit ctx: Context): ObjectVal = {
    val result = new InstancePClosure(ctx.global.instanceCounter.incrementAndGet(), returnByRef, parameterDecls,
      ctx.currentPosition, instance, lexicalValues, invoke)
    ctx.poolAutoRelease(result)
    result
  }

  def apply(returnByRef: Boolean, parameterDecls: List[PParamDef], pClass: PClass, lexicalValues: Seq[(String, PVar)],
            invoke: FunctionLikeContext => PAny)(implicit ctx: Context): ObjectVal = {
    val result = new StaticPClosure(ctx.global.instanceCounter.incrementAndGet(), returnByRef, parameterDecls,
      ctx.currentPosition, pClass, lexicalValues, invoke)
    ctx.poolAutoRelease(result)
    result
  }

  def apply(returnByRef: Boolean, parameterDecls: List[PParamDef], lexicalValues: Seq[(String, PVar)],
            invoke: FunctionLikeContext => PAny)(implicit ctx: Context): ObjectVal = {
    val result = new GlobalPClosure(ctx.global.instanceCounter.incrementAndGet(), returnByRef, parameterDecls,
      ctx.currentPosition, lexicalValues, invoke)
    ctx.poolAutoRelease(result)
    result
  }
}
