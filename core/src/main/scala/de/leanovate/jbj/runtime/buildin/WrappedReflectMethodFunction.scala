package de.leanovate.jbj.runtime.buildin

import de.leanovate.jbj.runtime.{Context, PFunction}
import de.leanovate.jbj.ast.{Expr, NodePosition, NamespaceName}
import scala.reflect.runtime.universe._
import de.leanovate.jbj.runtime.adapter.{Converter, ParameterAdapter}
import de.leanovate.jbj.runtime.value.PAnyVal
import de.leanovate.jbj.runtime.exception.FatalErrorJbjException

case class WrappedReflectMethodFunction[T, S <: PAnyVal](name: NamespaceName,
                                                       methodMirror: MethodMirror,
                                                       parameterAdapters: Seq[ParameterAdapter[_]],
                                                       resultConverter: Converter[T, S]) extends PFunction {
  lazy val requiredParameterCount = parameterAdapters.map(_.requiredCount).sum

  override def call(parameters: List[Expr])(implicit callerCtx: Context, callerPosition: NodePosition) = {
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
