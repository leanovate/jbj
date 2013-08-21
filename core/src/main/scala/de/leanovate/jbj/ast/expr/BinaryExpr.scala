package de.leanovate.jbj.ast.expr

import de.leanovate.jbj.ast.Expr
import java.io.PrintStream

trait BinaryExpr extends Expr {
  def left: Expr

  def right: Expr

  override def dump(out: PrintStream, ident: String) {
    out.println(ident + getClass.getSimpleName)
    left.dump(out, ident + "  ")
    right.dump(out, ident + "  ")
  }
}
