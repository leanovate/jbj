package de.leanovate.jbj.ast.stmt

import de.leanovate.jbj.ast.NamespaceName
import de.leanovate.jbj.runtime.value.ObjectVal

case class TraitUseDecl(traits:List[NamespaceName]) extends ClassMemberDecl {
}
