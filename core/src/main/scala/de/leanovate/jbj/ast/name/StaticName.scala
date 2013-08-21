package de.leanovate.jbj.ast.name

import de.leanovate.jbj.ast.Name
import java.io.PrintStream
import de.leanovate.jbj.runtime.context.Context

case class StaticName(name: String) extends Name {
  override def evalName(implicit ctx: Context) = name

  override def dump(out: PrintStream, ident: String) {
    super.dump(out, ident)
    out.println(ident + "  " + name)
  }
}
