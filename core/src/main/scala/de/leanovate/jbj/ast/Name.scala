package de.leanovate.jbj.ast

import de.leanovate.jbj.runtime.Context
import java.io.PrintStream

trait Name {
  def evalName(implicit ctx: Context): String

  def evalNamespaceName(implicit  ctx:Context) : NamespaceName = NamespaceName(evalName)

  def dump(out: PrintStream, ident: String) {
    out.println(ident + getClass.getSimpleName)
  }
}
