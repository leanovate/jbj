package de.leanovate.jbj.ast.stmt

import de.leanovate.jbj.ast.Node
import de.leanovate.jbj.runtime.value.ObjectVal
import de.leanovate.jbj.runtime.Context

trait ClassMemberDecl extends Node {
  def initializeInstance(instance:ObjectVal)(implicit ctx:Context) {}
}
