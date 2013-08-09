package de.leanovate.jbj.ast.expr

import de.leanovate.jbj.ast.{Name, Reference}
import de.leanovate.jbj.runtime.{Context}
import de.leanovate.jbj.runtime.value.{ValueOrRef, Value}

case class StaticClassVarReference(className: Name, variableName: Name) extends Reference {
  override def evalRef(implicit ctx: Context) = ???

  override def assignRef(valueOrRef: ValueOrRef)(implicit ctx: Context) {}
}
