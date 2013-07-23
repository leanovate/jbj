package de.leanovate.jbj.runtime.buildin

import de.leanovate.jbj.runtime.value.{UndefinedVal, BooleanVal}
import de.leanovate.jbj.runtime.{Value, Context, Function}

object VariableFunctions {
  val functions = Seq(
    BuildinFunction1("isset", {
      case Some(value) => BooleanVal(!value.isUndefined)
    }),
    new Function() {
      def name = "var_dump"

      def call(ctx: Context, parameters: List[Value]) = {
        parameters match {
          case params if !params.isEmpty => params.foreach(_.toDump(ctx.out))
          case _ => ctx.log.warn("var_dump() expects at least 1 parameter")
        }
        UndefinedVal
      }
    }
  )
}
