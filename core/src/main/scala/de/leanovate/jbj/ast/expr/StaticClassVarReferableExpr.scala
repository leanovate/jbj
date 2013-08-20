package de.leanovate.jbj.ast.expr

import de.leanovate.jbj.ast.{Name, ReferableExpr}
import de.leanovate.jbj.runtime.{Reference, Context}
import de.leanovate.jbj.runtime.value.{PVar, NullVal, PAny}
import de.leanovate.jbj.runtime.exception.FatalErrorJbjException

case class StaticClassVarReferableExpr(className: Name, variableName: Name) extends ReferableExpr {
  override def eval(implicit ctx: Context) = {
    val name = className.evalNamespaceName
    ctx.global.findClass(name).map {
      pClass =>
        pClass.findVariable(variableName.evalName).map(_.value).getOrElse(NullVal)
    }.getOrElse {
      throw new FatalErrorJbjException("Class '%s' not found".format(name.toString))
    }
  }

  override def evalRef(implicit ctx: Context) = new Reference {
    val name = className.evalNamespaceName
    val pClass = ctx.global.findClass(name).getOrElse {
      throw new FatalErrorJbjException("Class '%s' not found".format(name.toString))
    }
    val varName = variableName.evalName

    def asVal = pClass.findVariable(variableName.evalName).map(_.value).getOrElse(NullVal)


    def asVar = pClass.findVariable(varName).getOrElse {
      val result = PVar()
      pClass.defineVariable(varName, result)
      result
    }

    def assign(pAny: PAny) = {
      pClass.defineVariable(varName, pAny.asVar)
      pAny
    }

    def unset() {
      pClass.undefineVariable(varName)
    }
  }
}
