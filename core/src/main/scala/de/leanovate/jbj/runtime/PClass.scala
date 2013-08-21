package de.leanovate.jbj.runtime

import de.leanovate.jbj.ast.{Expr, ClassEntry, NamespaceName, NodePosition}
import de.leanovate.jbj.runtime.value._
import java.util.concurrent.atomic.AtomicLong
import de.leanovate.jbj.runtime.exception.FatalErrorJbjException
import scala.annotation.tailrec
import scala.collection.mutable
import de.leanovate.jbj.runtime.context.{MethodContext, StaticContext}
import de.leanovate.jbj.ast.expr.value.ScalarExpr
import de.leanovate.jbj.ast.expr.value.ScalarExpr
import de.leanovate.jbj.runtime.context.MethodContext
import scala.Some

trait PClass extends StaticContext {
  private val staticVariables = mutable.Map.empty[String, PVar]

  def classEntry: ClassEntry.Type

  def name: NamespaceName

  def superClass: Option[PClass]

  def newEmptyInstance(pClass: PClass)(implicit ctx: Context, callerPosition: NodePosition): ObjectVal = ObjectVal(pClass)

  def initializeInstance(instance: ObjectVal)(implicit ctx: Context, callerPosition: NodePosition) {}

  def newInstance(parameters: List[Expr])(implicit ctx: Context, callerPosition: NodePosition): ObjectVal

  def invokeMethod(ctx: Context, callerPosition: NodePosition, optInstance: Option[ObjectVal], methodName: String,
                   parameters: List[Expr]) = {
    findMethod(methodName) match {
      case Some(method) =>
        optInstance.map {
          instance =>
            method.invoke(ctx, callerPosition, instance, parameters)
        }.getOrElse {
          method.invokeStatic(ctx, callerPosition, this, parameters)
        }
      case None if optInstance.isDefined && (!ctx.isInstanceOf[MethodContext] ||
        ctx.asInstanceOf[MethodContext].methodName != "__call" ||
        ctx.asInstanceOf[MethodContext].instance.pClass != this) =>
        findMethod("__call") match {
          case Some(method) =>
            val parameterArray = ArrayVal(parameters.map {
              expr =>
                None -> expr.evalOld(ctx).copy(ctx)
            }: _*)(ctx)
            method.invoke(ctx, callerPosition, optInstance.get,
              ScalarExpr(StringVal(methodName)(ctx)) :: ScalarExpr(parameterArray) :: Nil)
          case None =>
            throw new FatalErrorJbjException("Call to undefined method %s::%s()".format(name.toString, methodName))(ctx)

        }
      case None =>
        throw new FatalErrorJbjException("Call to undefined method %s::%s()".format(name.toString, methodName))(ctx)
    }
  }

  def methods: Map[String, PMethod]

  def findMethod(methodName: String): Option[PMethod] = methods.get(methodName.toLowerCase)

  @tailrec
  final def isAssignableFrom(other: PClass): Boolean = this == other || (other.superClass match {
    case None => false
    case Some(s) => isAssignableFrom(s)
  })

  def findVariable(name: String) = staticVariables.get(name)

  def defineVariable(name: String, valueRef: PVar) {
    staticVariables.put(name, valueRef)
  }

  def undefineVariable(name: String) {
    staticVariables.remove(name)
  }
}