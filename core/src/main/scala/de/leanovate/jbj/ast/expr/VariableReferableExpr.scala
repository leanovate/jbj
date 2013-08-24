package de.leanovate.jbj.ast.expr

import de.leanovate.jbj.ast.{Name, ReferableExpr}
import de.leanovate.jbj.runtime.Reference
import java.io.PrintStream
import de.leanovate.jbj.runtime.value.{NullVal, PVal, PAny, PVar}
import de.leanovate.jbj.runtime.context.Context

case class VariableReferableExpr(variableName: Name) extends ReferableExpr {
  override def eval(implicit ctx: Context) = {
    val name = variableName.evalName
    ctx.findVariable(name).getOrElse {
      ctx.log.notice("Undefined variable: %s".format(name))
      NullVal
    }
  }

  override def evalRef(implicit ctx: Context) = new Reference {
    val name = variableName.evalName

    def asVal = ctx.findVariable(name).map(_.value).getOrElse(NullVal)

    def asVar = ctx.findOrDefineVariable(name)

    def assign(pAny: PAny): PAny = {
      pAny match {
        case pVar: PVar =>
          ctx.defineVariable(name, pVar)
        case pVal: PVal =>
          ctx.findOrDefineVariable(name).value = pVal
      }
      pAny
    }

    def unset() {
      ctx.undefineVariable(name)
    }
  }

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
