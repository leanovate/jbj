/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.core.buildin

import de.leanovate.jbj.runtime.PFunction
import de.leanovate.jbj.core.ast.{Expr, NamespaceName}
import scala.reflect.runtime.universe._
import de.leanovate.jbj.runtime.adapter.{Converter, ParameterAdapter}
import de.leanovate.jbj.runtime.value.{BooleanVal, PAny}
import de.leanovate.jbj.runtime.exception.FatalErrorJbjException
import de.leanovate.jbj.runtime.context.Context
import java.lang.reflect.InvocationTargetException

case class WrappedReflectMethodFunction[T, S <: PAny](name: NamespaceName,
                                                      methodMirror: MethodMirror,
                                                      parameterAdapters: Seq[ParameterAdapter[_]],
                                                      resultConverter: Converter[T, S],
                                                      warnExactly: Boolean) extends PFunction {
  lazy val requiredParameterCount = parameterAdapters.map(_.requiredCount).sum

  override def call(parameters: List[Expr])(implicit callerCtx: Context) = {
    val argBuilder = Seq.newBuilder[Any]
    var notEnough = false

    val unMapped = parameterAdapters.foldLeft(parameters) {
      (params, adapter) =>
        adapter.adapt(params) match {
          case Some((arg, remain)) =>
            argBuilder += arg
            remain
          case None =>
            notEnough = true
            Nil
        }
    }
    if (!unMapped.isEmpty && warnExactly) {
      callerCtx.log.warn("%s() expects exactly %d parameter, %d given".format(name.toString, requiredParameterCount, parameters.size))
      BooleanVal.FALSE
    } else if (notEnough) {
      if (warnExactly) {
        callerCtx.log.warn("%s() expects exactly %d parameter, %d given".format(name.toString, requiredParameterCount, parameters.size))
        BooleanVal.FALSE
      } else {
        throw new FatalErrorJbjException("%s() expects at least %d parameter, %d given".format(name.toString, requiredParameterCount, parameters.size))
      }
    } else {
      try {
        val result = methodMirror.apply(argBuilder.result(): _*).asInstanceOf[T]
        resultConverter.toJbj(result)
      } catch {
        case e: InvocationTargetException =>
          throw e.getCause
      }
    }
  }
}
