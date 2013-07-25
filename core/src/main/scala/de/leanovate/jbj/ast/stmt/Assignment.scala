package de.leanovate.jbj.ast.stmt

import de.leanovate.jbj.ast.{FilePosition, Expr}

case class Assignment(position: FilePosition, variableName: String, expr: Option[Expr])
