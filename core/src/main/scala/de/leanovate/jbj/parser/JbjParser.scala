package de.leanovate.jbj.parser

import de.leanovate.jbj.ast._
import de.leanovate.jbj.ast.stmt._
import de.leanovate.jbj.ast.stmt.cond._
import de.leanovate.jbj.ast.stmt.cond.SwitchStmt
import de.leanovate.jbj.ast.stmt.loop.{ForeachKeyValueStmt, ForeachValueStmt, ForStmt, WhileStmt}
import scala.collection.mutable
import scala.util.parsing.combinator.Parsers
import de.leanovate.jbj.ast.expr.value._
import scala.language.implicitConversions
import de.leanovate.jbj.ast.expr.calc._
import de.leanovate.jbj.ast.stmt.GlobalAssignStmt
import de.leanovate.jbj.ast.expr.VariableReference
import de.leanovate.jbj.ast.stmt.cond.DefaultCaseBlock
import de.leanovate.jbj.ast.expr.calc.AddExpr
import de.leanovate.jbj.ast.expr.comp.BoolAndExpr
import de.leanovate.jbj.ast.stmt.cond.IfStmt
import de.leanovate.jbj.parser.JbjTokens.NumericLit
import de.leanovate.jbj.ast.stmt.ReturnStmt
import de.leanovate.jbj.ast.expr.calc.SubExpr
import de.leanovate.jbj.ast.stmt.ClassDefStmt
import scala.Some
import de.leanovate.jbj.ast.expr.AssignExpr
import de.leanovate.jbj.ast.expr.value.ConstGetExpr
import de.leanovate.jbj.ast.expr.IndexReference
import de.leanovate.jbj.parser.JbjTokens.Inline
import de.leanovate.jbj.ast.stmt.BreakStmt
import de.leanovate.jbj.ast.expr.comp.LtExpr
import de.leanovate.jbj.ast.expr.comp.GeExpr
import de.leanovate.jbj.ast.Prog
import de.leanovate.jbj.ast.stmt.ExprStmt
import de.leanovate.jbj.ast.stmt.cond.ElseIfBlock
import de.leanovate.jbj.ast.expr.NegExpr
import de.leanovate.jbj.ast.stmt.ParameterDef
import de.leanovate.jbj.ast.expr.DotExpr
import de.leanovate.jbj.parser.JbjTokens.ScriptEnd
import de.leanovate.jbj.parser.JbjTokens.ScriptStart
import de.leanovate.jbj.ast.expr.calc.MulExpr
import de.leanovate.jbj.ast.expr.value.IntegerConstExpr
import de.leanovate.jbj.ast.stmt.StaticAssignStmt
import de.leanovate.jbj.parser.JbjTokens.InterpolatedStringLit
import de.leanovate.jbj.ast.stmt.AssignStmt
import de.leanovate.jbj.ast.stmt.FunctionDefStmt
import de.leanovate.jbj.ast.stmt.Assignment
import de.leanovate.jbj.ast.expr.PropertyReference
import de.leanovate.jbj.parser.JbjTokens.Identifier
import de.leanovate.jbj.ast.expr.comp.LeExpr
import de.leanovate.jbj.ast.expr.GetAndDecrExpr
import de.leanovate.jbj.ast.stmt.cond.CaseBlock
import de.leanovate.jbj.ast.stmt.ContinueStmt
import de.leanovate.jbj.ast.expr.IncrAndGetExpr
import de.leanovate.jbj.ast.expr.CallFunctionExpr
import de.leanovate.jbj.runtime.context.GlobalContext
import de.leanovate.jbj.ast.expr.comp.EqExpr
import de.leanovate.jbj.ast.expr.MethodCallReference
import de.leanovate.jbj.ast.expr.ArrayCreateExpr
import de.leanovate.jbj.ast.stmt.InlineStmt
import de.leanovate.jbj.ast.expr.comp.BoolOrExpr
import de.leanovate.jbj.parser.JbjTokens.Keyword
import de.leanovate.jbj.ast.expr.value.StringConstExpr
import de.leanovate.jbj.parser.JbjTokens.StringLit
import de.leanovate.jbj.ast.expr.comp.BoolXorExpr
import de.leanovate.jbj.ast.FilePosition
import de.leanovate.jbj.ast.expr.comp.GtExpr
import de.leanovate.jbj.ast.expr.GetAndIncrExpr
import de.leanovate.jbj.parser.JbjTokens.ScriptStartEcho
import de.leanovate.jbj.ast.expr.calc.DivExpr
import de.leanovate.jbj.ast.stmt.EchoStmt

object JbjParser extends Parsers {
  type Elem = JbjTokens.Token

  private val keywordCache = mutable.HashMap[String, Parser[Keyword]]()

  def value: Parser[Expr] =
    (numericLit ^^ (s => IntegerConstExpr(s.position, s.chars.toLong))
      | stringLit ^^ (s => StringConstExpr(s.position, s.chars))
      | interpolatedStringLit ^^ (s => InterpolatedStringExpr(s.position, s.charOrInterpolations))
      )

  def variable: Parser[VariableReference] = "$" ~ ident ^^ {
    case v ~ name => VariableReference(v.position, name.chars)
  }

  def reference: Parser[Reference] = variable ~ rep(refAccess) ^^ {
    case variable ~ refAccesses => refAccesses.foldLeft(variable.asInstanceOf[Reference]) {
      (ref, refAccess) => refAccess(ref)
    }
  }

  def refAccess: Parser[Reference => Reference] = "[" ~> expr <~ "]" ^^ {
    expr => IndexReference(_: Reference, expr)
  } | "->" ~> ident ~ "(" ~ repsep(expr, ",") <~ ")" ^^ {
    case method ~ _ ~ params => MethodCallReference(_: Reference, method.chars, params)
  } | "->" ~> ident ^^ {
    property => PropertyReference(_: Reference, property.chars)
  }

  def referenceExpr: Parser[Expr] = reference <~ "+" <~ "+" ^^ {
    ref => GetAndIncrExpr(ref)
  } | reference <~ "-" <~ "-" ^^ {
    ref => GetAndDecrExpr(ref)
  } | "+" ~> "+" ~> reference ^^ {
    ref => GetAndIncrExpr(ref)
  } | "-" ~> "-" ~> reference ^^ {
    ref => IncrAndGetExpr(ref)
  } | reference |
    "array" ~ "(" ~ repsep(arrayValues, ",") <~ ")" ^^ {
      case array ~ _ ~ arrayValues => ArrayCreateExpr(array.position, arrayValues)
    }

  def arrayValues: Parser[(Option[Expr], Expr)] = expr ~ "=>" ~ expr ^^ {
    case indexExpr ~ _ ~ valueExpr => (Some(indexExpr), valueExpr)
  } | expr ^^ {
    valueExpr => (None, valueExpr)
  }

  def functionCall: Parser[Expr] = ident ~ "(" ~ repsep(expr, ",") <~ ")" ^^ {
    case name ~ _ ~ params => CallFunctionExpr(name.position, name.chars, params)
  }

  def parens: Parser[Expr] = "(" ~> expr <~ ")"

  def neg: Parser[NegExpr] = "-" ~ term ^^ {
    case sign ~ term => NegExpr(sign.position, term)
  }

  def constant: Parser[Expr] = ident ^^ {
    name => ConstGetExpr(name.position, name.chars)
  }

  def term: Parser[Expr] = value | referenceExpr | functionCall | constant | parens | neg

  def binaryOp(level: Int): Parser[((Expr, Expr) => Expr)] = {
    level match {
      case 1 => "or" ^^^ ((a: Expr, b: Expr) => BoolOrExpr(a, b))
      case 2 => "xor" ^^^ ((a: Expr, b: Expr) => BoolXorExpr(a, b))
      case 3 => "and" ^^^ ((a: Expr, b: Expr) => BoolAndExpr(a, b))
      case 4 => "=" ^^^ {
        (a: Expr, b: Expr) => (a, b) match {
          case (a: Reference, b: Expr) => AssignExpr(a, b)
        }
      }
      case 5 => "||" ^^^ ((a: Expr, b: Expr) => BoolOrExpr(a, b))
      case 6 => "&&" ^^^ ((a: Expr, b: Expr) => BoolAndExpr(a, b))
      case 7 => "|" ^^^ ((a: Expr, b: Expr) => BitOrExpr(a, b))
      case 8 => "" ^^^ ((a: Expr, b: Expr) => BitXorExpr(a, b))
      case 9 => "&" ^^^ ((a: Expr, b: Expr) => BitAndExpr(a, b))
      case 10 => "==" ^^^ ((a: Expr, b: Expr) => EqExpr(a, b))
      case 11 =>
        ">" ^^^ ((a: Expr, b: Expr) => GtExpr(a, b)) |
          ">=" ^^^ ((a: Expr, b: Expr) => GeExpr(a, b)) |
          "<" ^^^ ((a: Expr, b: Expr) => LtExpr(a, b)) |
          "<=" ^^^ ((a: Expr, b: Expr) => LeExpr(a, b))
      case 12 =>
        "." ^^^ ((a: Expr, b: Expr) => DotExpr(a, b)) |
          "+" ^^^ ((a: Expr, b: Expr) => AddExpr(a, b)) |
          "-" ^^^ ((a: Expr, b: Expr) => SubExpr(a, b))
      case 13 =>
        "*" ^^^ ((a: Expr, b: Expr) => MulExpr(a, b)) |
          "/" ^^^ ((a: Expr, b: Expr) => DivExpr(a, b))
      case _ => throw new RuntimeException("bad precedence level " + level)
    }
  }

  val minPrec = 1

  val maxPrec = 13

  def binary(level: Int): Parser[Expr] =
    if (level > maxPrec) {
      term
    }
    else {
      binary(level + 1) * binaryOp(level)
    }

  def expr: Parser[Expr] = binary(minPrec) | term

  def regularStmt: Parser[Stmt] =
    modifiers ~ rep1sep(assignment, ",") ^^ {
      case modifiers ~ assignments if modifiers.contains(Modifier.STATIC) =>
        StaticAssignStmt(assignments.head.position, modifiers, assignments)
      case modifiers ~ assignments if modifiers.contains(Modifier.GLOBAL) =>
        GlobalAssignStmt(assignments.head.position, modifiers, assignments)
      case modifiers ~ assignments =>
        AssignStmt(assignments.head.position, modifiers, assignments)
    } | rep1sep(assignmentWithExpr, ",") ^^ {
      assignments => AssignStmt(assignments.head.position, Set.empty[Modifier.Type], assignments)
    } | "echo" ~ rep1sep(expr, ",") ^^ {
      case echo ~ params => EchoStmt(echo.position, params)
    } | "return" ~ expr ^^ {
      case ret ~ expr => ReturnStmt(ret.position, expr)
    } | "break" ~ opt(numericLit) ^^ {
      case br ~ depth => BreakStmt(br.position, depth.map(_.chars.toInt).getOrElse(1))
    } | "continue" ~ opt(numericLit) ^^ {
      case con ~ depth => ContinueStmt(con.position, depth.map(_.chars.toInt).getOrElse(1))
    } | expr ^^ {
      expr => ExprStmt(expr)
    }

  def modifiers: Parser[Set[Modifier.Type]] = rep1(modifier) ^^ (_.toSet)

  def modifier: Parser[Modifier.Type] = "static" ^^^ Modifier.STATIC | "global" ^^^ Modifier.GLOBAL |
    "public" ^^^ Modifier.PUBLIC | "protected" ^^^ Modifier.PROTECTED | "private" ^^^ Modifier.PRIVATE

  def assignment: Parser[Assignment] = assignmentWithExpr | variable ^^ {
    variable => Assignment(variable.position, variable, None)
  }

  def assignmentWithExpr: Parser[Assignment] = variable ~ "=" ~ expr ^^ {
    case variable ~ _ ~ expr => Assignment(variable.position, variable, Some(expr))
  }

  def blockLikeStmt: Parser[Stmt] =
    ifStmt | switchStmt | whileStmt | forStmt | foreachStmt | functionDef | classDef | block

  def closedStmt: Parser[Stmt] = regularStmt <~ ";" | blockLikeStmt

  def stmtsWithUnclosed: Parser[List[Stmt]] = rep(closedStmt) ~ opt(regularStmt) ^^ {
    case stmts ~ optUnclosed => stmts ++ optUnclosed
  }

  def stmtsOrInline: Parser[List[Stmt]] = rep(rep1(closedStmt) |
    opt(regularStmt) ~ scriptEnd ~ rep(inline) <~ scriptStart ^^ {
      case optUnclosed ~ _ ~ inline => optUnclosed.toList ++ inline
    }) ^^ {
    stmts => stmts.flatten
  }

  def block: Parser[BlockStmt] = "{" ~ stmtsOrInline <~ "}" ^^ {
    case paren ~ stmts => BlockStmt(paren.position, stmts)
  }

  def ifStmt: Parser[IfStmt] = "if" ~ "(" ~ expr ~ ")" ~ closedStmt ~ rep(elseIfBlock) ~ opt("else" ~> closedStmt) ^^ {
    case ifT ~ _ ~ cond ~ _ ~ thenBlock ~ elseIfs ~ optElse => IfStmt(ifT.position, cond, thenBlock, elseIfs, optElse)
  }

  def elseIfBlock: Parser[ElseIfBlock] = "elseif" ~> "(" ~> expr ~ ")" ~ closedStmt ^^ {
    case cond ~ _ ~ thenBlock => ElseIfBlock(cond, thenBlock)
  }

  def switchStmt: Parser[SwitchStmt] = "switch" ~ "(" ~ expr ~ ")" ~ "{" ~ switchCases ~ "}" ^^ {
    case switchT ~ _ ~ expr ~ _ ~ _ ~ cases ~ _ => SwitchStmt(switchT.position, expr, cases)
  }

  def switchCases: Parser[List[SwitchCase]] = rep(
    "case" ~> expr ~ ":" ~ stmtsOrInline ^^ {
      case expr ~ _ ~ stmts => CaseBlock(expr, stmts)
    } | "default" ~> ":" ~> stmtsOrInline ^^ {
      stmts => DefaultCaseBlock(stmts)
    }
  )

  def whileStmt: Parser[WhileStmt] = "while" ~ "(" ~ expr ~ ")" ~ closedStmt ^^ {
    case wh ~ _ ~ expr ~ _ ~ stmt => WhileStmt(wh.position, expr, stmt)
  }

  def forStmt: Parser[ForStmt] = "for" ~ "(" ~ regularStmt ~ ";" ~ expr ~ ";" ~ regularStmt ~ ")" ~ closedStmt ^^ {
    case fo ~ _ ~ beforeStmt ~ _ ~ condition ~ _ ~ afterStmt ~ _ ~ stmt =>
      ForStmt(fo.position, beforeStmt, condition, afterStmt, stmt)
  }

  def foreachStmt: Parser[Stmt] = "foreach" ~ "(" ~ expr ~ "as" ~ variable ~ opt("=>" ~> variable) ~ ")" ~ closedStmt ^^ {
    case fo ~ _ ~ arrayExpr ~ _ ~ valueVar ~ None ~ _ ~ stmt =>
      ForeachValueStmt(fo.position, arrayExpr, valueVar.variableName, stmt)
    case fo ~ _ ~ arrayExpr ~ _ ~ keyVar ~ Some(valueVar) ~ _ ~ stmt =>
      ForeachKeyValueStmt(fo.position, arrayExpr, keyVar.variableName, valueVar.variableName, stmt)
  }

  def functionDef: Parser[FunctionDefStmt] = "function" ~ ident ~ "(" ~ parameterDefs ~ ")" ~ block ^^ {
    case func ~ name ~ _ ~ parameters ~ _ ~ body => FunctionDefStmt(func.position, name.chars, parameters, body)
  }

  def classDef: Parser[ClassDefStmt] = "class" ~ ident ~ opt("extends" ~> ident) ~ block ^^ {
    case cls ~ className ~ superClassName ~ body =>
      ClassDefStmt(cls.position, className.chars, superClassName.map(_.chars), body)
  }

  def parameterDefs: Parser[List[ParameterDef]] = repsep(parameterDef, ",")

  def parameterDef: Parser[ParameterDef] = variable ^^ (v => ParameterDef(v.variableName, byRef = false, default = None))

  def script: Parser[List[Stmt]] =
    scriptStart ~> stmtsWithUnclosed |
      scriptStartEcho ~ rep1sep(expr, ",") ~ opt(";" ~> stmtsWithUnclosed) ^^ {
        case start ~ params ~ None => EchoStmt(start.position, params) :: Nil
        case start ~ params ~ Some(stmts) => EchoStmt(start.position, params) :: stmts
      }

  def inlineAndScript: Parser[List[Stmt]] = rep(inline) ~ opt(script) ^^ {
    case inline ~ None => inline
    case inline ~ Some(script) => inline ++ script
  }

  def prog: Parser[Prog] = repsep(inlineAndScript, scriptEnd) ^^ {
    stmts => Prog(FilePosition("-", 0), stmts.flatten)
  }

  def inline: Parser[InlineStmt] =
    elem("inline", _.isInstanceOf[Inline]) ^^ {
      t => InlineStmt(t.position, t.chars)
    }

  def scriptStart: Parser[String] =
    elem("scriptStart", _.isInstanceOf[ScriptStart]) ^^ (_.chars)

  def scriptStartEcho: Parser[ScriptStartEcho] =
    elem("scriptStartEcho", _.isInstanceOf[ScriptStartEcho]) ^^ (_.asInstanceOf[ScriptStartEcho])

  def scriptEnd: Parser[String] =
    elem("scriptEnd", _.isInstanceOf[ScriptEnd]) ^^ (_.chars)

  /** A parser which matches a single keyword token.
    *
    * @param chars    The character string making up the matched keyword.
    * @return a `Parser` that matches the given string
    */
  //  implicit def keyword(chars: String): Parser[String] = accept(Keyword(chars)) ^^ (_.chars)
  implicit def keyword(chars: String): Parser[Keyword] =
    keywordCache.getOrElseUpdate(chars, elem("keyword " + chars, {
      t => t.chars == chars && t.isInstanceOf[Keyword]
    }) ^^ (_.asInstanceOf[Keyword]))

  /** A parser which matches a numeric literal */
  def numericLit: Parser[NumericLit] =
    elem("number", _.isInstanceOf[NumericLit]) ^^ (_.asInstanceOf[NumericLit])

  /** A parser which matches a string literal */
  def stringLit: Parser[StringLit] =
    elem("string literal", _.isInstanceOf[StringLit]) ^^ (_.asInstanceOf[StringLit])

  def interpolatedStringLit: Parser[InterpolatedStringLit] =
    elem("interpolated string literal", _.isInstanceOf[InterpolatedStringLit]) ^^ (_.asInstanceOf[InterpolatedStringLit])

  /** A parser which matches an identifier */
  def ident: Parser[Identifier] =
    elem("identifier", _.isInstanceOf[Identifier]) ^^ (_.asInstanceOf[Identifier])

  def parse(s: String): ParseResult[Prog] = {
    val tokens = new JbjInitialLexer(s)
    phrase(prog)(tokens)
  }

  def parseExpr(s: String): Expr = {
    val tokens = new JbjScriptLexer(s)
    phrase(expr)(tokens) match {
      case Success(expr, _) => expr
      case e: NoSuccess =>
        throw new IllegalArgumentException("Bad syntax: " + e)
    }
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
        val context = GlobalContext(System.out, System.err)
        tree.exec(context)
      case e: NoSuccess => Console.err.println(e)
    }
  }

  //A main method for testing
  def main(args: Array[String]) = {
    test( """<?php
            |
            |$strVals = array(
            |   "0","65","-44", "1.2", "-7.7", "abc", "123abc", "123e5", "123e5xyz", " 123abc", "123 abc", "123abc ", "3.4a",
            |   "a5.9"
            |);
            |
            |error_reporting(E_ERROR);
            |
            |foreach ($strVals as $strVal) {
            |   foreach($strVals as $otherVal) {
            |	   echo "--- testing: '$strVal' + '$otherVal' ---\n";
            |      var_dump($strVal+$otherVal);
            |   }
            |}
            |
            |
            |?>""".stripMargin)
  }
}
