package de.leanovate.jbj.transcode.builders

import scala.text.Document._
import scala.text.Document

class CodeUnitBuilder(name: String, packageName: Option[String]) extends Builder {
  private val statements = Seq.newBuilder[Document]

  def addStatement(statement: Document) {
    statements += statement
  }

  override def build() = {

    packageName.map(name =>
      s"package $name" :: break
    ).getOrElse(empty) :/:
      "import de.leanovate.jbj.runtime.context.Context" :/:
      "import de.leanovate.jbj.runtime.JbjCodeUnit" :/:
      break :/:
      s"object $name extends JbjCodeUnit {" :/:
      nest(2, empty :/: "def exec(implicit ctx: Context) {" :/:
        nest(2, statements.result().foldLeft(empty: Document) {
          (doc, stmt) => doc :/: stmt
        }) :/: "}" :: empty) :/:
      "}" :/: empty
  }
}
