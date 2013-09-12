/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.core.ast.expr

import de.leanovate.jbj.core.ast.RefExpr
import de.leanovate.jbj.runtime.context.Context
import de.leanovate.jbj.runtime.{ConstantReference, Reference}
import de.leanovate.jbj.runtime.value.{PVar, PAny}
import de.leanovate.jbj.runtime.exception.FatalErrorJbjException

trait CallRefExpr extends RefExpr {
  override def eval(implicit ctx: Context) = evalRef.byVal

  override def evalRef(implicit ctx: Context) = new ConstantReference(call) {
    override def unset() {
      throw new FatalErrorJbjException("Can't use function return value in write context")
    }
  }

  def call(implicit ctx: Context): PAny
}
