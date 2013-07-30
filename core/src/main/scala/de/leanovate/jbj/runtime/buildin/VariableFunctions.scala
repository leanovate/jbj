package de.leanovate.jbj.runtime.buildin

import de.leanovate.jbj.runtime.value.{UndefinedVal, BooleanVal}
import de.leanovate.jbj.runtime.{Value, Context, PFunction}
import de.leanovate.jbj.ast.NodePosition

object VariableFunctions {
  val functions = Seq(
    BuildinFunction1("isset", {
      case Some(value) => BooleanVal(!value.isUndefined)
    }),
    new PFunction() {
      def name = "var_dump"

      def call(ctx: Context, callerPosition: NodePosition, parameters: List[Value]) = {
        parameters match {
          case params if !params.isEmpty => params.foreach(_.toDump(ctx.out))
          case _ => ctx.log.warn(callerPosition, "var_dump() expects at least 1 parameter, 0 given")
        }
        UndefinedVal
      }
    }
  )
}
