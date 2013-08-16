package de.leanovate.jbj.runtime.buildin

import de.leanovate.jbj.runtime.value._
import de.leanovate.jbj.runtime._
import de.leanovate.jbj.ast.NodePosition
import scala.collection.mutable
import de.leanovate.jbj.runtime.IntArrayKey
import de.leanovate.jbj.runtime.annotations.GlobalFunction

object ArrayFunctions extends WrappedFunctions {
  @GlobalFunction
  def count(value: PAnyVal): Int = value match {
    case array: ArrayVal => array.keyValues.size
    case _ => 1
  }

  @GlobalFunction
  def array_merge(values: PAnyVal*)(implicit ctx: Context, callerPosition: NodePosition): PAnyVal = {
    if (values.isEmpty) {
      ctx.log.warn(callerPosition, "array_merge() expects at least 1 parameter, 0 given")
      NullVal
    } else {
      var count: Long = -1
      var builder = mutable.LinkedHashMap.newBuilder[ArrayKey, PAny]
      values.foreach {
        case array: ArrayVal =>
          array.keyValues.map {
            case (IntArrayKey(_), value) =>
              count += 1
              builder += IntArrayKey(count) -> value
            case (key, value) =>
              builder += key -> value
          }
        case _ =>
      }
      new ArrayVal(builder.result())
    }
  }
}
