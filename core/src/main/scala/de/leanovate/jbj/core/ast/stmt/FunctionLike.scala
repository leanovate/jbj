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

  def setParameters(funcCtx: FunctionLikeContext, callerContext: Context, parameters: List[PParam]) {
    val parameterIt = parameters.iterator
    val arguments = Seq.newBuilder[PAny]
    parameterDecls.zipWithIndex.foreach {
      case (parameterDecl, index) =>
        if (parameterIt.hasNext) {
          val param = parameterIt.next()
          if (parameterDecl.byRef) {
            val pVar = param.byRef match {
              case pVar: PVar => pVar
              case pAny =>
                callerContext.log.strict("Only variables should be passed by reference")
                pAny.asVar
            }
            checkAndDefine(funcCtx, parameterDecl, index, pVar)
            arguments += pVar
          } else {
            val pVal = param.byVal
            checkAndDefine(funcCtx, parameterDecl, index, PVar(pVal))
            arguments += pVal
          }
        } else {
          parameterDecl.defaultVal(funcCtx) match {
            case Some(pVal) =>
              checkAndDefine(funcCtx, parameterDecl, index, PVar(pVal))
              arguments += pVal
            case None =>
              checkEmpty(funcCtx, parameterDecl, index)
              arguments += NullVal
          }
        }
    }
    parameterIt.foreach {
      param =>
        val pVal = param.byVal
        arguments += pVal
    }
    funcCtx.functionArguments = arguments.result()
  }

  private def checkEmpty(funcCtx: Context, parameterDecl: ParameterDecl, index: Int) {
    parameterDecl.typeHint.foreach(_.checkEmpty(index)(funcCtx))
  }

  private def checkAndDefine(funcCtx: Context, parameterDecl: ParameterDecl, index: Int, pVar: PVar) {
    parameterDecl.typeHint.foreach(_.check(pVar, index)(funcCtx))
    funcCtx.defineVariable(parameterDecl.variableName, pVar)
  }

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
