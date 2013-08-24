package de.leanovate.jbj.ast.expr

import de.leanovate.jbj.ast.{Name, ReferableExpr}
import java.io.PrintStream
import de.leanovate.jbj.runtime.value.NullVal
import de.leanovate.jbj.runtime.context.Context

case class VariableReferableExpr(variableName: Name) extends ReferableExpr {
  override def eval(implicit ctx: Context) = {
    val name = variableName.evalName
    ctx.findVariable(name).getOrElse {
      ctx.log.notice("Undefined variable: %s".format(name))
      NullVal
    }
  }

  override def evalRef(implicit ctx: Context) = ctx.getVariable(variableName.evalName)

  override def dump(out: PrintStream, ident: String) {
    super.dump(out, ident)
    variableName.dump(out, ident + "  ")
  }

  override def toXml =
    <VariableReferableExpr>
      <name>
        {variableName.toXml}
      </name>
    </VariableReferableExpr>
}
