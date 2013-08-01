package de.leanovate.jbj.runtime.buildin

import de.leanovate.jbj.runtime.value.{ArrayVal, UndefinedVal, IntegerVal}
import de.leanovate.jbj.runtime.{Value, Context, PFunction}
import de.leanovate.jbj.ast.{NamespaceName, NodePosition}
import de.leanovate.jbj.runtime.value.ArrayVal.IntArrayKey

object ArrayFunctions {
  val functions = Seq(
    BuildinFunction1("count", {
      case Some(array: ArrayVal) => array.count
      case Some(_) => IntegerVal(1)
    }),
    new PFunction() {
      def name = NamespaceName("array_merge")

      def call(ctx: Context, callerPosition: NodePosition, parameters: List[Value]) =
        parameters match {
          case params if !params.isEmpty =>
            var count: Long = -1
            Left(ArrayVal(params.map {
              case array: ArrayVal =>
                array.keyValues.map {
                  case (IntArrayKey(_), value) =>
                    count += 1
                    IntArrayKey(count) -> value
                  case (key, value) => key -> value
                }
            }.flatten))
          case _ =>
            ctx.log.warn(callerPosition, "array_merge() expects at least 1 parameter, 0 given")
            Left(UndefinedVal)
        }
    })
}
