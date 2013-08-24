package de.leanovate.jbj.core.ast.name

import de.leanovate.jbj.core.ast.Name
import java.io.PrintStream
import de.leanovate.jbj.core.runtime.context.Context
import scala.xml.{Text, NodeSeq}

case class StaticName(name: String) extends Name {
  override def evalName(implicit ctx: Context) = name

  override def dump(out: PrintStream, ident: String) {
    super.dump(out, ident)
    out.println(ident + "  " + name)
  }

  def toXml: NodeSeq = Text(name)
}
