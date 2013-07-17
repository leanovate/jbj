package de.leanovate.jbj.parser

import scala.util.parsing.combinator.syntactical.StdTokenParsers
import de.leanovate.jbj.ast._
import de.leanovate.jbj.ast.stmt._
import de.leanovate.jbj.ast.expr._
import de.leanovate.jbj.ast.expr.calc.AddExpr
import de.leanovate.jbj.ast.expr.calc.MulExpr
import de.leanovate.jbj.ast.stmt.AssignStmt
import de.leanovate.jbj.ast.stmt.ReturnStmt
import de.leanovate.jbj.ast.expr.calc.SubExpr
import de.leanovate.jbj.ast.expr.comp.LeExpr
import scala.Some
import de.leanovate.jbj.ast.value.StringVal
import de.leanovate.jbj.exec.GlobalContext
import de.leanovate.jbj.ast.expr.comp.EqExpr
import de.leanovate.jbj.ast.expr.comp.LtExpr
import de.leanovate.jbj.ast.expr.comp.GeExpr
import de.leanovate.jbj.ast.stmt.InlineStmt
import de.leanovate.jbj.ast.Prog
import de.leanovate.jbj.ast.value.IntegerVal
import de.leanovate.jbj.ast.expr.comp.GtExpr
import de.leanovate.jbj.ast.expr.calc.DivExpr
import de.leanovate.jbj.ast.stmt.EchoStmt
import de.leanovate.jbj.ast.stmt.cond._
import de.leanovate.jbj.ast.expr.calc.AddExpr
import de.leanovate.jbj.ast.stmt.cond.IfStmt
import de.leanovate.jbj.ast.stmt.ReturnStmt
import de.leanovate.jbj.ast.expr.calc.SubExpr
import de.leanovate.jbj.ast.stmt.cond.SwitchStmt
import scala.Some
import de.leanovate.jbj.ast.value.StringVal
import de.leanovate.jbj.ast.expr.comp.LtExpr
import de.leanovate.jbj.ast.expr.comp.GeExpr
import de.leanovate.jbj.ast.Prog
import de.leanovate.jbj.ast.stmt.cond.ElseIfBlock
import de.leanovate.jbj.ast.expr.calc.MulExpr
import de.leanovate.jbj.ast.stmt.AssignStmt
import de.leanovate.jbj.ast.expr.comp.LeExpr
import de.leanovate.jbj.ast.stmt.cond.CaseBlock
import de.leanovate.jbj.exec.GlobalContext
import de.leanovate.jbj.ast.expr.comp.EqExpr
import de.leanovate.jbj.ast.stmt.InlineStmt
import de.leanovate.jbj.ast.value.IntegerVal
import de.leanovate.jbj.ast.expr.comp.GtExpr
import de.leanovate.jbj.ast.expr.calc.DivExpr
import de.leanovate.jbj.ast.stmt.EchoStmt
import de.leanovate.jbj.ast.stmt.loop.WhileStmt
import de.leanovate.jbj.ast.stmt.cond.DefaultCaseBlock
import de.leanovate.jbj.ast.expr.calc.AddExpr
import de.leanovate.jbj.ast.stmt.cond.IfStmt
import de.leanovate.jbj.ast.stmt.ReturnStmt
import de.leanovate.jbj.ast.expr.calc.SubExpr
import scala.Some
import de.leanovate.jbj.ast.value.StringVal
import de.leanovate.jbj.ast.expr.comp.LtExpr
import de.leanovate.jbj.ast.expr.comp.GeExpr
import de.leanovate.jbj.ast.Prog
import de.leanovate.jbj.ast.stmt.cond.ElseIfBlock
import de.leanovate.jbj.ast.expr.NegExpr
import de.leanovate.jbj.ast.expr.DotExpr
import de.leanovate.jbj.ast.expr.calc.MulExpr
import de.leanovate.jbj.ast.stmt.AssignStmt
import de.leanovate.jbj.ast.expr.comp.LeExpr
import de.leanovate.jbj.ast.stmt.cond.CaseBlock
import de.leanovate.jbj.ast.expr.VarGetExpr
import de.leanovate.jbj.exec.GlobalContext
import de.leanovate.jbj.ast.expr.comp.EqExpr
import de.leanovate.jbj.ast.stmt.InlineStmt
import de.leanovate.jbj.ast.value.IntegerVal
import de.leanovate.jbj.ast.expr.CallExpr
import de.leanovate.jbj.ast.expr.comp.GtExpr
import de.leanovate.jbj.ast.expr.calc.DivExpr
import de.leanovate.jbj.ast.stmt.EchoStmt

object JbjParser extends StdTokenParsers {
  type Tokens = JbjTokens

  val lexical = new JbjLexer

  import lexical.{Inline, ScriptStart, ScriptStartEcho, ScriptEnd, VarIdentifier}

  lexical.reserved ++= List("static", "private", "class",
    "echo",
    "return", "break", "continue",
    "if", "else", "elseif", "while", "for",
    "switch", "case", "default")
  lexical.delimiters ++= List(".", "+", "-", "*", "/", "(", ")", ",", ":", ";", "{", "}", "=", ">", ">=", "<", "<=", "==")

  def value =
    (numericLit ^^ (s => IntegerVal(s.toInt))
      | stringLit ^^ (s => StringVal(s)))

  def variableRef = variable <~ "+" <~ "+" ^^ {
    s => VarGetAndIncrExpr(s)
  } | variable ^^ {
    s => VarGetExpr(s)
  }

  def functionCall: Parser[Expr] = ident ~ "(" ~ params <~ ")" ^^ {
    case name ~ _ ~ params => CallExpr(name, params)
  }

  def parens: Parser[Expr] = "(" ~> expr <~ ")"

  def neg: Parser[NegExpr] = "-" ~> term ^^ (term => NegExpr(term))

  def term = value | variableRef | functionCall | parens | neg

  def binaryOp(level: Int): Parser[((Expr, Expr) => Expr)] = {
    level match {
      case 1 =>
        ">" ^^^ {
          (a: Expr, b: Expr) => GtExpr(a, b)
        } | ">=" ^^^ {
          (a: Expr, b: Expr) => GeExpr(a, b)
        } | "<" ^^^ {
          (a: Expr, b: Expr) => LtExpr(a, b)
        } | "<=" ^^^ {
          (a: Expr, b: Expr) => LeExpr(a, b)
        } | "==" ^^^ {
          (a: Expr, b: Expr) => EqExpr(a, b)
        }
      case 2 =>
        "." ^^^ {
          (a: Expr, b: Expr) => DotExpr(a, b)
        }
      case 3 =>
        "+" ^^^ {
          (a: Expr, b: Expr) => AddExpr(a, b)
        } | "-" ^^^ {
          (a: Expr, b: Expr) => SubExpr(a, b)
        }
      case 4 =>
        "*" ^^^ {
          (a: Expr, b: Expr) => MulExpr(a, b)
        } | "/" ^^^ {
          (a: Expr, b: Expr) => DivExpr(a, b)
        }
      case _ => throw new RuntimeException("bad precedence level " + level)
    }
  }

  val minPrec = 1

  val maxPrec = 4

  def binary(level: Int): Parser[Expr] =
    if (level > maxPrec) {
      term
    }
    else {
      binary(level + 1) * binaryOp(level)
    }

  def expr: Parser[Expr] = binary(minPrec) | term

  def params: Parser[List[Expr]] = expr ~ rep("," ~> expr) ^^ {
    case expr ~ exprs => expr :: exprs
  }

  def stmt: Parser[Stmt] =
    opt("static") ~ variable ~ "=" ~ expr ^^ {
      case static ~ variable ~ _ ~ expr => AssignStmt(variable, expr, static = static.isDefined)
    } | "echo" ~> params ^^ {
      parms => EchoStmt(parms)
    } | "return" ~> expr ^^ {
      expr => ReturnStmt(expr)
    } | "break" ^^^ {
      BreakStmt
    } | "continue" ^^^ {
      ContinueStmt
    } | ifStmt | switchStmt | whileStmt | expr ^^ {
      expr => ExprStmt(expr)
    }

  def stmts: Parser[List[Stmt]] = stmt ~ rep(";" ~> opt(stmt)) ^^ {
    case stmt ~ stmts => stmt :: stmts.flatten
  }

  def stmtsOrInline: Parser[List[Stmt]] = stmts |
    scriptEnd ~> opt(rep(inline)) <~ scriptStart ^^ {
      inline => inline.getOrElse(Nil)
    }

  def block: Parser[BlockStmt] = "{" ~> rep(stmtsOrInline) <~ "}" ^^ {
    stmts => BlockStmt(stmts.flatten)
  }

  def ifStmt: Parser[IfStmt] = "if" ~> "(" ~> expr ~ ")" ~ block ~ rep(elseIfBlock) ~ opt("else" ~> block) ^^ {
    case cond ~ _ ~ then ~ elseIfs ~ optElse => IfStmt(cond, then, elseIfs, optElse)
  }

  def elseIfBlock: Parser[ElseIfBlock] = "elseif" ~> "(" ~> expr ~ ")" ~ block ^^ {
    case cond ~ _ ~ then => ElseIfBlock(cond, then)
  }

  def switchStmt: Parser[SwitchStmt] = "switch" ~> "(" ~> expr ~ ")" ~ "{" ~ switchCases ~ "}" ^^ {
    case expr ~ _ ~ _ ~ cases ~ _ => SwitchStmt(expr, cases)
  }

  def switchCases: Parser[List[SwitchCase]] = rep(
    "case" ~> expr ~ ":" ~ stmtsOrInline ^^ {
      case expr ~ _ ~ stmts => CaseBlock(expr, stmts)
    } | "default" ~> ":" ~> stmtsOrInline ^^ {
      stmts => DefaultCaseBlock(stmts)
    }
  )

  def whileStmt: Parser[WhileStmt] = "while" ~> "(" ~> expr ~ ")" ~ block ^^ {
    case expr ~ _ ~ block => WhileStmt(expr, block)
  }

  def script =
    scriptStart ~> stmts <~ scriptEnd |
      scriptStartEcho ~> params ~ opt(rep(";" ~> stmt)) <~ scriptEnd ^^ {
        case params ~ None => EchoStmt(params) :: Nil
        case params ~ Some(stmts) => EchoStmt(params) :: stmts
      }

  def inlineOrScript: Parser[List[Stmt]] = inline ^^ {
    inline => List(inline)
  } | script

  def prog: Parser[Prog] = opt(rep(inlineOrScript)) ^^ {
    case None => Prog(List())
    case Some(stmts) => Prog(stmts.flatten)
  }


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

  def variable: Parser[String] =
    elem("variable", _.isInstanceOf[VarIdentifier]) ^^ (_.asInstanceOf[VarIdentifier].name)

  def parse(s: String): ParseResult[Prog] = {
    val tokens = new lexical.InitialScanner(s)
    phrase(prog)(tokens)
  }

  def apply(s: String): Prog = {
    parse(s) match {
      case Success(tree, _) => tree
      case e: NoSuccess =>
        throw new IllegalArgumentException("Bad syntax: " + e)
    }
  }

  //Simplify testing
  def test(exprstr: String) = {
    parse(exprstr) match {
      case Success(tree, _) =>
        println("Tree: " + tree)
        val context = GlobalContext(System.out)
        tree.exec(context)
      case e: NoSuccess => Console.err.println(e)
    }
  }

  //A main method for testing
  def main(args: Array[String]) = {

    test( """<?php if (1 > 2) { echo "hurra" } ?>""")
    test("Hurra <?php echo 1>2 ?>")
  }

}
