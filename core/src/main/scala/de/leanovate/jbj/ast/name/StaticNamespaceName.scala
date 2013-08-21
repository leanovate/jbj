package de.leanovate.jbj.ast.name

import de.leanovate.jbj.ast.{Name, NamespaceName}
import java.io.PrintStream
import de.leanovate.jbj.runtime.context.Context

case class StaticNamespaceName(namespaceName: NamespaceName) extends Name {
  override def evalName(implicit ctx: Context) = evalNamespaceName.toString

  override def evalNamespaceName(implicit ctx: Context) = namespaceName

  override def dump(out: PrintStream, ident: String) {
    super.dump(out, ident)
    out.println(ident + "  " + namespaceName.toString)
  }
}
