package de.leanovate.jbj.converter.builders

import scala.text.Document._
import scala.text.{DocNil, Document}
import scala.collection.mutable

class CodeUnitBuilder(name: String, packageName: Option[String]) extends Builder {
  private val statements = Seq.newBuilder[Document]

  private val localVariables = mutable.Set.empty[String]

  def defineLocalVar(name: String) = if (!localVariables.contains(name)) {
    statements += text( s"""val $name = lvar("${name}")""")
    localVariables.add(name)
  }

  def addStatement(statement: Document) {
    statements += statement
  }

  override def build() = {

    packageName.map(name =>
      s"package $name" :: break
    ).getOrElse(empty) :/:
      "import de.leanovate.jbj.runtime.context.Context" :/:
      "import de.leanovate.jbj.runtime.Operators._" :/:
      "import de.leanovate.jbj.runtime.JbjCodeUnit" :/:
      break :/:
      s"trait $name extends JbjCodeUnit {" :/:
      nest(2, empty :/: "def exec(implicit ctx: Context) {" :/:
        nest(2, statements.result().filter(_ != DocNil).foldLeft(empty: Document) {
          (doc, stmt) => doc :/: stmt
        }) :/: "}" :: empty) :/:
      "}" :/: break :/:
      s"object $name extends $name {" :/: "}" :/: empty
  }
}
