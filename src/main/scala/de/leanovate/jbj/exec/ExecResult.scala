package de.leanovate.jbj.exec

import de.leanovate.jbj.ast.Value

sealed trait ExecResult

case class SuccessExecResult() extends ExecResult

case class ReturnExecResult(value: Value) extends ExecResult

case class BreakExecResult() extends ExecResult

case class ContinueExecResult() extends ExecResult
