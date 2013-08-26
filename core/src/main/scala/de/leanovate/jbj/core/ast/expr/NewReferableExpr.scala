package de.leanovate.jbj.core.ast.expr

import de.leanovate.jbj.core.ast._
import de.leanovate.jbj.core.runtime.Reference
import de.leanovate.jbj.core.runtime.value.{PAny, NullVal}
import de.leanovate.jbj.core.runtime.exception.FatalErrorJbjException
import de.leanovate.jbj.core.runtime.context.Context
import scala.Some

case class NewReferableExpr(className: Name, parameters: List[Expr]) extends ReferableExpr {
  override def eval(implicit ctx: Context) = ctx.global.findClass(className.evalNamespaceName) match {
    case Some(pClass) =>
      pClass.newInstance(parameters)
    case None =>
      ctx.log.fatal("Class '%s' not found".format(className.toString))
      NullVal
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
