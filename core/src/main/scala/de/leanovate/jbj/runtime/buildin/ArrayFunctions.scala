package de.leanovate.jbj.runtime.buildin

import de.leanovate.jbj.runtime.value.{Value, NullVal, ArrayVal, IntegerVal}
import de.leanovate.jbj.runtime._
import de.leanovate.jbj.ast.NodePosition
import scala.collection.mutable
import de.leanovate.jbj.runtime.IntArrayKey
import de.leanovate.jbj.ast.NamespaceName
import scala.Some

object ArrayFunctions {
  val functions: Seq[PFunction] = Seq(
    BuildinFunction1("count", {
      case (_, _, Some(array: ArrayVal)) => array.count
      case (_, _, Some(_)) => IntegerVal(1)
    }),
    new PFunction() {
      def name = NamespaceName(relative = false, "array_merge")

      def call(ctx: Context, callerPosition: NodePosition, parameters: List[Value]) =
        parameters match {
          case params if !params.isEmpty =>
            var count: Long = -1
            var builder = mutable.LinkedHashMap.newBuilder[ArrayKey, Value]
            params.foreach {
              case array: ArrayVal =>
                array.keyValues.map {
                  case (IntArrayKey(_), value) =>
                    count += 1
                    builder += IntArrayKey(count) -> value
                  case (key, value) =>
                    builder += key -> value
                }
            }
            Left(new ArrayVal(builder.result()))
          case _ =>
            ctx.log.warn(callerPosition, "array_merge() expects at least 1 parameter, 0 given")
            Left(NullVal)
        }
    })
}
