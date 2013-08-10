package de.leanovate.jbj.ast.stmt

import de.leanovate.jbj.ast.{Reference, NodePosition, Expr}
import de.leanovate.jbj.runtime.Context
import de.leanovate.jbj.runtime.value.ValueRef
import de.leanovate.jbj.runtime.exception.FatalErrorJbjException

trait FunctionLike {

  def parameterDecls: List[ParameterDecl]

  def setParameters(funcCtx: Context, callerContext: Context, callerPosition: NodePosition, parameters: List[Expr]) {
    val parameterIt = parameters.iterator
    parameterDecls.foreach {
      parameterDecl =>
        if (parameterIt.hasNext) {
          parameterIt.next() match {
            case reference: Reference if parameterDecl.byRef =>
              reference.evalRef(callerContext) match {
                case valueRef: ValueRef =>
                  funcCtx.defineVariable(parameterDecl.variableName, valueRef)(callerPosition)
                case _ =>
                  throw new FatalErrorJbjException("Only variables can be passed by reference")(callerContext, callerPosition)
              }
            case _ if parameterDecl.byRef =>
              throw new FatalErrorJbjException("Only variables can be passed by reference")(callerContext, callerPosition)
            case expr =>
              funcCtx.defineVariable(parameterDecl.variableName, ValueRef(expr.eval(callerContext)))(callerPosition)
          }
        } else {
          funcCtx.defineVariable(parameterDecl.variableName, ValueRef(parameterDecl.defaultVal(callerContext)))(callerPosition)
        }
    }
  }
}
