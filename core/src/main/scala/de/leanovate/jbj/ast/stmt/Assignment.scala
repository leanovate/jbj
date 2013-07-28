package de.leanovate.jbj.ast.stmt

import de.leanovate.jbj.ast.{Reference, FilePosition, Expr}

case class Assignment(position: FilePosition, reference: Reference, expr: Option[Expr])
