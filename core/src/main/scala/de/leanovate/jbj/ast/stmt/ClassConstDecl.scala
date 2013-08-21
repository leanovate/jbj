package de.leanovate.jbj.ast.stmt

import de.leanovate.jbj.ast.Stmt
import de.leanovate.jbj.runtime.value.ObjectVal
import de.leanovate.jbj.runtime.context.Context

case class ClassConstDecl(assignments: List[StaticAssignment]) extends ClassMemberDecl {
}
