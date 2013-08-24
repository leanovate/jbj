package de.leanovate.jbj.core.ast.stmt

import de.leanovate.jbj.core.ast.NamespaceName

sealed trait TypeHint

object ArrayTypeHint extends TypeHint

object CallableTypeHint extends TypeHint

case class ClassTypeHint(className: NamespaceName) extends TypeHint
