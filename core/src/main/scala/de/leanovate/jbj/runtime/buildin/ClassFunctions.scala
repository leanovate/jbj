package de.leanovate.jbj.runtime.buildin

import de.leanovate.jbj.runtime.value._
import de.leanovate.jbj.runtime.value.StringVal
import scala.Some
import de.leanovate.jbj.runtime.{Value, PClass, PFunction}
import de.leanovate.jbj.ast.NamespaceName

object ClassFunctions {
  val functions: Seq[PFunction] = Seq(
    BuildinFunction1("get_class", {
      case (_, _, Some(obj: ObjectVal)) => StringVal(obj.pClass.name.toString)
      case (ctx, callerPosition, Some(_)) =>
        ctx.log.warn(callerPosition, "get_class() expects parameter 1 to be object, string given")
        BooleanVal.FALSE
    }),
    BuildinFunction1("class_exists", {
      case (ctx, callerPosition, Some(name)) =>
        BooleanVal(ctx.global.findClassOrAutoload(NamespaceName(name.toStr.value))(callerPosition).isDefined)
    }),
    BuildinFunction1("get_class_methods", {
      case (ctx, callerPosition, Some(name)) =>
        ctx.global.findClassOrAutoload(NamespaceName(name.toStr.value))(callerPosition).map {
          pClass =>
            ArrayVal(pClass.methods.map(None -> StringVal(_)): _*)
        }.getOrElse(NullVal)
    })
  )
}
