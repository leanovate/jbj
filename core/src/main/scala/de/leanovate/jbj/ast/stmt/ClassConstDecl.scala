package de.leanovate.jbj.ast.stmt

import de.leanovate.jbj.ast.Stmt
import de.leanovate.jbj.runtime.Context
import de.leanovate.jbj.runtime.value.ObjectVal

case class ClassConstDecl(assignments: List[StaticAssignment]) extends ClassMemberDecl {
}
