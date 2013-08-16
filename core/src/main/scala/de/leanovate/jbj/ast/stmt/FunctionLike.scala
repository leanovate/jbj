package de.leanovate.jbj.ast.stmt

import de.leanovate.jbj.ast.{ReferableExpr, NodePosition, Expr}
import de.leanovate.jbj.runtime.Context
import de.leanovate.jbj.runtime.value.PVar
import de.leanovate.jbj.runtime.exception.FatalErrorJbjException

trait FunctionLike {

  def parameterDecls: List[ParameterDecl]

  def setParameters(funcCtx: Context, callerContext: Context, callerPosition: NodePosition, parameters: List[Expr]) {
    val parameterIt = parameters.iterator
    parameterDecls.foreach {
      parameterDecl =>
        if (parameterIt.hasNext) {
          parameterIt.next() match {
            case reference: ReferableExpr if parameterDecl.byRef =>
              funcCtx.defineVariable(parameterDecl.variableName, reference.evalVar(callerContext))(callerPosition)
            case _ if parameterDecl.byRef =>
              throw new FatalErrorJbjException("Only variables can be passed by reference")(callerContext, callerPosition)
            case expr =>
              funcCtx.defineVariable(parameterDecl.variableName, PVar(expr.eval(callerContext)))(callerPosition)
          }
        } else {
          funcCtx.defineVariable(parameterDecl.variableName, PVar(parameterDecl.defaultVal(callerContext)))(callerPosition)
        }
    }
  }
}
