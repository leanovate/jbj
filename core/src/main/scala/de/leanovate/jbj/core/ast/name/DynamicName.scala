package de.leanovate.jbj.core.ast.name

import de.leanovate.jbj.core.ast.{Name, Expr}
import java.io.PrintStream
import de.leanovate.jbj.core.runtime.context.Context
import scala.xml.NodeSeq

case class DynamicName(expr: Expr) extends Name {
  override def evalName(implicit ctx: Context) = expr.eval.asVal.toStr.asString

  override def dump(out: PrintStream, ident: String) {
    super.dump(out, ident)
    expr.dump(out, ident + "  ")
  }

  def toXml: NodeSeq = expr.toXml
}
