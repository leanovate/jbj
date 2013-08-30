/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.core.ast.expr

import de.leanovate.jbj.core.ast._
import de.leanovate.jbj.core.runtime.Reference
import de.leanovate.jbj.core.runtime.value.PAny
import de.leanovate.jbj.core.runtime.exception.FatalErrorJbjException
import de.leanovate.jbj.core.runtime.context.Context
import scala.Some

case class NewReferableExpr(className: Name, parameters: List[Expr]) extends ReferableExpr {
  override def eval(implicit ctx: Context) = ctx.global.findInterfaceOrClass(className.evalNamespaceName) match {
    case Some(Right(pClass)) =>
      pClass.newInstance(parameters)
    case Some(Left(pInterface)) =>
      throw new FatalErrorJbjException("Cannot instantiate interface %s".format(pInterface.name.toString))
    case None =>
      throw new FatalErrorJbjException("Class '%s' not found".format(className.evalNamespaceName.toString))
  }

  override def evalRef(implicit ctx: Context) = new Reference {
    val result = eval

    def isDefined = !asVal.isNull

    def asVal = result.asVal

    def asVar = result.asVar

    def assign(pAny: PAny)(implicit ctx:Context) = {
      throw new FatalErrorJbjException("Can't use new result in write context")
    }

    def unset() {
      throw new FatalErrorJbjException("Can't use new result in write context")
    }
  }

  override def visit[R](visitor: NodeVisitor[R]) = visitor(this).thenChildren(parameters)
}
