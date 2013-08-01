package de.leanovate.jbj.runtime.buildin

import de.leanovate.jbj.runtime.value.UndefinedVal
import de.leanovate.jbj.runtime.{Value, Context, PFunction}
import de.leanovate.jbj.ast.{NamespaceName, NodePosition}

object RuntimeFunctions {
  val functions = Seq(
    BuildinFunction1("error_reporting", {
      case _ => UndefinedVal
    }),
    new PFunction() {
      def name = NamespaceName("define")

      def call(ctx: Context, callerPosition: NodePosition, parameters: List[Value]) = {
        parameters match {
          case name :: value :: Nil =>
            ctx.defineConstant(name.toStr.value, value, caseInsensitive = false)
          case name :: value :: caseInensitive :: Nil =>
            ctx.defineConstant(name.toStr.value, value, caseInensitive.toBool.value)
          case _ => ctx.log.warn(callerPosition, "var_dump() expects at least 2 parameter, %d given".format(parameters.length))
        }
        Left(UndefinedVal)
      }
    }
  )
}
