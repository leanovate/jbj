package de.leanovate.jbj.runtime.buildin

import de.leanovate.jbj.runtime.value._
import de.leanovate.jbj.runtime.value.StringVal
import scala.Some
import de.leanovate.jbj.runtime.PFunction

object ClassFunctions {
  val functions: Seq[PFunction] = Seq(
    BuildinFunction1("get_class", {
      case (_, _, Some(obj: ObjectVal)) => StringVal(obj.pClass.name.toString)
      case (ctx, callerPosition, Some(_)) =>
        ctx.log.warn(callerPosition, "get_class() expects parameter 1 to be object, string given")
        BooleanVal.FALSE
    })
  )
}
