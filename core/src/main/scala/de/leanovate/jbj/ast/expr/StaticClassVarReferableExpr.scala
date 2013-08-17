package de.leanovate.jbj.ast.expr

import de.leanovate.jbj.ast.{Name, ReferableExpr}
import de.leanovate.jbj.runtime.Context
import de.leanovate.jbj.runtime.value.{PVar, NullVal, PAny}
import de.leanovate.jbj.runtime.exception.FatalErrorJbjException

case class StaticClassVarReferableExpr(className: Name, variableName: Name) extends ReferableExpr {
  override def eval(implicit ctx:Context) = {
    val name = className.evalNamespaceName
    ctx.global.findClass(name).map {
      pClass =>
        pClass.findVariable(variableName.evalName).map(_.value).getOrElse(NullVal)
    }.getOrElse {
      throw new FatalErrorJbjException("Class '%s' not found".format(name.toString))
    }
  }

  override def evalVar(implicit ctx: Context) = {
    val name = className.evalNamespaceName
    ctx.global.findClass(name).map {
      pClass =>
        val varName = variableName.evalName
        pClass.findVariable(varName).getOrElse {
          val result = PVar()
          pClass.defineVariable(varName, result)
          result
        }
    }.getOrElse {
      throw new FatalErrorJbjException("Class '%s' not found".format(name.toString))
    }
  }

  override def assignVar(pAny: PAny)(implicit ctx: Context) {
    val name = className.evalNamespaceName
    ctx.global.findClass(name).map {
      pClass =>
        pClass.defineVariable(variableName.evalName, pAny.asVar)
    }.getOrElse {
      throw new FatalErrorJbjException("Class '%s' not found".format(name.toString))
    }
  }

  override def unsetVar(implicit ctx:Context) {
    val name = className.evalNamespaceName
    ctx.global.findClass(name).map {
      pClass =>
        pClass.undefineVariable(variableName.evalName)
    }.getOrElse {
      throw new FatalErrorJbjException("Class '%s' not found".format(name.toString))
    }
  }
}
