/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.core.ast.stmt

import de.leanovate.jbj.core.ast.{Stmt, ReferableExpr, Expr}
import de.leanovate.jbj.core.runtime._
import de.leanovate.jbj.core.runtime.value.{NullVal, PVar}
import de.leanovate.jbj.core.runtime.exception.FatalErrorJbjException
import de.leanovate.jbj.core.runtime.BreakExecResult
import de.leanovate.jbj.core.runtime.ReturnExecResult
import scala.Some
import de.leanovate.jbj.core.runtime.context.{FunctionLikeContext, Context}
import de.leanovate.jbj.core.ast.decl.ParameterDecl

trait FunctionLike extends BlockLike {

  def parameterDecls: List[ParameterDecl]

  def setParameters(funcCtx: Context, callerContext: Context, parameters: List[Expr]) {
    val parameterIt = parameters.iterator
    parameterDecls.zipWithIndex.foreach {
      case (parameterDecl, index) =>
        if (parameterIt.hasNext) {
          parameterIt.next() match {
            case reference: ReferableExpr if parameterDecl.byRef =>
              val pVar = reference.evalRef(callerContext).asVar match {
                case pVar: PVar => pVar
                case pAny =>
                  callerContext.log.strict("Only variables should be passed by reference")
                  pAny.asVar
              }
              checkAndDefine(funcCtx, parameterDecl, index, pVar)
            case _ if parameterDecl.byRef =>
              throw new FatalErrorJbjException("Only variables can be passed by reference")(callerContext)
            case expr =>
              checkAndDefine(funcCtx, parameterDecl, index, PVar(expr.eval(callerContext).asVal))
          }
        } else {
          checkAndDefine(funcCtx, parameterDecl, index, PVar(parameterDecl.defaultVal(funcCtx)))
        }
    }
  }

  private def checkAndDefine(funcCtx: Context, parameterDecl: ParameterDecl, index: Int, pVar: PVar) {
    parameterDecl.typeHint.foreach(_.check(pVar, index)(funcCtx))
    funcCtx.defineVariable(parameterDecl.variableName, pVar)
  }

  def perform(funcCtx: FunctionLikeContext, returnByRef: Boolean, stmts: List[Stmt]) = {
    val result = execStmts(stmts)(funcCtx) match {
      case SuccessExecResult => NullVal
      case ret: ReturnExecResult => ret.expr match {
        case Some(referable: ReferableExpr) if returnByRef => referable.evalRef(funcCtx).asVar match {
          case pVar: PVar =>
            funcCtx.callerContext.poolAutoRelease(pVar)
            pVar
          case pAny =>
            funcCtx.log.notice("Only variable references should be returned by reference")
            val pVar = pAny.asVar
            funcCtx.callerContext.poolAutoRelease(pVar)
            pVar
        }
        case Some(expr) if returnByRef =>
          funcCtx.log.notice("Only variable references should be returned by reference")
          val pVar = expr.eval(funcCtx).asVar
          funcCtx.callerContext.poolAutoRelease(pVar)
          pVar
        case Some(expr) => expr.eval(funcCtx).asVal
        case None => NullVal
      }
      case result: BreakExecResult =>
        throw new FatalErrorJbjException("Cannot break/continue %d level".format(result.depth))(funcCtx)
      case result: ContinueExecResult =>
        throw new FatalErrorJbjException("Cannot break/continue %d level".format(result.depth))(funcCtx)
    }
    funcCtx.cleanup()
    result
  }

}
