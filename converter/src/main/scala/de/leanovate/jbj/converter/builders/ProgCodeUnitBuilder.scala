package de.leanovate.jbj.converter.builders

import scala.text.Document._
import scala.text.{DocNil, Document}
import scala.collection.mutable

class ProgCodeUnitBuilder(name: String, packageName: Option[String]) extends CodeUnitBuilder {
  private val statements = Seq.newBuilder[Document]

  private val localVariables = mutable.SortedSet.empty[String]

  def defineLocalVar(name: String) = localVariables.add(name)

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
      nest(2, empty :/: "def exec(implicit ctx: Context) {" ::
        nest(2,
          localVariables.foldLeft(empty: Document) {
            (doc, localVariable) =>
              doc :/: s"""val $localVariable = lvar("${localVariable}")""" :: empty
          } :/: statements.result().filter(_ != DocNil).foldLeft(empty: Document) {
            (doc, stmt) => doc :/: stmt
          }) :/: "}" :: empty) :/:
      "}" :/: break :/:
      s"object $name extends $name {" :/: "}" :/: empty
  }
}
