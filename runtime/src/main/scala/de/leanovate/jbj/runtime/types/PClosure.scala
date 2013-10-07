/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.runtime.types

import de.leanovate.jbj.runtime.{NodePosition, NamespaceName}
import de.leanovate.jbj.runtime.value._
import de.leanovate.jbj.runtime.context.{FunctionContext, FunctionLikeContext, Context}
import de.leanovate.jbj.runtime.exception.FatalErrorJbjException

class PClosure(instanceNum: Long, returnByRef: Boolean, parameterDecls: List[PParamDef], position: NodePosition,
               lexicalValues: Seq[(String, PVar)], invoke: FunctionLikeContext => PAny)
  extends StdObjectVal(PClosure, instanceNum, new ExtendedLinkedHashMap[ObjectPropertyKey.Key]) {

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

  override def destructInstance(instance: ObjectVal)(implicit ctx: Context) {}

  override def properties = Map.empty

  override def methods = Map.empty

  def apply(returnByRef: Boolean, parameterDecls: List[PParamDef], lexicalValues: Seq[(String, PVar)],
            invoke: FunctionLikeContext => PAny)(implicit ctx: Context): ObjectVal =
    new PClosure(ctx.global.instanceCounter.incrementAndGet(), returnByRef, parameterDecls,
      ctx.currentPosition, lexicalValues, invoke)
}
