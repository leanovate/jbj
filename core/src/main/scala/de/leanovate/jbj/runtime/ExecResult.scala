package de.leanovate.jbj.runtime

import de.leanovate.jbj.ast.NodePosition


sealed trait ExecResult

object SuccessExecResult extends ExecResult

case class ReturnExecResult(value: Value) extends ExecResult

case class BreakExecResult(depth: Long)(implicit var position:NodePosition) extends ExecResult

case class ContinueExecResult(depth: Long)(implicit var position:NodePosition) extends ExecResult
