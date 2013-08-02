package de.leanovate.jbj.ast

import de.leanovate.jbj.runtime.Context
import java.io.PrintStream

trait Name {
  def evalName(ctx: Context): String

  def evalNamespaceName(ctx:Context) : NamespaceName = NamespaceName(evalName(ctx))

  def dump(out: PrintStream, ident: String) {
    out.println(ident + getClass.getSimpleName)
  }
}
