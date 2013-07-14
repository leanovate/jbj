package de.leanovate.jbj.parser

import scala.util.parsing.combinator.syntactical.StdTokenParsers
import de.leanovate.jbj.ast._
import de.leanovate.jbj.ast.expr.NegExpr
import de.leanovate.jbj.ast.expr.MulExpr
import de.leanovate.jbj.ast.expr.DivExpr
import de.leanovate.jbj.ast.expr.AddExpr
import de.leanovate.jbj.ast.expr.SubExpr
import de.leanovate.jbj.ast.stmt.{EchoStmt, InlineStmt}
import de.leanovate.jbj.ast.value.IntegerVal

object JbjParser extends StdTokenParsers {
  type Tokens = JbjTokens

  val lexical = new JbjLexer

  import lexical.{Inline, ScriptStart, ScriptStartEcho, ScriptEnd}

  lexical.reserved ++= List("echo")
  lexical.delimiters ++= List("+", "-", "*", "/", "(", ")", ",", ":", "{", "}")

  def value = numericLit ^^ (s => IntegerVal(s.toInt))

  def parens: Parser[Expr] = "(" ~> expr <~ ")"

  def neg: Parser[NegExpr] = "-" ~> term ^^ (term => NegExpr(term))

  def term = (value | parens | neg)

  def binaryOp(level: Int): Parser[((Expr, Expr) => Expr)] = {

    level match {
      case 1 =>
        "+" ^^^ {
          (a: Expr, b: Expr) => AddExpr(a, b)
        } |
          "-" ^^^ {
            (a: Expr, b: Expr) => SubExpr(a, b)
          }
      case 2 =>
        "*" ^^^ {
          (a: Expr, b: Expr) => MulExpr(a, b)
        } |
          "/" ^^^ {
            (a: Expr, b: Expr) => DivExpr(a, b)
          }
      case _ => throw new RuntimeException("bad precedence level " + level)
    }
  }

  val minPrec = 1

  val maxPrec = 2

  def binary(level: Int): Parser[Expr] =
    if (level > maxPrec) {
      term
    }
    else {
      binary(level + 1) * binaryOp(level)
    }

  def expr = (binary(minPrec) | term)

  def params = expr ~ opt(rep("," ~> expr)) ^^ {
    case expr ~ None => expr :: Nil
    case expr ~ Some(exprs) => expr :: exprs
  }

  def stmt = (
    "echo" ~> params ^^ (parms => EchoStmt(parms))
    )

  def inline: Parser[InlineStmt] =
    elem("inline", _.isInstanceOf[Inline]) ^^ {
      t => InlineStmt(t.chars)
    }

  def scriptStart: Parser[String] =
    elem("scriptStart", _.isInstanceOf[ScriptStart]) ^^ (_.chars)

  def scriptStartEcho: Parser[String] =
    elem("scriptStartEcho", _.isInstanceOf[ScriptStartEcho]) ^^ (_.chars)

  def scriptEnd: Parser[String] =
    elem("scriptEnd", _.isInstanceOf[ScriptEnd]) ^^ (_.chars)

  def script = (
    scriptStart ~> stmt
      | scriptStartEcho ~> params ^^ {
      params => EchoStmt(params)
    }
    )

  def prog = opt(rep(inline)) ~> script

  def parse(s: String): ParseResult[Node] = {

    val tokens = new lexical.InitialScanner(s)
    phrase(prog)(tokens)
  }

  def apply(s: String): Node = {

    parse(s) match {
      case Success(tree, _) => tree
      case e: NoSuccess =>
        throw new IllegalArgumentException("Bad syntax: " + s)
    }
  }

  //Simplify testing
  def test(exprstr: String) = {

    parse(exprstr) match {
      case Success(tree, _) =>
        println("Tree: " + tree)
      case e: NoSuccess => Console.err.println(e)
    }
  }

  //A main method for testing
  def main(args: Array[String]) = {

    test("<?php echo 1+2")
    test("<% echo 1+2*3")
    test("<? echo 1*2+3")
    test("<%= 1*2+3+4*5+6")
    test("<?= 1*(2+3)")
    test("<?php echo (1+2)*3")
  }

}
