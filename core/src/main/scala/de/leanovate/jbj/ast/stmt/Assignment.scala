package de.leanovate.jbj.ast.stmt

import de.leanovate.jbj.ast.Expr

case class Assignment(variableName: String, expr: Expr)
