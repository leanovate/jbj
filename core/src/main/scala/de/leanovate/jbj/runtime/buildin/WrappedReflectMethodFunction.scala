package de.leanovate.jbj.runtime.buildin

import de.leanovate.jbj.runtime.PFunction
import de.leanovate.jbj.ast.{Expr, NamespaceName}
import scala.reflect.runtime.universe._
import de.leanovate.jbj.runtime.adapter.{Converter, ParameterAdapter}
import de.leanovate.jbj.runtime.value.PAny
import de.leanovate.jbj.runtime.exception.FatalErrorJbjException
import de.leanovate.jbj.runtime.context.Context

case class WrappedReflectMethodFunction[T, S <: PAny](name: NamespaceName,
                                                      methodMirror: MethodMirror,
                                                      parameterAdapters: Seq[ParameterAdapter[_]],
                                                      resultConverter: Converter[T, S]) extends PFunction {
  lazy val requiredParameterCount = parameterAdapters.map(_.requiredCount).sum

  override def call(parameters: List[Expr])(implicit callerCtx: Context) = {
    val argBuilder = Seq.newBuilder[Any]

    parameterAdapters.foldLeft(parameters) {
      (params, adapter) =>
        adapter.adapt(params) match {
          case Some((arg, remain)) =>
            argBuilder += arg
            remain
          case None =>
            throw new FatalErrorJbjException("%s() expects at least %d parameter, 0 given".format(name.toString, requiredParameterCount, parameters.size))
        }
    }
    val result = methodMirror.apply(argBuilder.result(): _*).asInstanceOf[T]
    resultConverter.toJbj(result)
  }
}
