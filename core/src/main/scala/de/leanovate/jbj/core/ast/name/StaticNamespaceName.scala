package de.leanovate.jbj.core.ast.name

import de.leanovate.jbj.core.ast.{Name, NamespaceName}
import java.io.PrintStream
import de.leanovate.jbj.core.runtime.context.Context
import scala.xml.{Text, NodeSeq}

case class StaticNamespaceName(namespaceName: NamespaceName) extends Name {
  override def evalName(implicit ctx: Context) = evalNamespaceName.toString

  override def evalNamespaceName(implicit ctx: Context) = namespaceName

  override def dump(out: PrintStream, ident: String) {
    super.dump(out, ident)
    out.println(ident + "  " + namespaceName.toString)
  }

  def toXml: NodeSeq = Text(namespaceName.toString)
}
