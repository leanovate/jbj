package de.leanovate.jbj.transcode.builders

import scala.text.Document
import scala.text.Document._

object StatementBuilder {
  def inlineStmt(inlineText: String): Document =
    text("echo(") :: LiteralBuilder.build(inlineText) :: text(")")
}
