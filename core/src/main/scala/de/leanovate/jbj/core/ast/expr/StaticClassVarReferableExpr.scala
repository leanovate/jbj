package de.leanovate.jbj.core.ast.expr

import de.leanovate.jbj.core.ast.{Name, ReferableExpr}
import de.leanovate.jbj.core.runtime.Reference
import de.leanovate.jbj.core.runtime.value.{PVar, NullVal, PAny}
import de.leanovate.jbj.core.runtime.exception.FatalErrorJbjException
import de.leanovate.jbj.core.runtime.context.Context

case class StaticClassVarReferableExpr(className: Name, variableName: Name) extends ReferableExpr {
  override def eval(implicit ctx: Context) = {
    val name = className.evalNamespaceName
    ctx.global.findClass(name).map {
      pClass =>
        val staticClassContext = ctx.global.staticContext(pClass)
        staticClassContext.findVariable(variableName.evalName).map(_.value).getOrElse(NullVal)
    }.getOrElse {
      throw new FatalErrorJbjException("Class '%s' not found".format(name.toString))
    }
  }

  override def evalRef(implicit ctx: Context) = new Reference {
    val name = className.evalNamespaceName
    val pClass = ctx.global.findClass(name).getOrElse {
      throw new FatalErrorJbjException("Class '%s' not found".format(name.toString))
    }
    val staticClassContext = ctx.global.staticContext(pClass)
    val varName = variableName.evalName

    def isDefined = !asVal.isNull

    def asVal = staticClassContext.findVariable(variableName.evalName).map(_.value).getOrElse(NullVal)

    def asVar = staticClassContext.findVariable(varName).getOrElse {
      val result = PVar()
      staticClassContext.defineVariable(varName, result)
      result
    }

    def assign(pAny: PAny)(implicit ctx: Context) = {
      staticClassContext.defineVariable(varName, pAny.asVar)
      pAny
    }

    def unset() {
      staticClassContext.undefineVariable(varName)
    }
  }

  override def toXml() =
    <StaticClassVarReferableExpr>
      <class>
        {className.toXml}
      </class>
      <name>
        {variableName.toXml}
      </name>
    </StaticClassVarReferableExpr>
}
