package de.leanovate.jbj.ast.stmt

import de.leanovate.jbj.ast.FilePosition
import de.leanovate.jbj.ast.expr.ScalarExpr
import de.leanovate.jbj.runtime.Value

case class StaticAssignment(position: FilePosition, variableName: String, initial: Value)
