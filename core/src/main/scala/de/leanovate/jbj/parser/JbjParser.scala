package de.leanovate.jbj.parser

import de.leanovate.jbj.ast._
import de.leanovate.jbj.ast.stmt.cond._
import scala.collection.mutable
import scala.util.parsing.combinator.Parsers
import de.leanovate.jbj.ast.expr.value._
import scala.language.implicitConversions
import de.leanovate.jbj.parser.JbjTokens._
import de.leanovate.jbj.ast.stmt._
import de.leanovate.jbj.runtime.value.UndefinedVal
import de.leanovate.jbj.ast.stmt.GlobalVarDeclAssignStmt
import de.leanovate.jbj.ast.expr.VariableReference
import de.leanovate.jbj.ast.stmt.cond.DefaultCaseBlock
import de.leanovate.jbj.ast.expr.calc.AddExpr
import de.leanovate.jbj.ast.stmt.ReturnStmt
import de.leanovate.jbj.ast.expr.calc.SubExpr
import de.leanovate.jbj.ast.stmt.cond.SwitchStmt
import scala.Some
import de.leanovate.jbj.ast.expr.AssignExpr
import de.leanovate.jbj.ast.expr.IndexReference
import de.leanovate.jbj.parser.JbjTokens.Inline
import de.leanovate.jbj.runtime.value.FloatVal
import de.leanovate.jbj.ast.expr.calc.BitAndExpr
import de.leanovate.jbj.ast.Prog
import de.leanovate.jbj.ast.stmt.ExprStmt
import de.leanovate.jbj.ast.expr.calc.NegExpr
import de.leanovate.jbj.ast.stmt.ParameterDef
import de.leanovate.jbj.ast.expr.DotExpr
import de.leanovate.jbj.ast.expr.calc.MulExpr
import de.leanovate.jbj.parser.JbjTokens.InterpolatedStringLit
import de.leanovate.jbj.ast.expr.PropertyReference
import de.leanovate.jbj.parser.JbjTokens.Identifier
import de.leanovate.jbj.ast.stmt.loop.ForeachValueStmt
import de.leanovate.jbj.ast.expr.comp.LeExpr
import de.leanovate.jbj.parser.JbjTokens.LongNumLit
import de.leanovate.jbj.ast.expr.CallFunctionExpr
import de.leanovate.jbj.runtime.context.GlobalContext
import de.leanovate.jbj.ast.expr.comp.EqExpr
import de.leanovate.jbj.ast.expr.MethodCallReference
import de.leanovate.jbj.ast.stmt.loop.ForeachKeyValueStmt
import de.leanovate.jbj.ast.expr.comp.BoolOrExpr
import de.leanovate.jbj.ast.FilePosition
import de.leanovate.jbj.runtime.value.IntegerVal
import de.leanovate.jbj.ast.expr.comp.GtExpr
import de.leanovate.jbj.ast.stmt.EchoStmt
import de.leanovate.jbj.ast.expr.calc.DivExpr
import de.leanovate.jbj.ast.stmt.loop.ForStmt
import de.leanovate.jbj.ast.expr.comp.BoolAndExpr
import de.leanovate.jbj.ast.stmt.cond.IfStmt
import de.leanovate.jbj.ast.stmt.FunctionDeclStmt
import de.leanovate.jbj.ast.expr.value.ConstGetExpr
import de.leanovate.jbj.ast.stmt.BlockStmt
import de.leanovate.jbj.runtime.value.StringVal
import de.leanovate.jbj.parser.JbjTokens.Variable
import de.leanovate.jbj.ast.stmt.BreakStmt
import de.leanovate.jbj.ast.expr.comp.LtExpr
import de.leanovate.jbj.ast.stmt.loop.DoWhileStmt
import de.leanovate.jbj.ast.expr.comp.GeExpr
import de.leanovate.jbj.ast.stmt.loop.WhileStmt
import de.leanovate.jbj.ast.stmt.cond.ElseIfBlock
import de.leanovate.jbj.ast.expr.ScalarExpr
import de.leanovate.jbj.ast.stmt.StaticVarDeclStmt
import de.leanovate.jbj.ast.stmt.LabelStmt
import de.leanovate.jbj.ast.stmt.StaticAssignment
import de.leanovate.jbj.ast.expr.calc.BitOrExpr
import de.leanovate.jbj.ast.stmt.cond.CaseBlock
import de.leanovate.jbj.ast.expr.GetAndDecrExpr
import de.leanovate.jbj.ast.stmt.ContinueStmt
import de.leanovate.jbj.ast.expr.calc.BitXorExpr
import de.leanovate.jbj.ast.expr.IncrAndGetExpr
import de.leanovate.jbj.ast.expr.DecrAndGetExpr
import de.leanovate.jbj.ast.expr.ArrayCreateExpr
import de.leanovate.jbj.ast.stmt.InlineStmt
import de.leanovate.jbj.parser.JbjTokens.Keyword
import de.leanovate.jbj.parser.JbjTokens.StringLit
import de.leanovate.jbj.ast.expr.comp.BoolXorExpr
import de.leanovate.jbj.ast.expr.GetAndIncrExpr

object JbjParser extends Parsers {
  type Elem = JbjTokens.Token

  private val keywordCache = mutable.HashMap[String, Parser[Keyword]]()

  def parse(s: String): ParseResult[Prog] = {
    val tokens = new JbjInitialLexer(s)
    phrase(start)(tokens)
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

  private def start: Parser[Prog] = topStatementList ^^ {
    stmts => Prog(FilePosition("-", 0), stmts)
  }

  private def topStatementList: Parser[List[Stmt]] = rep(topStatement)

  private def topStatement: Parser[Stmt] = statement | functionDeclarationStatement | classDeclarationStatement

  private def innerStatementList: Parser[List[Stmt]] = rep(innerStatement)

  private def namespaceName: Parser[NamespaceName] = rep1sep(identLit, "\\") ^^ {
    path => NamespaceName(path.map(_.chars): _*)
  }

  private def innerStatement: Parser[Stmt] = statement

  private def statement: Parser[Stmt] = identLit <~ ":" ^^ {
    label => LabelStmt(label.position, label.chars)
  } | untickedStatement <~ rep(";")

  private def untickedStatement: Parser[Stmt] =
    "{" ~ innerStatementList <~ "}" ^^ {
      case paren ~ stmts => BlockStmt(paren.position, stmts)
    } | "if" ~ parenthesisExpr ~ statement ~ elseIfList ~ elseSingle ^^ {
      case ifT ~ cond ~ thenStmt ~ elseIfs ~ elseStmt =>
        IfStmt(ifT.position, cond, thenStmt :: Nil, elseIfs, elseStmt)
    } | "if" ~ parenthesisExpr ~ ":" ~ innerStatementList ~ newElseIfList ~ newElseSingle <~ "endif" <~ ";" ^^ {
      case ifT ~ cond ~ _ ~ thenStmts ~ elseIfs ~ elseStmts =>
        IfStmt(ifT.position, cond, thenStmts, elseIfs, elseStmts)
    } | "while" ~ parenthesisExpr ~ whileStatement ^^ {
      case whileT ~ cond ~ stmts => WhileStmt(whileT.position, cond, stmts)
    } | "do" ~ statement ~ "while" ~ parenthesisExpr <~ ";" ^^ {
      case doT ~ stmt ~ _ ~ cond => DoWhileStmt(doT.position, stmt :: Nil, cond)
    } | "for" ~ "(" ~ forExpr ~ ";" ~ forExpr ~ ";" ~ forExpr ~ ")" ~ forStatement ^^ {
      case forT ~ _ ~ befores ~ _ ~ conds ~ _ ~ afters ~ _ ~ stmts => ForStmt(forT.position, befores, conds, afters, stmts)
    } | "switch" ~ parenthesisExpr ~ switchCaseList ^^ {
      case switchT ~ expr ~ cases => SwitchStmt(switchT.position, expr, cases)
    } | "break" ~ opt(expr) ^^ {
      case br ~ depth => BreakStmt(br.position, depth)
    } | "continue" ~ opt(expr) ^^ {
      case con ~ depth => ContinueStmt(con.position, depth)
    } | "return" ~ opt(expr) <~ ";" ^^ {
      case ret ~ expr => ReturnStmt(ret.position, expr)
    } | "global" ~ globalVarList ^^ {
      case globalT ~ vars => GlobalVarDeclAssignStmt(globalT.position, vars)
    } | "static" ~ staticVarList ^^ {
      case staticT ~ vars => StaticVarDeclStmt(staticT.position, vars)
    } | "echo" ~ echoExprList <~ ";" ^^ {
      case echo ~ params => EchoStmt(echo.position, params)
    } | inlineHtml | expr <~ ";" ^^ {
      expr => ExprStmt(expr)
    } | "foreach" ~ "(" ~ exprWithoutVariable ~ "as" ~ foreachVariable ~ foreachOptionalArg ~ ")" ~ foreachStatement ^^ {
      case foreachT ~ _ ~ array ~ _ ~ valueVar ~ None ~ _ ~ stmts =>
        ForeachValueStmt(foreachT.position, array, valueVar.variableName, stmts)
      case foreachT ~ _ ~ array ~ _ ~ keyVar ~ Some(valueVar) ~ _ ~ stmts =>
        ForeachKeyValueStmt(foreachT.position, array, keyVar.variableName, valueVar.variableName, stmts)
    }

  private def functionDeclarationStatement: Parser[FunctionDeclStmt] = untickedFunctionDeclarationStatement <~ rep(";")

  private def classDeclarationStatement: Parser[ClassDeclStmt] = untickedClassDeclarationStatement <~ rep(";")

  private def untickedFunctionDeclarationStatement: Parser[FunctionDeclStmt] =
    "function" ~ opt("&") ~ identLit ~ "(" ~ parameterList ~ ")" ~ "{" ~ innerStatementList <~ "}" ^^ {
      case func ~ isRef ~ name ~ _ ~ params ~ _ ~ _ ~ body => FunctionDeclStmt(func.position, name.chars, params, body)
    }

  private def untickedClassDeclarationStatement: Parser[ClassDeclStmt] =
    classEntryType ~ identLit ~ extendsFrom ~ implementsList ~ "{" ~ classStatementList <~ "}" ^^ {
      case classEntry ~ name ~ extendsFrom ~ implementsList ~ _ ~ stmts =>
        ClassDeclStmt(name.position, classEntry, name.chars, extendsFrom, implementsList, stmts)
    }

  private def classEntryType: Parser[ClassEntry.Type] =
    "class" ^^^ ClassEntry.CLASS | "abstract" ~ "class" ^^^ ClassEntry.ABSTRACT_CLASS |
      "final" ~ "class" ^^^ ClassEntry.FINAL_CLASS | "trait" ^^^ ClassEntry.TRAIT

  private def extendsFrom: Parser[Option[NamespaceName]] = opt("extends" ~> fullyQualifiedClassName)

  private def implementsList: Parser[List[NamespaceName]] = opt("implements" ~> interfaceList) ^^ {
    optInterfaces => optInterfaces.getOrElse(Nil)
  }

  private def interfaceList: Parser[List[NamespaceName]] = rep1sep(fullyQualifiedClassName, ",")

  private def foreachOptionalArg: Parser[Option[VariableReference]] = opt("=>" ~> variable)

  private def foreachVariable: Parser[VariableReference] = variable

  private def forStatement: Parser[List[Stmt]] =
    ":" ~> innerStatementList <~ "endfor" <~ ";" | statement ^^ (_ :: Nil)

  private def foreachStatement: Parser[List[Stmt]] =
    ":" ~> innerStatementList <~ "endforeach" <~ ";" | statement ^^ (_ :: Nil)

  private def switchCaseList: Parser[List[SwitchCase]] =
    "{" ~> opt(";") ~> caseList <~ "}" |
      ":" ~> opt(";") ~> caseList <~ "endswitch" <~ ";"

  private def caseList: Parser[List[SwitchCase]] = rep(
    "case" ~> expr ~ caseSeparator ~ innerStatementList ^^ {
      case expr ~ _ ~ stmts => CaseBlock(expr, stmts)
    } | "default" ~> caseSeparator ~> innerStatementList ^^ {
      case stmts => DefaultCaseBlock(stmts)
    }
  )

  private def caseSeparator: Parser[Any] = ":" | ";"

  private def whileStatement: Parser[List[Stmt]] = ":" ~> innerStatementList <~ "endwhile" <~ ";" | statement ^^ (_ :: Nil)

  private def elseIfList: Parser[List[ElseIfBlock]] = rep("elseif" ~> parenthesisExpr ~ statement ^^ {
    case cond ~ stmt => ElseIfBlock(cond, stmt :: Nil)
  })

  private def newElseIfList: Parser[List[ElseIfBlock]] = rep("elseif" ~> parenthesisExpr ~ ":" ~ innerStatementList ^^ {
    case cond ~ _ ~ stmts => ElseIfBlock(cond, stmts)
  })

  private def elseSingle: Parser[List[Stmt]] = opt("else" ~> statement) ^^ (_.toList)

  private def newElseSingle: Parser[List[Stmt]] = opt("else" ~> ":" ~> innerStatementList) ^^ (_.toList.flatten)

  private def parameterList: Parser[List[ParameterDef]] = repsep(parameterDef, ",")

  private def globalVarList: Parser[List[String]] = rep1sep(variableLit ^^ {
    v => v.name
  }, ",")

  private def staticVarList: Parser[List[StaticAssignment]] = rep1sep(variableLit ~ opt("=" ~> staticScalar) ^^ {
    case v ~ optScalar => StaticAssignment(v.position, v.name, optScalar.map(_.value).getOrElse(UndefinedVal))
  }, ",")

  private def classStatementList: Parser[List[Stmt]] = rep(classStatement)

  private def classStatement: Parser[Stmt] = "function" ^^^ BreakStmt(null, None)

  private def methodBody: Parser[List[Stmt]] = ";" ^^^ Nil | "{" ~> innerStatementList <~ "}"

  private def variableModifiers: Parser[Set[MemberModifier.Type]] = nonEmptyMemberModifiers |
    "var" ^^^ Set.empty[MemberModifier.Type]

  private def nonEmptyMemberModifiers: Parser[Set[MemberModifier.Type]] = rep1(memberModifier) ^^ (_.toSet)

  private def memberModifier: Parser[MemberModifier.Type] = "public" ^^^ MemberModifier.PUBLIC | "protected" ^^^ MemberModifier.PROTECTED |
    "private" ^^^ MemberModifier.PRIVATE | "static" ^^^ MemberModifier.STATIC | "final" ^^^ MemberModifier.FINAL |
    "abstract" ^^^ MemberModifier.ABSTRACT

  private def echoExprList: Parser[List[Expr]] = rep1sep(expr, ",")

  private def forExpr: Parser[List[Expr]] = repsep(expr, ",")

  private def exprWithoutVariable: Parser[Expr] = expr

  private def parenthesisExpr: Parser[Expr] = "(" ~> expr <~ ")"

  private def fullyQualifiedClassName: Parser[NamespaceName] = namespaceName

  private def commonScalar: Parser[ScalarExpr] =
    longNumLit ^^ {
      s => ScalarExpr(s.position, IntegerVal(s.value))
    } | doubleNumLit ^^ {
      s => ScalarExpr(s.position, FloatVal(s.value))
    } | stringLit ^^ {
      s => ScalarExpr(s.position, StringVal(s.chars))
    }

  private def staticScalar: Parser[ScalarExpr] =
    commonScalar | "+" ~> staticScalar ^^ {
      s => ScalarExpr(s.position, s.value.toNum)
    } | "-" ~> staticScalar ^^ {
      s => ScalarExpr(s.position, s.value.toNum.neg)
    }

  private def value: Parser[Expr] =
    (staticScalar
      | interpolatedStringLit ^^ (s => InterpolatedStringExpr(s.position, s.charOrInterpolations))
      )

  private def variable: Parser[VariableReference] = variableLit ^^ {
    case v => VariableReference(v.position, v.name)
  }

  private def reference: Parser[Reference] = variable ~ rep(refAccess) ^^ {
    case variable ~ refAccesses => refAccesses.foldLeft(variable.asInstanceOf[Reference]) {
      (ref, refAccess) => refAccess(ref)
    }
  }

  private def refAccess: Parser[Reference => Reference] = "[" ~> expr <~ "]" ^^ {
    expr => IndexReference(_: Reference, expr)
  } | "->" ~> identLit ~ "(" ~ repsep(expr, ",") <~ ")" ^^ {
    case method ~ _ ~ params => MethodCallReference(_: Reference, method.chars, params)
  } | "->" ~> identLit ^^ {
    property => PropertyReference(_: Reference, property.chars)
  }

  private def referenceExpr: Parser[Expr] = reference <~ "++" ^^ {
    ref => GetAndIncrExpr(ref)
  } | reference <~ "--" ^^ {
    ref => GetAndDecrExpr(ref)
  } | "++" ~> reference ^^ {
    ref => IncrAndGetExpr(ref)
  } | "--" ~> reference ^^ {
    ref => DecrAndGetExpr(ref)
  } | reference |
    "array" ~ "(" ~ repsep(arrayValues, ",") <~ ")" ^^ {
      case array ~ _ ~ arrayValues => ArrayCreateExpr(array.position, arrayValues)
    }

  private def arrayValues: Parser[(Option[Expr], Expr)] = expr ~ "=>" ~ expr ^^ {
    case indexExpr ~ _ ~ valueExpr => (Some(indexExpr), valueExpr)
  } | expr ^^ {
    valueExpr => (None, valueExpr)
  }

  private def functionCall: Parser[Expr] = identLit ~ "(" ~ repsep(expr, ",") <~ ")" ^^ {
    case name ~ _ ~ params => CallFunctionExpr(name.position, name.chars, params)
  }

  private def parens: Parser[Expr] = "(" ~> expr <~ ")"

  private def neg: Parser[NegExpr] = "-" ~ term ^^ {
    case sign ~ term => NegExpr(sign.position, term)
  }

  private def constant: Parser[Expr] = identLit ^^ {
    name => ConstGetExpr(name.position, name.chars)
  }

  private def term: Parser[Expr] = value | referenceExpr | functionCall | constant | parens | neg

  private def binaryOp(level: Int): Parser[((Expr, Expr) => Expr)] = {
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

  private def parameterDef: Parser[ParameterDef] = variable ^^ (v => ParameterDef(v.variableName, byRef = false, default = None))

  private def inlineHtml: Parser[InlineStmt] =
    elem("inline", _.isInstanceOf[Inline]) ^^ {
      t => InlineStmt(t.position, t.chars)
    }

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
  private def longNumLit: Parser[LongNumLit] =
    elem("long number", _.isInstanceOf[LongNumLit]) ^^ (_.asInstanceOf[LongNumLit])

  /** A parser which matches a numeric literal */
  private def doubleNumLit: Parser[DoubleNumLit] =
    elem("double number", _.isInstanceOf[DoubleNumLit]) ^^ (_.asInstanceOf[DoubleNumLit])

  /** A parser which matches a string literal */
  private def stringLit: Parser[StringLit] =
    elem("string literal", _.isInstanceOf[StringLit]) ^^ (_.asInstanceOf[StringLit])

  private def interpolatedStringLit: Parser[InterpolatedStringLit] =
    elem("interpolated string literal", _.isInstanceOf[InterpolatedStringLit]) ^^ (_.asInstanceOf[InterpolatedStringLit])

  /** A parser which matches an identifier */
  private def identLit: Parser[Identifier] =
    elem("identifier", _.isInstanceOf[Identifier]) ^^ (_.asInstanceOf[Identifier])

  private def variableLit: Parser[Variable] =
    elem("variable", _.isInstanceOf[Variable]) ^^ (_.asInstanceOf[Variable])


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
            |$a = 5;
            |
            |var_dump($a);
            |
            |static $a = "aa";
            |static $a = 11;
            |
            |var_dump($a);
            |
            |function foo() {
            |	static $a = 13;
            |	static $a = 14;
            |
            |	var_dump($a);
            |}
            |
            |foo();
            |
            |?>""".stripMargin)
  }
}
