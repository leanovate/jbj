/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.converter.visitor

import de.leanovate.jbj.core.ast.{Expr, NodeVisitor}
import scala.text.Document
import scala.text.Document._
import de.leanovate.jbj.core.ast.expr._
import de.leanovate.jbj.core.ast.expr.value.ScalarExpr
import de.leanovate.jbj.converter.builders.{CodeUnitBuilder, ProgCodeUnitBuilder, LiteralBuilder}
import de.leanovate.jbj.core.ast.expr.calc._
import de.leanovate.jbj.core.ast.expr.value.ScalarExpr
import de.leanovate.jbj.core.ast.expr.AssignRefExpr
import de.leanovate.jbj.core.ast.expr.PrintExpr
import de.leanovate.jbj.core.ast.name.StaticName
import de.leanovate.jbj.core.ast.expr.comp._
import de.leanovate.jbj.core.ast.expr.VariableRefExpr
import de.leanovate.jbj.core.ast.expr.value.ScalarExpr
import de.leanovate.jbj.core.ast.expr.AssignRefExpr
import de.leanovate.jbj.core.ast.expr.calc.AddExpr
import de.leanovate.jbj.core.ast.expr.calc.MulExpr
import de.leanovate.jbj.core.ast.name.StaticName
import de.leanovate.jbj.core.ast.expr.ArrayCreateExpr
import de.leanovate.jbj.core.ast.expr.ArrayKeyValue
import de.leanovate.jbj.core.ast.expr.calc.SubExpr
import de.leanovate.jbj.core.ast.expr.calc.ConcatExpr
import de.leanovate.jbj.core.ast.expr.CallByNameRefExpr
import de.leanovate.jbj.core.ast.expr.DimRefExpr
import de.leanovate.jbj.core.ast.expr.PrintExpr
import scala.Some
import de.leanovate.jbj.core.ast.expr.GetAndIncrExpr
import de.leanovate.jbj.core.ast.expr.VariableRefExpr
import de.leanovate.jbj.core.ast.expr.value.ScalarExpr
import de.leanovate.jbj.core.ast.expr.calc.AddExpr
import de.leanovate.jbj.core.ast.expr.calc.MulExpr
import de.leanovate.jbj.core.ast.name.StaticName
import de.leanovate.jbj.core.ast.expr.ArrayKeyValue
import de.leanovate.jbj.core.ast.expr.calc.SubExpr
import de.leanovate.jbj.core.ast.expr.CallByNameRefExpr
import de.leanovate.jbj.core.ast.expr.DimRefExpr
import de.leanovate.jbj.core.ast.expr.PrintExpr
import de.leanovate.jbj.core.ast.expr.comp.LeExpr
import scala.Some
import de.leanovate.jbj.core.ast.expr.GetAndDecrExpr
import de.leanovate.jbj.core.ast.expr.AssignRefExpr
import de.leanovate.jbj.core.ast.expr.comp.LtExpr
import de.leanovate.jbj.core.ast.expr.comp.GeExpr
import de.leanovate.jbj.core.ast.expr.ArrayCreateExpr
import de.leanovate.jbj.core.ast.expr.calc.ConcatExpr
import de.leanovate.jbj.core.ast.expr.comp.GtExpr
import de.leanovate.jbj.core.ast.expr.GetAndIncrExpr
import de.leanovate.jbj.core.ast.expr.calc.DivExpr

class ExpressionVisitor(implicit builder: CodeUnitBuilder) extends NodeVisitor[Document] {
  val expressions = Seq.newBuilder[Document]

  override def result = expressions.result().reduceOption(_ :: _).getOrElse(empty)

  override def visit = {

    case AddExpr(left, right) =>
      expressions += parentesis(Precedence.AddSub)(left) :: " + " :: parentesis(Precedence.AddSub)(right)
      acceptsNextSibling

    case ArrayCreateExpr(keyValues) if keyValues.forall(_.key.isEmpty) =>
      expressions += "array(" :: keyValues.map(_.value.foldWith(new ExpressionVisitor)).reduceOption(_ :: ", " :: _).getOrElse(empty) :: ")" :: empty
      acceptsNextSibling

    case ArrayCreateExpr(keyValues) =>
      expressions += "map(" :: keyValues.map {
        case ArrayKeyValue(None, value, _) =>
          "None ->" :: value.foldWith(new ExpressionVisitor)
        case ArrayKeyValue(Some(key), value, _) =>
          "Some(" :: key.foldWith(new ExpressionVisitor) :: ") ->" :: value.foldWith(new ExpressionVisitor)
      }.reduceOption(_ :: ", " :: _).getOrElse(empty) :: ")" :: empty
      acceptsNextSibling

    case AssignRefExpr(refExpr, expr) =>
      expressions += refExpr.foldWith(new ExpressionVisitor) :: " := " :: expr.foldWith(new ExpressionVisitor)
      acceptsNextSibling

    case CallByNameRefExpr(name, parameters) =>
      expressions += s"""f("$name")(""" :: parameters.map(_.foldWith(new ExpressionVisitor)).reduceOption(_ :: ", " :: _).getOrElse(empty) :: ")" :: empty
      acceptsNextSibling

    case ConcatExpr(left, right) =>
      expressions += parentesis(Precedence.AddSub)(left) :: " !! " :: parentesis(Precedence.AddSub)(right)
      acceptsNextSibling

    case DimRefExpr(ref, index) =>
      expressions += parentesis(Precedence.Term)(ref) :: ".dim(" :: index.map(_.foldWith(new ExpressionVisitor)).getOrElse(empty) :: ")" :: empty
      acceptsNextSibling

    case DivExpr(left, right) =>
      expressions += parentesis(Precedence.MulDiv)(left) :: " / " :: parentesis(Precedence.AddSub)(right)
      acceptsNextSibling

    case EqExpr(left, right) =>
      expressions += parentesis(Precedence.Eq)(left) :: " :== " :: parentesis(Precedence.AddSub)(right)
      acceptsNextSibling

    case GetAndDecrExpr(expr) =>
      expressions += parentesis(Precedence.Term)(expr) :: ".--" :: empty
      acceptsNextSibling

    case GetAndIncrExpr(expr) =>
      expressions += parentesis(Precedence.Term)(expr) :: ".++" :: empty
      acceptsNextSibling

    case GeExpr(left, right) =>
      expressions += parentesis(Precedence.Compare)(left) :: " >= " :: parentesis(Precedence.AddSub)(right)
      acceptsNextSibling

    case GtExpr(left, right) =>
      expressions += parentesis(Precedence.Compare)(left) :: " > " :: parentesis(Precedence.AddSub)(right)
      acceptsNextSibling

    case LeExpr(left, right) =>
      expressions += parentesis(Precedence.Compare)(left) :: " <= " :: parentesis(Precedence.AddSub)(right)
      acceptsNextSibling

    case LtExpr(left, right) =>
      expressions += parentesis(Precedence.Compare)(left) :: " < " :: parentesis(Precedence.AddSub)(right)
      acceptsNextSibling

    case MulExpr(left, right) =>
      expressions += parentesis(Precedence.MulDiv)(left) :: " * " :: parentesis(Precedence.AddSub)(right)
      acceptsNextSibling

    case PrintExpr(expr) =>
      expressions += text("print(") :: expr.foldWith(new ExpressionVisitor) :: text(")")
      acceptsNextSibling

    case ScalarExpr(value) =>
      expressions += LiteralBuilder.build(value)
      acceptsNextSibling

    case SubExpr(left, right) =>
      expressions += parentesis(Precedence.AddSub)(left) :: " - " :: parentesis(Precedence.AddSub)(right)
      acceptsNextSibling

    case VariableRefExpr(StaticName(name)) =>
      builder.defineLocalVar(name)
      expressions += text(name)
      acceptsNextSibling

    case stmt =>
      println("Unhandled node: " + stmt)
      abort
  }

  def parentesis(threshold: Precedence.Type): PartialFunction[Expr, Document] = {
    case expr: BinaryExpr if expr.precedence.id < threshold.id =>
      text("(") :: expr.foldWith(new ExpressionVisitor) :: text(")")
    case expr: Expr => expr.foldWith(new ExpressionVisitor)
  }
}
