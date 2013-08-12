package de.leanovate.jbj.runtime.buildin

import de.leanovate.jbj.runtime.value._
import de.leanovate.jbj.runtime.value.StringVal
import scala.Some
import de.leanovate.jbj.runtime.PFunction
import de.leanovate.jbj.ast.NamespaceName

object ClassFunctions {
  val functions: Seq[PFunction] = Seq(
    BuildinFunction1("get_class", {
      case (ctx, _, Some(obj: ObjectVal)) => StringVal(obj.pClass.name.toString)(ctx)
      case (ctx, callerPosition, Some(_)) =>
        ctx.log.warn(callerPosition, "get_class() expects parameter 1 to be object, string given")
        BooleanVal.FALSE
    }),
    BuildinFunction1("class_exists", {
      case (ctx, callerPosition, Some(name)) =>
        BooleanVal(ctx.global.findClassOrAutoload(NamespaceName(name.toStr(ctx).asString(ctx)))(callerPosition).isDefined)
    }),
    BuildinFunction1("get_class_methods", {
      case (ctx, callerPosition, Some(name)) =>
        ctx.global.findClassOrAutoload(NamespaceName(name.toStr(ctx).asString(ctx)))(callerPosition).map {
          pClass =>
            ArrayVal(pClass.methods.values.map {
              method => None -> StringVal(method.name)(ctx)
            }.toSeq: _*)(ctx)
        }.getOrElse(NullVal)
    }),
    BuildinFunction2("is_subclass_of", {
      case (ctx, callerPosition, Some(obj: ObjectVal), Some(name)) =>
        obj.pClass.superClass.flatMap {
          superClass =>
            ctx.global.findClassOrAutoload(NamespaceName(name.toStr(ctx).asString(ctx)))(callerPosition).map {
              pClass =>
                BooleanVal(pClass.isAssignableFrom(superClass))
            }
        }.getOrElse(BooleanVal.FALSE)
      case (ctx, callerPosition, Some(_), Some(_)) =>
        BooleanVal.FALSE
    }),
    BuildinFunction1("get_parent_class", {
      case (ctx, callerPosition, Some(obj: ObjectVal)) =>
        obj.pClass.superClass.map {
          superClass => StringVal(superClass.name.toString)(ctx)
        }.getOrElse(BooleanVal.FALSE)
      case (ctx, callerPosition, Some(name)) =>
        ctx.global.findClassOrAutoload(NamespaceName(name.toStr(ctx).asString(ctx)))(callerPosition).flatMap {
          pClass =>
            pClass.superClass.map {
              superClass => StringVal(superClass.name.toString)(ctx)
            }
        }.getOrElse(BooleanVal.FALSE)
    })
  )
}
