/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.core.ast.expr

import de.leanovate.jbj.core.ast._
import de.leanovate.jbj.runtime.Reference
import de.leanovate.jbj.runtime.value.PAny
import de.leanovate.jbj.runtime.exception.FatalErrorJbjException
import de.leanovate.jbj.runtime.context.Context
import scala.Some

case class NewRefExpr(className: Name, parameters: List[Expr]) extends RefExpr {
  override def eval(implicit ctx: Context) = ctx.global.findInterfaceOrClass(className.evalNamespaceName, autoload = true) match {
    case Some(Right(pClass)) =>
      pClass.newInstance(parameters.map(ExprParam.apply))
    case Some(Left(pInterface)) =>
      throw new FatalErrorJbjException("Cannot instantiate interface %s".format(pInterface.name.toString))
    case None =>
      throw new FatalErrorJbjException("Class '%s' not found".format(className.evalNamespaceName.toString))
  }

  override def evalRef(implicit ctx: Context) = new Reference {
    val result = eval

    override def isConstant = false

    override def isDefined = !asVal.isNull

    override def asVal = result.asVal

    override def asVar = result.asVar

    override def :=(pAny: PAny)(implicit ctx: Context) = {
      throw new FatalErrorJbjException("Can't use new result in write context")
    }

    override def unset()(implicit ctx: Context) {
      throw new FatalErrorJbjException("Can't use new result in write context")
    }
  }

  override def phpStr = "new " + className.phpStr + parameters.map(_.phpStr).mkString("(", ", ", ")")

  override def accept[R](visitor: NodeVisitor[R]) = visitor(this).thenChildren(parameters)
}
