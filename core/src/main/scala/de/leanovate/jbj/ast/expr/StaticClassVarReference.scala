package de.leanovate.jbj.ast.expr

import de.leanovate.jbj.ast.{Name, Reference}
import de.leanovate.jbj.runtime.{Context}
import de.leanovate.jbj.runtime.value.{ValueRef, NullVal, ValueOrRef, Value}
import de.leanovate.jbj.runtime.exception.FatalErrorJbjException

case class StaticClassVarReference(className: Name, variableName: Name) extends Reference {
  override def evalRef(implicit ctx: Context) = {
    val name = className.evalNamespaceName
    ctx.global.findClass(name).map {
      pClass =>
        pClass.findVariable(variableName.evalName).getOrElse(NullVal)
    }.getOrElse {
      throw new FatalErrorJbjException("Class '%s' not found".format(name.toString))
    }
  }

  override def assignRef(valueOrRef: ValueOrRef)(implicit ctx: Context) {
    val name = className.evalNamespaceName
    ctx.global.findClass(name).map {
      pClass =>
        pClass.defineVariable(variableName.evalName, ValueRef(valueOrRef.value))
    }.getOrElse {
      throw new FatalErrorJbjException("Class '%s' not found".format(name.toString))
    }
  }
}
