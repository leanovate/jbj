package de.leanovate.jbj.ast.stmt

import de.leanovate.jbj.ast.{HasNodePosition, Node}
import de.leanovate.jbj.runtime.value.ObjectVal
import de.leanovate.jbj.runtime.context.Context
import de.leanovate.jbj.runtime.PClass

trait ClassMemberDecl extends Node with HasNodePosition {
  def initializeInstance(instance: ObjectVal, pClass: PClass)(implicit ctx: Context) {}
}
