package de.leanovate.jbj.parser

import de.leanovate.jbj.ast._
import de.leanovate.jbj.ast.stmt.cond._
import de.leanovate.jbj.ast.stmt.loop._
import de.leanovate.jbj.ast.expr._
import de.leanovate.jbj.ast.expr.value._
import de.leanovate.jbj.ast.expr.calc._
import de.leanovate.jbj.ast.expr.comp._
import de.leanovate.jbj.ast.name.{StaticName, DynamicName}
import de.leanovate.jbj.parser.JbjTokens._
import de.leanovate.jbj.ast.stmt._
import de.leanovate.jbj.runtime.value._
import scala.collection.mutable
import scala.util.parsing.combinator.Parsers
import scala.language.implicitConversions
import scala.Some
import de.leanovate.jbj.runtime.context.GlobalContext

class JbjParser(parseCtx: ParseContext) extends Parsers {
  type Elem = JbjTokens.Token

  private val keywordCache = mutable.HashMap[String, Parser[String]]()

  def parse(s: String): Prog = {
    val tokens = new JbjInitialLexer(s)
    phrase(start)(tokens) match {
      case Success(tree, _) => tree
      case e: NoSuccess =>
        throw new IllegalArgumentException("Bad syntax: " + e)
    }
  }

  def parseExpr(s: String): Expr = {
    val tokens = new JbjScriptLexer(s)
    phrase(expr)(tokens) match {
      case Success(expr, _) => expr
      case e: NoSuccess =>
        throw new IllegalArgumentException("Bad syntax: " + e)
    }
  }

  private def start: Parser[Prog] = withPos(topStatementList ^^ {
    stmts => Prog(stmts)
  })

  private def topStatementList: Parser[List[Stmt]] = rep(topStatement)

  private def namespaceName: Parser[NamespaceName] = rep1sep(identLit, "\\") ^^ {
    path => NamespaceName(path: _*)
  }

  private def topStatement: Parser[Stmt] = withPos(statement | functionDeclarationStatement |
    classDeclarationStatement | constantDeclaration)

  private def constantDeclaration: Parser[Stmt] = "const" ~> rep1(identLit ~ "=" ~ staticScalar ^^ {
    case name ~ _ ~ scalar => StaticAssignment(name, scalar.value)
  }) ^^ {
    assignments => ConstDeclStmt(assignments)
  }

  private def innerStatementList: Parser[List[Stmt]] = rep(innerStatement)

  private def innerStatement: Parser[Stmt] = withPos(statement)

  private def statement: Parser[Stmt] = identLit <~ ":" ^^ {
    label => LabelStmt(label)
  } | untickedStatement <~ rep(";")

  private def untickedStatement: Parser[Stmt] =
    "{" ~> innerStatementList <~ "}" ^^ {
      stmts => BlockStmt(stmts)
    } | "if" ~> parenthesisExpr ~ statement ~ elseIfList ~ elseSingle ^^ {
      case cond ~ thenStmt ~ elseIfs ~ elseStmt =>
        IfStmt(cond, thenStmt :: Nil, elseIfs, elseStmt)
    } | "if" ~> parenthesisExpr ~ ":" ~ innerStatementList ~ newElseIfList ~ newElseSingle <~ "endif" <~ ";" ^^ {
      case cond ~ _ ~ thenStmts ~ elseIfs ~ elseStmts =>
        IfStmt(cond, thenStmts, elseIfs, elseStmts)
    } | "while" ~> parenthesisExpr ~ whileStatement ^^ {
      case cond ~ stmts => WhileStmt(cond, stmts)
    } | "do" ~> statement ~ "while" ~ parenthesisExpr <~ ";" ^^ {
      case stmt ~ _ ~ cond => DoWhileStmt(stmt :: Nil, cond)
    } | "for" ~> "(" ~> forExpr ~ ";" ~ forExpr ~ ";" ~ forExpr ~ ")" ~ forStatement ^^ {
      case befores ~ _ ~ conds ~ _ ~ afters ~ _ ~ stmts => ForStmt(befores, conds, afters, stmts)
    } | "switch" ~> parenthesisExpr ~ switchCaseList ^^ {
      case expr ~ cases => SwitchStmt(expr, cases)
    } | "break" ~> opt(expr) ^^ {
      depth => BreakStmt(depth)
    } | "continue" ~> opt(expr) ^^ {
      depth => ContinueStmt(depth)
    } | "return" ~> opt(expr) <~ ";" ^^ {
      expr => ReturnStmt(expr)
    } | "global" ~> globalVarList ^^ {
      vars => GlobalVarDeclAssignStmt(vars)
    } | "static" ~> staticVarList ^^ {
      vars => StaticVarDeclStmt(vars)
    } | "echo" ~> echoExprList <~ ";" ^^ {
      params => EchoStmt(params)
    } | inlineHtml | expr <~ ";" ^^ {
      expr => ExprStmt(expr)
    } | "foreach" ~> "(" ~> exprWithoutVariable ~ "as" ~ foreachVariable ~ foreachOptionalArg ~ ")" ~ foreachStatement ^^ {
      case array ~ _ ~ valueVar ~ None ~ _ ~ stmts =>
        ForeachValueStmt(array, valueVar, stmts)
      case array ~ _ ~ keyVar ~ Some(valueVar) ~ _ ~ stmts =>
        ForeachKeyValueStmt(array, keyVar, valueVar, stmts)
    } | "try" ~> "{" ~> innerStatementList ~ "}" ~ catchStatement ~ finallyStatement ^^ {
      case tryStmts ~ _ ~ catchBlocks ~ finallyStmts => TryCatchStmt(tryStmts, catchBlocks, finallyStmts)
    } | "throw" ~> expr ^^ {
      expr => ThrowStmt(expr)
    }

  private def catchStatement: Parser[List[CatchBlock]] = rep(
    "catch" ~> "(" ~> fullyQualifiedClassName ~ variableLit ~ ")" ~ "{" ~ innerStatementList <~ "}" ^^ {
      case exceptionName ~ variable ~ _ ~ _ ~ stmts => CatchBlock(exceptionName, variable, stmts)
    })

  private def finallyStatement: Parser[List[Stmt]] =
    opt("finally" ~> "{" ~> innerStatementList <~ "}") ^^ (_.getOrElse(Nil))

  private def functionDeclarationStatement: Parser[FunctionDeclStmt] = untickedFunctionDeclarationStatement <~ rep(";")

  private def classDeclarationStatement: Parser[ClassDeclStmt] = untickedClassDeclarationStatement <~ rep(";")

  private def untickedFunctionDeclarationStatement: Parser[FunctionDeclStmt] =
    "function" ~ opt("&") ~ identLit ~ "(" ~ parameterList ~ ")" ~ "{" ~ innerStatementList <~ "}" ^^ {
      case func ~ isRef ~ name ~ _ ~ params ~ _ ~ _ ~ body => FunctionDeclStmt(name, params, body)
    }

  private def untickedClassDeclarationStatement: Parser[ClassDeclStmt] =
    classEntryType ~ identLit ~ extendsFrom ~ implementsList ~ "{" ~ classStatementList <~ "}" ^^ {
      case classEntry ~ name ~ extendsFrom ~ implementsList ~ _ ~ stmts =>
        ClassDeclStmt(classEntry, name, extendsFrom, implementsList, stmts)
    }

  private def classEntryType: Parser[ClassEntry.Type] =
    "class" ^^^ ClassEntry.CLASS | "abstract" ~ "class" ^^^ ClassEntry.ABSTRACT_CLASS |
      "final" ~ "class" ^^^ ClassEntry.FINAL_CLASS | "trait" ^^^ ClassEntry.TRAIT

  private def extendsFrom: Parser[Option[NamespaceName]] = opt("extends" ~> fullyQualifiedClassName)

  private def implementsList: Parser[List[NamespaceName]] = opt("implements" ~> interfaceList) ^^ {
    optInterfaces => optInterfaces.getOrElse(Nil)
  }

  private def interfaceList: Parser[List[NamespaceName]] = rep1sep(fullyQualifiedClassName, ",")

  private def foreachOptionalArg: Parser[Option[Reference]] = opt("=>" ~> variable)

  private def foreachVariable: Parser[Reference] = variable

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
    })

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

  private def parameterList: Parser[List[ParameterDecl]] = repsep(
    optionalClassType ~ opt("&") ~ variableLit ~ opt("=" ~> staticScalar) ^^ {
      case typeHint ~ optRef ~ variable ~ optDefault =>
        ParameterDecl(typeHint, variable, optRef.isDefined, optDefault.map(_.value))
    }, ",")

  private def optionalClassType: Parser[Option[TypeHint]] = opt("array" ^^^ ArrayTypeHint |
    "callable" ^^^ CallableTypeHint | fullyQualifiedClassName ^^ (name => ClassTypeHint(name)))

  private def functionCallParameterList: Parser[List[Expr]] = "(" ~> repsep(exprWithoutVariable | variable, ",") <~ ")"

  private def globalVarList: Parser[List[String]] = rep1sep(variableLit, ",")

  private def staticVarList: Parser[List[StaticAssignment]] = rep1sep(variableLit ~ opt("=" ~> staticScalar) ^^ {
    case v ~ optScalar => StaticAssignment(v, optScalar.map(_.value).getOrElse(UndefinedVal))
  }, ",")

  private def classStatementList: Parser[List[Stmt]] = rep(classStatement)

  private def classStatement: Parser[Stmt] = variableModifiers ~ classVariableDeclaration <~ ";" ^^ {
    case modifiers ~ assignments => ClassVarDeclStmt(modifiers, assignments)
  } | classConstantDeclaration ^^ {
    assignments => ClassConstDeclStmt(assignments)
  } | traitUseStatement |
    methodModifiers ~ "function" ~ opt("&") ~ identLit ~ "(" ~ parameterList ~ ")" ~ methodBody ^^ {
      case modifiers ~ _ ~ optRef ~ name ~ _ ~ params ~ _ ~ stmts => ClassMethodDeclStmt(modifiers, name, params, stmts)
    }

  private def traitUseStatement: Parser[TraitUseStmt] = "use" ~> traitList <~ ";" ^^ {
    traits => TraitUseStmt(traits)
  }

  private def traitList: Parser[List[NamespaceName]] = rep1(fullyQualifiedClassName)

  private def methodBody: Parser[List[Stmt]] = ";" ^^^ Nil | "{" ~> innerStatementList <~ "}"

  private def variableModifiers: Parser[Set[MemberModifier.Type]] = nonEmptyMemberModifiers |
    "var" ^^^ Set.empty[MemberModifier.Type]

  private def methodModifiers: Parser[Set[MemberModifier.Type]] = opt(nonEmptyMemberModifiers) ^^ {
    optModifiers => optModifiers.getOrElse(Set.empty[MemberModifier.Type])
  }

  private def nonEmptyMemberModifiers: Parser[Set[MemberModifier.Type]] =
    rep1(memberModifier) ^^ (_.toSet)

  private def memberModifier: Parser[MemberModifier.Type] =
    "public" ^^^ MemberModifier.PUBLIC | "protected" ^^^ MemberModifier.PROTECTED |
      "private" ^^^ MemberModifier.PRIVATE | "static" ^^^ MemberModifier.STATIC |
      "final" ^^^ MemberModifier.FINAL | "abstract" ^^^ MemberModifier.ABSTRACT

  private def classVariableDeclaration: Parser[List[StaticAssignment]] =
    rep1sep(variableLit ~ opt("=" ~> staticScalar) ^^ {
      case variable ~ optScalar =>
        StaticAssignment(variable, optScalar.map(_.value).getOrElse(UndefinedVal))
    }, ",")

  private def classConstantDeclaration: Parser[List[StaticAssignment]] =
    "const" ~> rep1sep(identLit ~ "=" ~ staticScalar ^^ {
      case name ~ _ ~ scalar => StaticAssignment(name, scalar.value)
    }, ",")

  private def echoExprList: Parser[List[Expr]] = rep1sep(expr, ",")

  private def forExpr: Parser[List[Expr]] = repsep(expr, ",")

  private def newExpr: Parser[NewExpr] = "new" ~> classNameReference ^^ {
    className => NewExpr(className)
  }

  private def exprWithoutVariable: Parser[Expr] =
    variable ~ "=" ~ expr ^^ {
      case variable ~ _ ~ expr => AssignExpr(variable, expr)
    } | variable ~ "+=" ~ expr ^^ {
      case variable ~ _ ~ expr => AddToExpr(variable, expr)
    } | variable ~ "-=" ~ expr ^^ {
      case variable ~ _ ~ expr => SubFromExpr(variable, expr)
    } | variable ~ "*=" ~ expr ^^ {
      case variable ~ _ ~ expr => MulByExpr(variable, expr)
    } | variable ~ "/=" ~ expr ^^ {
      case variable ~ _ ~ expr => DivByExpr(variable, expr)
    } | variable ~ ".=" ~ expr ^^ {
      case variable ~ _ ~ expr => ConcatWithExpr(variable, expr)
    } | newExpr | binary(minPrec) | termWithoutVariable

  private def binaryOp(level: Int): Parser[((Expr, Expr) => Expr)] = {
    level match {
      case 1 => "or" ^^^ ((a: Expr, b: Expr) => BoolOrExpr(a, b))
      case 2 => "xor" ^^^ ((a: Expr, b: Expr) => BoolXorExpr(a, b))
      case 3 => "and" ^^^ ((a: Expr, b: Expr) => BoolAndExpr(a, b))
      case 4 => "||" ^^^ ((a: Expr, b: Expr) => BoolOrExpr(a, b))
      case 5 => "&&" ^^^ ((a: Expr, b: Expr) => BoolAndExpr(a, b))
      case 6 => "|" ^^^ ((a: Expr, b: Expr) => BitOrExpr(a, b))
      case 7 => "" ^^^ ((a: Expr, b: Expr) => BitXorExpr(a, b))
      case 8 => "&" ^^^ ((a: Expr, b: Expr) => BitAndExpr(a, b))
      case 9 =>
        "==" ^^^ ((a: Expr, b: Expr) => EqExpr(a, b)) |
          "!=" ^^^ ((a: Expr, b: Expr) => NotEqExpr(a, b)) |
          "<>" ^^^ ((a: Expr, b: Expr) => NotEqExpr(a, b))
      case 10 =>
        ">" ^^^ ((a: Expr, b: Expr) => GtExpr(a, b)) |
          ">=" ^^^ ((a: Expr, b: Expr) => GeExpr(a, b)) |
          "<" ^^^ ((a: Expr, b: Expr) => LtExpr(a, b)) |
          "<=" ^^^ ((a: Expr, b: Expr) => LeExpr(a, b))
      case 11 =>
        "." ^^^ ((a: Expr, b: Expr) => ConcatExpr(a, b)) |
          "+" ^^^ ((a: Expr, b: Expr) => AddExpr(a, b)) |
          "-" ^^^ ((a: Expr, b: Expr) => SubExpr(a, b))
      case 12 =>
        "*" ^^^ ((a: Expr, b: Expr) => MulExpr(a, b)) |
          "/" ^^^ ((a: Expr, b: Expr) => DivExpr(a, b))
      case _ => throw new RuntimeException("bad precedence level " + level)
    }
  }

  val minPrec = 1

  val maxPrec = 12

  def binary(level: Int): Parser[Expr] =
    if (level > maxPrec) {
      term
    }
    else {
      binary(level + 1) * binaryOp(level)
    }

  private def termWithoutVariable: Parser[Expr] =
    rwVariable <~ "++" ^^ {
      ref => GetAndIncrExpr(ref)
    } | rwVariable <~ "--" ^^ {
      ref => GetAndDecrExpr(ref)
    } | "++" ~> rwVariable ^^ {
      ref => IncrAndGetExpr(ref)
    } | "--" ~> rwVariable ^^ {
      ref => DecrAndGetExpr(ref)
    } | scalar | combinedScalar | constant | parenthesisExpr | "-" ~> term ^^ {
      expr => NegExpr(expr)
    } | "+" ~> term

  private def combinedScalar: Parser[Expr] = "array" ~> "(" ~> arrayPairList <~ ")" ^^ {
    keyValues => ArrayCreateExpr(keyValues)
  }

  private def functionCall: Parser[Reference] = namespaceName ~ functionCallParameterList ^^ {
    case name ~ params => CallFunctionReference(name, params)
  }

  private def className: Parser[NamespaceName] = namespaceName

  private def fullyQualifiedClassName: Parser[NamespaceName] = namespaceName

  private def classNameReference: Parser[NamespaceName] = className

  private def ctorArguments: Parser[List[Expr]] = functionCallParameterList

  private def commonScalar: Parser[ScalarExpr] =
    longNumLit ^^ {
      s => ScalarExpr(IntegerVal(s))
    } | doubleNumLit ^^ {
      s => ScalarExpr(FloatVal(s))
    } | stringLit ^^ {
      s => ScalarExpr(StringVal(s))
    }

  private def staticScalar: Parser[ScalarExpr] =
    commonScalar | "+" ~> staticScalar ^^ {
      s => ScalarExpr(s.value.toNum)
    } | "-" ~> staticScalar ^^ {
      s => ScalarExpr(-s.value.toNum)
    }

  private def scalar: Parser[Expr] = commonScalar | interpolatedStringLit ^^ (s => InterpolatedStringExpr(parseCtx, s.charOrInterpolations))

  private def expr: Parser[Expr] = exprWithoutVariable ||| rVariable

  private def term: Parser[Expr] = termWithoutVariable ||| rVariable

  private def parenthesisExpr: Parser[Expr] = "(" ~> expr <~ ")"

  private def rVariable: Parser[Reference] = variable

  private def rwVariable: Parser[Reference] = variable

  private def variable: Parser[Reference] = baseVariableWithFunctionCalls

  private def baseVariableWithFunctionCalls: Parser[Reference] = baseVariable | functionCall

  private def baseVariable: Parser[Reference] = referenceVariable

  private def referenceVariable: Parser[Reference] = compoundVariable ~ rep(
    "[" ~> dimOffset <~ "]" | "{" ~> expr <~ "}" ^^ (Some(_))
  ) ^^ {
    case v ~ dims => dims.foldLeft(v.asInstanceOf[Reference]) {
      (ref, dim) => IndexReference(ref, dim)
    }
  }

  private def compoundVariable: Parser[Reference] = variableLit ^^ {
    v => VariableReference(StaticName(v))
  } | "$" ~> "{" ~> expr <~ "}" ^^ {
    expr => VariableReference(DynamicName(expr))
  }

  private def dimOffset: Parser[Option[Expr]] = opt(expr)

  private def arrayPairList: Parser[List[(Option[Expr], Expr)]] = repsep(
    expr ~ opt("=>" ~> expr) ^^ {
      case keyExpr ~ Some(valueExpr) => Some(keyExpr) -> valueExpr
      case valueExpr ~ None => None -> valueExpr
    }, ",") <~ opt(",")


  private def reference: Parser[Reference] = variable ~ rep(refAccess) ^^ {
    case variable ~ refAccesses => refAccesses.foldLeft(variable.asInstanceOf[Reference]) {
      (ref, refAccess) => refAccess(ref)
    }
  }

  private def refAccess: Parser[Reference => Reference] = "[" ~> expr <~ "]" ^^ {
    expr => IndexReference(_: Reference, Some(expr))
  } | "->" ~> identLit ~ "(" ~ repsep(expr, ",") <~ ")" ^^ {
    case method ~ _ ~ params => MethodCallReference(_: Reference, method, params)
  } | "->" ~> identLit ^^ {
    property => PropertyReference(_: Reference, property)
  }

  private def referenceExpr: Parser[Expr] =
    reference <~ "++" ^^ {
      ref => GetAndIncrExpr(ref)
    } | reference <~ "--" ^^ {
      ref => GetAndDecrExpr(ref)
    } | "++" ~> reference ^^ {
      ref => IncrAndGetExpr(ref)
    } | "--" ~> reference ^^ {
      ref => DecrAndGetExpr(ref)
    } | reference |
      "array" ~ "(" ~ repsep(arrayValues, ",") <~ opt(",") <~ ")" ^^ {
        case array ~ _ ~ arrayValues => ArrayCreateExpr(arrayValues)
      }

  private def arrayValues: Parser[(Option[Expr], Expr)] = expr ~ "=>" ~ expr ^^ {
    case indexExpr ~ _ ~ valueExpr => (Some(indexExpr), valueExpr)
  } | expr ^^ {
    valueExpr => (None, valueExpr)
  }

  private def constant: Parser[Expr] = identLit ^^ {
    name => ConstGetExpr(name)
  }


  private def inlineHtml: Parser[InlineStmt] =
    elem("inline", _.isInstanceOf[Inline]) ^^ {
      t => InlineStmt(t.chars)
    }

  /** A parser which matches a single keyword token.
    *
    * @param chars    The character string making up the matched keyword.
    * @return a `Parser` that matches the given string
    */
  //  implicit def keyword(chars: String): Parser[String] = accept(Keyword(chars)) ^^ (_.chars)
  implicit def keyword(chars: String): Parser[String] =
    keywordCache.getOrElseUpdate(chars, accept(Keyword(chars)) ^^ (_.chars))

  /** A parser which matches a numeric literal */
  private def longNumLit: Parser[Long] =
    elem("long number", _.isInstanceOf[LongNumLit]) ^^ (_.asInstanceOf[LongNumLit].value)

  /** A parser which matches a numeric literal */
  private def doubleNumLit: Parser[Double] =
    elem("double number", _.isInstanceOf[DoubleNumLit]) ^^ (_.asInstanceOf[DoubleNumLit].value)

  /** A parser which matches a string literal */
  private def stringLit: Parser[String] =
    elem("string literal", _.isInstanceOf[StringLit]) ^^ (_.chars)

  private def interpolatedStringLit: Parser[InterpolatedStringLit] =
    elem("interpolated string literal", _.isInstanceOf[InterpolatedStringLit]) ^^ (_.asInstanceOf[InterpolatedStringLit])

  /** A parser which matches an identifier */
  private def identLit: Parser[String] =
    elem("identifier", _.isInstanceOf[Identifier]) ^^ (_.chars)

  private def variableLit: Parser[String] =
    elem("variable", _.isInstanceOf[Variable]) ^^ (_.chars)

  private def withPos[T <: Node](parser: Parser[T]) = Parser {
    in =>
      parser(in) match {
        case Success(n, in1) =>
          n.position = FileNodePosition(parseCtx.fileName, in.pos.line)
          Success(n, in1)
        case ns: NoSuccess => ns
      }
  }
}

object JbjParser {
  def apply(s: String): Prog = {
    val parser = new JbjParser(ParseContext("-"))
    parser.parse(s)
  }


  //Simplify testing
  def test(exprstr: String) = {
    val parser = new JbjParser(ParseContext("-"))
    val tree = parser.parse(exprstr)

    tree.dump(System.out, "")

    val context = GlobalContext(System.out, System.err)
    tree.exec(context)
  }

  //A main method for testing
  def main(args: Array[String]) = {
    test( """<?php
            |$valid_true = array(1, "1", "true", 1.0, array(1));
            |$valid_false = array(0, "", 0.0, array(), NULL);
            |
            |$int1 = 679;
            |$int2 = -67835;
            |$valid_int1 = array("678", "678abc", " 678", "678  ", 678.0, 6.789E2, "+678", +678);
            |$valid_int2 = array("-67836", "-67836abc", " -67836", "-67836  ", -67835.0001, -6.78351E4);
            |$invalid_int1 = array(679, "679");
            |$invalid_int2 = array(-67835, "-67835");
            |
            |$float1 = 57385.45835;
            |$float2 = -67345.76567;
            |$valid_float1 = array("57385.45834",  "57385.45834aaa", "  57385.45834", 5.738545834e4);
            |$valid_float2 = array("-67345.76568", "-67345.76568aaa", "  -67345.76568", -6.734576568E4);
            |$invalid_float1 = array(57385.45835, 5.738545835e4);
            |$invalid_float2 = array(-67345.76567, -6.734576567E4);
            |
            |
            |$toCompare = array(
            |// boolean test will result in both sides being converted to boolean so !0 = true and true is not > true for example
            |// also note that a string of "0" is converted to false but a string of "0.0" is converted to true
            |// false cannot be tested as 0 can never be > 0 or 1
            |  true, $valid_false, $valid_true,
            |  $int1, $valid_int1, $invalid_int1,
            |  $int2, $valid_int2, $invalid_int2,
            |  $float1, $valid_float1, $invalid_float1,
            |  $float2, $valid_float2, $invalid_float2
            |);
            |
            |$failed = false;
            |for ($i = 0; $i < count($toCompare); $i +=3) {
            |   $typeToTest = $toCompare[$i];
            |   $valid_compares = $toCompare[$i + 1];
            |   $invalid_compares = $toCompare[$i + 2];
            |
            |   foreach($valid_compares as $compareVal) {
            |      if ($typeToTest > $compareVal) {
            |         // do nothing
            |      }
            |      else {
            |         echo "FAILED: '$typeToTest' <= '$compareVal'\n";
            |         $failed = true;
            |      }
            |   }
            |
            |   foreach($invalid_compares as $compareVal) {
            |      if ($typeToTest > $compareVal) {
            |         echo "FAILED: '$typeToTest' > '$compareVal'\n";
            |         $failed = true;
            |      }
            |   }
            |
            |}
            |if ($failed == false) {
            |   echo "Test Passed\n";
            |}
            |?>""".stripMargin)
  }
}
