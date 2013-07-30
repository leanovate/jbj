package de.leanovate.jbj.ast.stmt

import de.leanovate.jbj.runtime.Value

case class StaticAssignment(variableName: String, initial: Value)
