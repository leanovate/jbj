package de.leanovate.jbj.runtime.buildin

import de.leanovate.jbj.runtime.value.{ValueOrRef, Value, NullVal}
import de.leanovate.jbj.runtime.{Context, PFunction}
import de.leanovate.jbj.ast.{Expr, NamespaceName, NodePosition}

object RuntimeFunctions {
  val functions : Seq[PFunction]= Seq(
    BuildinFunction1("error_reporting", {
      case _ => NullVal
    }),
    new PFunction() {
      def name = NamespaceName(relative = false, "define")

      def call(ctx: Context, callerPosition: NodePosition, parameters: List[Expr]) = {
        parameters.map(_.eval(ctx)) match {
          case name :: value :: Nil =>
            ctx.defineConstant(name.value.toStr.asString, value.value, caseInsensitive = false)
          case name :: value :: caseInensitive :: Nil =>
            ctx.defineConstant(name.value.toStr.asString, value.value, caseInensitive.value.toBool.asBoolean)
          case _ => ctx.log.warn(callerPosition, "var_dump() expects at least 2 parameter, %d given".format(parameters.length))
        }
        NullVal
      }
    }
  )
}
