/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.runtime


sealed trait ExecResult

object SuccessExecResult extends ExecResult

case class ReturnExecResult(param: Option[PParam])(implicit val position: NodePosition) extends ExecResult

case class BreakExecResult(depth: Long)(implicit val position: NodePosition) extends ExecResult

case class ContinueExecResult(depth: Long)(implicit val position: NodePosition) extends ExecResult
