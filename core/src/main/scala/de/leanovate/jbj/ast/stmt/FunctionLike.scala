package de.leanovate.jbj.ast.stmt

import de.leanovate.jbj.ast.{ReferableExpr, NodePosition, Expr}
import de.leanovate.jbj.runtime.Context
import de.leanovate.jbj.runtime.value.{PAnyVal, VarRef}
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
              reference.evalRef(callerContext) match {
                case valueRef: VarRef =>
                  funcCtx.defineVariable(parameterDecl.variableName, valueRef)(callerPosition)
                case value: PAnyVal =>
                  callerContext.log.strict(callerPosition, "Only variables should be passed by reference")
                  funcCtx.defineVariable(parameterDecl.variableName, VarRef(value))(callerPosition)
              }
            case _ if parameterDecl.byRef =>
              throw new FatalErrorJbjException("Only variables can be passed by reference")(callerContext, callerPosition)
            case expr =>
              funcCtx.defineVariable(parameterDecl.variableName, VarRef(expr.eval(callerContext)))(callerPosition)
          }
        } else {
          funcCtx.defineVariable(parameterDecl.variableName, VarRef(parameterDecl.defaultVal(callerContext)))(callerPosition)
        }
    }
  }
}
