/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.converter.builders

import scala.collection.mutable
import scala.text.{DocNil, Document}
import scala.text.Document._
import de.leanovate.jbj.runtime.NamespaceName
import de.leanovate.jbj.core.ast.decl.ParameterDecl

class FunctionCodeUnitBuilder(parent: CodeUnitBuilder, name: NamespaceName, parameters: List[ParameterDecl]) extends CodeUnitBuilder {
  private val parameterNames = parameters.map(_.name)
  private val localVariables = mutable.SortedSet.empty[String]
  private val statements = Seq.newBuilder[Document]

  override def isFunctionDirect(name: String) = parent.isFunctionDirect(name)

  override def defineFunction(name: String, code: Document) = parent.defineFunction(name, code)

  override def defineLocalVar(name: String) = if (!parameterNames.contains(name)) localVariables.add(name)

  def addStatement(statement: Document) {
    statements += statement
  }

  override def build() = {
    s"def $name(" ::
      parameters.map(_.name :: ": PVal" :: empty).reduceOption(_ :: ", " :: _).getOrElse(empty) ::
      """)(implicit callerCtx: Context): PAny =""" ::
      nest(2, break :: s"""functionCtx("$name",callerCtx) {""" ::
        nest(2, " ctx:Context =>" :/:
          s"_$name(" :: parameters.map(p => s"""lvar("${p.name}", ${p.name})(ctx)""" :: empty).reduceOption(_ :: ", " :: _).getOrElse(empty) :: ")(ctx)" :: empty) :/: "}" :: break) :/:
      s"def _$name(" ::
      parameters.map(_.name :: ": Reference" :: empty).reduceOption(_ :: ", " :: _).getOrElse(empty) ::
      """)(implicit ctx: Context): PAny = {""" ::
      nest(2, localVariables.foldLeft(empty: Document) {
        (doc, localVariable) =>
          doc :/: s"""val $localVariable = lvar("${localVariable}")""" :: empty
      } :/: statements.result().filter(_ != DocNil).foldLeft(empty: Document) {
        (doc, stmt) => doc :/: stmt
      }) :/: "}" :: empty
  }

}
