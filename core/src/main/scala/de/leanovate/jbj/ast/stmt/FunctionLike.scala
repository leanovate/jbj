package de.leanovate.jbj.ast.stmt

import de.leanovate.jbj.ast.{Stmt, ReferableExpr, NodePosition, Expr}
import de.leanovate.jbj.runtime._
import de.leanovate.jbj.runtime.value.{NullVal, PVar}
import de.leanovate.jbj.runtime.exception.FatalErrorJbjException
import de.leanovate.jbj.runtime.BreakExecResult
import de.leanovate.jbj.runtime.ReturnExecResult
import scala.Some

trait FunctionLike extends BlockLike {

  def parameterDecls: List[ParameterDecl]

  def setParameters(funcCtx: Context, callerContext: Context, callerPosition: NodePosition, parameters: List[Expr]) {
    val parameterIt = parameters.iterator
    parameterDecls.foreach {
      parameterDecl =>
        if (parameterIt.hasNext) {
          parameterIt.next() match {
            case reference: ReferableExpr if parameterDecl.byRef =>
              val pVar = reference.evalRef(callerContext).asVar match {
                case pVar: PVar => pVar
                case pAny =>
                  callerContext.log.strict(callerPosition, "Only variables should be passed by reference")
                  pAny.asVar
              }
              funcCtx.defineVariable(parameterDecl.variableName, pVar)(callerPosition)
            case _ if parameterDecl.byRef =>
              throw new FatalErrorJbjException("Only variables can be passed by reference")(callerContext, callerPosition)
            case expr =>
              funcCtx.defineVariable(parameterDecl.variableName, PVar(expr.evalOld(callerContext)))(callerPosition)
          }
        } else {
          funcCtx.defineVariable(parameterDecl.variableName, PVar(parameterDecl.defaultVal(callerContext)))(callerPosition)
        }
    }
  }

  def perform(funcCtx: Context, returnByRef: Boolean, stmts: List[Stmt]) =
    execStmts(stmts)(funcCtx) match {
      case SuccessExecResult => NullVal
      case ret: ReturnExecResult => ret.expr match {
        case Some(referable: ReferableExpr) if returnByRef => referable.evalRef(funcCtx).asVar match {
          case pVar: PVar => pVar
          case pAny =>
            funcCtx.log.notice(ret.position, "Only variable references should be returned by reference")
            pAny.asVar
        }
        case Some(expr) if returnByRef =>
          funcCtx.log.notice(ret.position, "Only variable references should be returned by reference")
          expr.evalOld(funcCtx).asVar
        case Some(expr) => expr.evalOld(funcCtx)
        case None => NullVal
      }
      case result: BreakExecResult =>
        throw new FatalErrorJbjException("Cannot break/continue %d level".format(result.depth))(funcCtx, result.position)
      case result: ContinueExecResult =>
        throw new FatalErrorJbjException("Cannot break/continue %d level".format(result.depth))(funcCtx, result.position)
    }

}
