package de.leanovate.jbj.runtime

import de.leanovate.jbj.ast.NodePosition
import de.leanovate.jbj.runtime.value.PAnyVal


sealed trait ExecResult

object SuccessExecResult extends ExecResult

case class ReturnExecResult(value: PAnyVal) extends ExecResult

case class BreakExecResult(depth: Long)(implicit val position:NodePosition) extends ExecResult

case class ContinueExecResult(depth: Long)(implicit val position:NodePosition) extends ExecResult
