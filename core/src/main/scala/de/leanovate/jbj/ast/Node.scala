package de.leanovate.jbj.ast

import java.io.PrintStream

trait Node {
  def dump(out: PrintStream, ident: String) {
    out.println(ident + getClass.getSimpleName)
  }
}
