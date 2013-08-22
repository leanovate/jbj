package de.leanovate.jbj.ast.stmt

import de.leanovate.jbj.ast.{Stmt, ReferableExpr, Expr}
import de.leanovate.jbj.runtime._
import de.leanovate.jbj.runtime.value.{NullVal, PVar}
import de.leanovate.jbj.runtime.exception.FatalErrorJbjException
import de.leanovate.jbj.runtime.BreakExecResult
import de.leanovate.jbj.runtime.ReturnExecResult
import scala.Some
import de.leanovate.jbj.runtime.context.Context

trait FunctionLike extends BlockLike {

  def parameterDecls: List[ParameterDecl]

  def setParameters(funcCtx: Context, callerContext: Context, parameters: List[Expr]) {
    val parameterIt = parameters.iterator
    parameterDecls.foreach {
      parameterDecl =>
        if (parameterIt.hasNext) {
          parameterIt.next() match {
            case reference: ReferableExpr if parameterDecl.byRef =>
              val pVar = reference.evalRef(callerContext).asVar match {
                case pVar: PVar => pVar
                case pAny =>
                  callerContext.log.strict("Only variables should be passed by reference")
                  pAny.asVar
              }
              funcCtx.getVariable(parameterDecl.variableName).ref = pVar
            case _ if parameterDecl.byRef =>
              throw new FatalErrorJbjException("Only variables can be passed by reference")(callerContext)
            case expr =>
              funcCtx.getVariable(parameterDecl.variableName).value = expr.eval(callerContext).asVal
          }
        } else {
          funcCtx.getVariable(parameterDecl.variableName).value = parameterDecl.defaultVal(callerContext)
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
            funcCtx.log.notice("Only variable references should be returned by reference")
            pAny.asVar
        }
        case Some(expr) if returnByRef =>
          funcCtx.log.notice("Only variable references should be returned by reference")
          expr.eval(funcCtx).asVar
        case Some(expr) => expr.eval(funcCtx).asVal
        case None => NullVal
      }
      case result: BreakExecResult =>
        throw new FatalErrorJbjException("Cannot break/continue %d level".format(result.depth))(funcCtx)
      case result: ContinueExecResult =>
        throw new FatalErrorJbjException("Cannot break/continue %d level".format(result.depth))(funcCtx)
    }

}
