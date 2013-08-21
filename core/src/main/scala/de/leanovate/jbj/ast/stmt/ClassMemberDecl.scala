package de.leanovate.jbj.ast.stmt

import de.leanovate.jbj.ast.{HasNodePosition, Node}
import de.leanovate.jbj.runtime.value.ObjectVal
import de.leanovate.jbj.runtime.context.Context

trait ClassMemberDecl extends Node with HasNodePosition {
  def initializeInstance(instance: ObjectVal)(implicit ctx: Context) {}
}
