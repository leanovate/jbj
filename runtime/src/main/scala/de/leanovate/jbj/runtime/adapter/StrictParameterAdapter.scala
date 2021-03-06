package de.leanovate.jbj.runtime.adapter

import de.leanovate.jbj.runtime.value.PAny
import de.leanovate.jbj.runtime.types.PParam
import de.leanovate.jbj.runtime.context.Context

case class StrictParameterAdapter[T, S <: PAny](parameterIdx: Int,
                                                converter: Converter[T, S],
                                                errorHandlers: ParameterAdapter.ErrorHandlers)
  extends ParameterAdapter[T] {
  override def requiredCount = 1

  override def adapt(parameters: Iterator[PParam])(implicit ctx: Context) = {
    if (parameters.hasNext) {
      val head = parameters.next()
      converter.toScala(head) match {
        case Some(v) => v
        case None =>
          errorHandlers.conversionError(converter.typeName, head.byVal.typeName(simple = false), parameterIdx)
          converter.missingValue
      }
    } else {
      errorHandlers.parameterMissing(parameterIdx)
      converter.missingValue
    }
  }
}
