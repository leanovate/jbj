package de.leanovate.jbj.ast.stmt

import de.leanovate.jbj.ast.Expr

case class StaticAssignment(variableName: String, initial: Option[Expr])
