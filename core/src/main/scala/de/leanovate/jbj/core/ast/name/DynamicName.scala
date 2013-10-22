/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.core.ast.name

import de.leanovate.jbj.core.ast.{NodeVisitor, Name, Expr}
import de.leanovate.jbj.runtime.context.Context
import de.leanovate.jbj.runtime.NamespaceName
import de.leanovate.jbj.runtime.value.{StringVal, ObjectVal}

case class DynamicName(expr: Expr) extends Name {
  override def evalName(implicit ctx: Context) = expr.eval.asVal.toStr.asString

  override def evalNameStrict(implicit ctx: Context) = expr.eval.concrete match {
    case StringVal(str) => Some(str)
    case _ => None
  }
  override def evalNamespaceName(implicit ctx: Context): NamespaceName = expr.eval.concrete match {
    case obj: ObjectVal => obj.pClass.name
    case pVal => NamespaceName(pVal.toStr.asString)
  }

  override def visit[R](visitor: NodeVisitor[R]) = visitor(this).thenChild(expr)

  override def phpStr = expr.phpStr
}
