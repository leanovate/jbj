/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.core.ast.decl

import de.leanovate.jbj.core.ast.NamespaceName
import de.leanovate.jbj.core.runtime.value.{ObjectVal, PVar}
import de.leanovate.jbj.core.runtime.context.{FunctionLikeContext, MethodContext, Context}
import de.leanovate.jbj.core.runtime.exception.CatchableFatalError

sealed trait TypeHint {
  def check(pVar: PVar, index: Int)(implicit ctx: Context)
}

object ArrayTypeHint extends TypeHint {
  override def check(pVar: PVar, index: Int)(implicit ctx: Context) {

  }
}

object CallableTypeHint extends TypeHint {
  override def check(pVar: PVar, index: Int)(implicit ctx: Context) {

  }
}

case class ClassTypeHint(className: NamespaceName) extends TypeHint {
  override def check(pVar: PVar, index: Int)(implicit ctx: Context) {
    ctx.global.findInterfaceOrClass(className, autoload = false) match {
      case Some(Left(pInterface)) =>
        pVar.value match {
          case obj: ObjectVal if obj.instanceOf(pInterface) =>
          case obj: ObjectVal =>
            ctx match {
              case methodCtx: FunctionLikeContext =>
                throw CatchableFatalError("Argument %d passed to %s must implement interface %s, instance of %s given, called in %s on line %d and defined".
                  format(index + 1, methodCtx.functionSignature,
                  pInterface.name.toString, obj.pClass.name.toString,
                  methodCtx.callerContext.currentPosition.fileName,
                  methodCtx.callerContext.currentPosition.line))
            }
          case _ =>
        }
      case Some(Right(pClass)) =>
      case _ =>
        pVar.value match {
          case obj: ObjectVal =>
            ctx match {
              case methodCtx: FunctionLikeContext =>
                throw CatchableFatalError("Argument %d passed to %s must be an instance of %s, instance of %s given, called in %s on line %d and defined".
                  format(index + 1, methodCtx.functionSignature,
                  className.toString, obj.pClass.name.toString,
                  methodCtx.callerContext.currentPosition.fileName,
                  methodCtx.callerContext.currentPosition.line))
            }
          case _ =>
        }
    }
  }
}
