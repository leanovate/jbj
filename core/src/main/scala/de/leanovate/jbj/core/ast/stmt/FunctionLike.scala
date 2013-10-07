/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.core.ast.stmt

import de.leanovate.jbj.core.ast._
import de.leanovate.jbj.runtime._
import de.leanovate.jbj.runtime.value.{PAny, NullVal, PVar}
import de.leanovate.jbj.runtime.exception.FatalErrorJbjException
import de.leanovate.jbj.runtime.context.{FunctionLikeContext, Context}
import de.leanovate.jbj.core.ast.decl.ParameterDecl
import de.leanovate.jbj.runtime.BreakExecResult
import de.leanovate.jbj.runtime.ReturnExecResult
import scala.Some
import de.leanovate.jbj.runtime.ContinueExecResult
import de.leanovate.jbj.runtime.types.PParam

trait FunctionLike extends BlockLike {

  def parameterDecls: List[ParameterDecl]

  def perform(funcCtx: FunctionLikeContext, returnByRef: Boolean, stmts: List[Stmt]) = {
    val result = execStmts(stmts)(funcCtx) match {
      case SuccessExecResult => NullVal
      case ret: ReturnExecResult => ret.param match {
        case Some(param) if param.hasRef && returnByRef => param.byRef match {
          case pVar: PVar =>
            funcCtx.callerContext.poolAutoRelease(pVar)
            pVar
          case pAny =>
            funcCtx.log.notice("Only variable references should be returned by reference")
            val pVar = pAny.asVar
            funcCtx.callerContext.poolAutoRelease(pVar)
            pVar
        }
        case Some(param) if returnByRef =>
          funcCtx.log.notice("Only variable references should be returned by reference")
          val pVar = PVar(param.byVal)
          funcCtx.callerContext.poolAutoRelease(pVar)
          pVar
        case Some(param) =>
          val pVal = param.byVal
          funcCtx.callerContext.poolAutoRelease(pVal)
          pVal
        case None => NullVal
      }
      case result: BreakExecResult =>
        throw new FatalErrorJbjException("Cannot break/continue %d level".format(result.depth))(funcCtx)
      case result: ContinueExecResult =>
        throw new FatalErrorJbjException("Cannot break/continue %d level".format(result.depth))(funcCtx)
    }
    funcCtx.autoRelease()
    funcCtx.cleanup()
    result
  }

}
