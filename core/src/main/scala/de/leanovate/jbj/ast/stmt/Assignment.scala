package de.leanovate.jbj.ast.stmt

import de.leanovate.jbj.ast.{Name, FilePosition, Expr}

case class Assignment(position: FilePosition, variableName: Name, expr: Option[Expr])
