package de.leanovate.jbj.converter.builders

import scala.text.Document
import scala.text.Document._

object StatementBuilder {
  def inlineStmt(inlineText: String): Document =
    if (inlineText.isEmpty)
      empty
    else
      text("inline(") :: LiteralBuilder.build(inlineText) :: text(")")
}
