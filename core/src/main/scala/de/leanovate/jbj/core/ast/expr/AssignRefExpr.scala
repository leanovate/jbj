/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.core.ast.expr

import de.leanovate.jbj.core.ast.{StaticInitializer, Expr, RefExpr}
import de.leanovate.jbj.runtime.{VariableReference, Reference}
import de.leanovate.jbj.runtime.value.{PVar, PAny}
import de.leanovate.jbj.runtime.exception.FatalErrorJbjException
import de.leanovate.jbj.runtime.context.{MethodContext, StaticContext, Context}
import de.leanovate.jbj.core.ast.name.StaticName

case class AssignRefExpr(reference: RefExpr, expr: Expr) extends RefExpr with StaticInitializer {
  override def initializeStatic(staticCtx: StaticContext)(implicit ctx: Context) {
    ctx match {
      case MethodContext(_, _, _) =>
        reference match {
          case VariableRefExpr(StaticName("this")) =>
            throw new FatalErrorJbjException("Cannot re-assign $this")
          case _ =>
        }
      case _ =>
    }
  }

  override def eval(implicit ctx: Context) = evalRef.byVal

  override def evalRef(implicit ctx: Context) = new Reference {
    val result = reference.evalRef.assign(expr.eval.asVal.copy)

    override def isConstant = true

    override def isDefined = !byVal.isNull

    override def byVal = result.asVal

    override def byVar = result.asVar

    override def assign(pAny: PAny)(implicit ctx: Context) = pAny

    override def unset() {
      throw new FatalErrorJbjException("Can't use function return value in write context")
    }
  }
}
