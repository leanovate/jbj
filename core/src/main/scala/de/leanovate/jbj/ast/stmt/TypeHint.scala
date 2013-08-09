package de.leanovate.jbj.ast.stmt

import de.leanovate.jbj.ast.{NamespaceName, Name}

sealed trait TypeHint

object ArrayTypeHint extends TypeHint

object CallableTypeHint extends TypeHint

case class ClassTypeHint(className: NamespaceName) extends TypeHint
