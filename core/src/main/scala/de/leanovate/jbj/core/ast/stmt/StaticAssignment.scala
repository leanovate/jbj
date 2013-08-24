package de.leanovate.jbj.core.ast.stmt

import de.leanovate.jbj.core.ast.Expr

case class StaticAssignment(variableName: String, initial: Option[Expr])
