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
    val optClass = ctx.global.findClass(name)
    val varName = variableName.evalName

    def asVar = optClass.map {
      pClass =>
        pClass.findVariable(varName).getOrElse {
          val result = PVar()
          pClass.defineVariable(varName, result)
          result
        }
    }.getOrElse {
      throw new FatalErrorJbjException("Class '%s' not found".format(name.toString))
    }

    def assign(pAny: PAny) = {
      ctx.global.findClass(name).map {
        pClass =>
          pClass.defineVariable(varName, pAny.asVar)
      }.getOrElse {
        throw new FatalErrorJbjException("Class '%s' not found".format(name.toString))
      }
      pAny
    }

    def unset() {
      ctx.global.findClass(name).map {
        pClass =>
          pClass.undefineVariable(varName)
      }.getOrElse {
        throw new FatalErrorJbjException("Class '%s' not found".format(name.toString))
      }
    }
  }

  override def evalVar(implicit ctx: Context) = {
    evalRef.asVar
  }

  override def assignVar(pAny: PAny)(implicit ctx: Context) {
    evalRef.assign(pAny)
  }

  override def unsetVar(implicit ctx: Context) {
    evalRef.unset()
  }
}
