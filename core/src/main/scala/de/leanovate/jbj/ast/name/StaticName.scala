package de.leanovate.jbj.ast.name

import de.leanovate.jbj.ast.Name
import de.leanovate.jbj.runtime.Context
import java.io.PrintStream

case class StaticName(name: String) extends Name {
  override def evalName(implicit ctx: Context) = name

  override def dump(out: PrintStream, ident: String) {
    super.dump(out, ident)
    out.println(ident + "  " + name)
  }
}
