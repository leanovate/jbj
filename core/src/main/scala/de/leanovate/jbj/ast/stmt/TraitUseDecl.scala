package de.leanovate.jbj.ast.stmt

import de.leanovate.jbj.ast.NamespaceName

case class TraitUseDecl(traits:List[NamespaceName]) extends ClassMemberDecl {
}
