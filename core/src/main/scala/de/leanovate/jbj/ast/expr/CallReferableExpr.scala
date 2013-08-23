package de.leanovate.jbj.ast.expr

import de.leanovate.jbj.ast.ReferableExpr
import de.leanovate.jbj.runtime.context.Context
import de.leanovate.jbj.runtime.Reference
import de.leanovate.jbj.runtime.value.PAny
import de.leanovate.jbj.runtime.exception.FatalErrorJbjException

trait CallReferableExpr extends ReferableExpr {
  override def eval(implicit ctx: Context) = call.asVal

  override def evalRef(implicit ctx: Context) = new Reference {
    val result = call

    def asVal = result.asVal

    def asVar = result

    def assign(pAny: PAny) = pAny

    def unset() {
      throw new FatalErrorJbjException("Can't use function return value in write context")
    }
  }

  def call(implicit ctx: Context): PAny
}
