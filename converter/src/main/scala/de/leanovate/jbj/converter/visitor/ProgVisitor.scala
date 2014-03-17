/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.converter.visitor

import de.leanovate.jbj.core.ast.{Stmt, Prog, NodeVisitor}
import scala.text.Document
import de.leanovate.jbj.converter.builders.{FunctionCodeUnitBuilder, ProgCodeUnitBuilder}
import de.leanovate.jbj.core.ast.decl.FunctionDeclStmt

class ProgVisitor(name: String, packageName: Option[String]) extends NodeVisitor[Document] {
  implicit val builder = new ProgCodeUnitBuilder(name, packageName)

  def result = builder.build()

  def visit = {
    case prop: Prog =>
      acceptsNextChild
    case FunctionDeclStmt(name, returnByRef, parameterDecls, stmts) =>
      val functionBuilder = new FunctionCodeUnitBuilder(builder, name, parameterDecls)
      stmts.foreach {
        stmt =>
          functionBuilder.addStatement(stmt.foldWith(new StatementVisitor()(functionBuilder)))
      }
      builder.defineFunction(name.toString, functionBuilder.build())
      acceptsNextSibling
    case stmt: Stmt =>
      builder.addStatement(stmt.foldWith(new StatementVisitor))
      acceptsNextSibling
  }
}
