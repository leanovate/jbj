package de.leanovate.jbj.core.ast.stmt

import de.leanovate.jbj.core.ast.{HasNodePosition, Node}
import de.leanovate.jbj.core.runtime.value.ObjectVal
import de.leanovate.jbj.core.runtime.context.Context
import de.leanovate.jbj.core.runtime.PClass

trait ClassMemberDecl extends Node with HasNodePosition {
  def initializeClass(pClass: ClassDeclStmt)(implicit ctx: Context) {}

  def initializeInstance(instance: ObjectVal, pClass: PClass)(implicit ctx: Context) {}
}
