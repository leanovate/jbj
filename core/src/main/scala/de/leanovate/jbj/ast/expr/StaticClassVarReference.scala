package de.leanovate.jbj.ast.expr

import de.leanovate.jbj.ast.{Name, Reference}
import de.leanovate.jbj.runtime.{Context, Value}

case class StaticClassVarReference(className: Name, variableName: Name) extends Reference {
  override def eval(implicit ctx: Context) = ???

  override def assign(value: Value)(implicit ctx: Context) {}
}
