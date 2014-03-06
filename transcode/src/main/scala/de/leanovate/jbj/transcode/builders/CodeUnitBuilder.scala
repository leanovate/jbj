package de.leanovate.jbj.transcode.builders

import scala.text.Document._
import scala.text.Document

class CodeUnitBuilder(name: String) extends Builder {
  private val statements = Seq.newBuilder[Document]

  def addStatement(statement: Document) {
    statements += statement
  }

  override def build() = {

    "import de.leanovate.jbj.runtime.context.Context" :/:
      s"object $name {" :/:
      nest(2, statements.result().foldLeft(empty: Document) {
        (doc, stmt) => doc :/: stmt
      }) :/:
      "}" :/: empty
  }
}
