/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.converter.builders

import scala.text.Document._
import scala.text.{DocNil, Document}
import scala.collection.mutable
import de.leanovate.jbj.buildins.StandardExtension

class ProgCodeUnitBuilder(name: String, packageName: Option[String]) extends CodeUnitBuilder {
  private val functions = Seq.newBuilder[Document]

  private val statements = Seq.newBuilder[Document]

  private val directFunctions = mutable.Set.empty[String]

  private val localVariables = mutable.SortedSet.empty[String]

  def defineFunction(name: String, code: Document) {
    functions += code
    directFunctions.add(name)
  }

  def defineLocalVar(name: String) = localVariables.add(name)

  def isFunctionDirect(name: String) = directFunctions.contains(name)

  def addStatement(statement: Document) {
    statements += statement
  }

  directFunctions ++= StandardExtension.functions.map(_.name.toString)

  override def build() = {

    packageName.map(name =>
      s"package $name" :: break
    ).getOrElse(empty) :/:
      "import de.leanovate.jbj.runtime.context.Context" :/:
      "import de.leanovate.jbj.runtime._" :/:
      "import de.leanovate.jbj.runtime.value._" :/:
      "import de.leanovate.jbj.runtime.Operators._" :/:
      "import de.leanovate.jbj.buildins.StandardExtension._" :/:
      break :/:
      s"trait $name extends JbjCodeUnit {" :/:
      nest(2, empty :: functions.result().foldLeft(empty: Document) {
        (doc, func) => doc :/: func :: break
      } :/: "def exec(implicit ctx: Context) {" ::
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
