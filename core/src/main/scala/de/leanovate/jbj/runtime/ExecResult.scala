package de.leanovate.jbj.runtime

import de.leanovate.jbj.ast.{Expr, NodePosition}


sealed trait ExecResult

object SuccessExecResult extends ExecResult

case class ReturnExecResult(expr: Option[Expr])(implicit val position: NodePosition) extends ExecResult

case class BreakExecResult(depth: Long)(implicit val position: NodePosition) extends ExecResult

case class ContinueExecResult(depth: Long)(implicit val position: NodePosition) extends ExecResult
