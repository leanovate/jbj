package de.leanovate.jbj.ast.name

import de.leanovate.jbj.ast.{Name, NamespaceName}
import de.leanovate.jbj.runtime.Context
import java.io.PrintStream

case class StaticNamespaceName(namespaceName: NamespaceName, relative:Boolean = true) extends Name {
  override def evalName(implicit ctx: Context) = evalNamespaceName.toString

  override def evalNamespaceName(implicit ctx: Context) = namespaceName

  override def dump(out: PrintStream, ident: String) {
    super.dump(out, ident)
    out.println(ident + "  " + namespaceName.toString)
  }
}
