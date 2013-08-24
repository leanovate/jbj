package de.leanovate.jbj.core.ast

import java.io.PrintStream
import de.leanovate.jbj.core.runtime.context.Context
import scala.xml.NodeSeq

trait Name {
  def evalName(implicit ctx: Context): String

  def evalNamespaceName(implicit ctx: Context): NamespaceName = NamespaceName(evalName)

  def toXml: NodeSeq

  def dump(out: PrintStream, ident: String) {
    out.println(ident + getClass.getSimpleName)
  }
}
