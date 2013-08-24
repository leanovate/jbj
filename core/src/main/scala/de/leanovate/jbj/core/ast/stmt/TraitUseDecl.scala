package de.leanovate.jbj.core.ast.stmt

import de.leanovate.jbj.core.ast.NamespaceName

case class TraitUseDecl(traits:List[NamespaceName]) extends ClassMemberDecl {
}
