package de.leanovate.jbj.parser

import de.leanovate.jbj.ast._
import de.leanovate.jbj.ast.stmt.cond._
import de.leanovate.jbj.ast.stmt.loop._
import de.leanovate.jbj.ast.expr._
import de.leanovate.jbj.ast.expr.value._
import de.leanovate.jbj.ast.expr.calc._
import de.leanovate.jbj.ast.expr.comp._
import de.leanovate.jbj.ast.name.{StaticNamespaceName, StaticName, DynamicName}
import de.leanovate.jbj.parser.JbjTokens._
import de.leanovate.jbj.ast.stmt._
import de.leanovate.jbj.runtime.value._
import scala.collection.mutable
import scala.util.parsing.combinator.{PackratParsers, Parsers}
import scala.language.implicitConversions
import scala.Some
import de.leanovate.jbj.runtime.context.GlobalContext
import de.leanovate.jbj.runtime.env.CgiEnvironment
import scala.util.parsing.input.Reader
import de.leanovate.jbj.runtime.Settings
import de.leanovate.jbj.JbjEnv

class JbjParser(parseCtx: ParseContext) extends Parsers with PackratParsers {
  type Elem = JbjTokens.Token

  private val keywordCache = mutable.HashMap[String, Parser[String]]()

  def parse(s: String): Prog = {
    val tokens = new TokenReader(s, InitialLexer)
    phrase(start)(tokens) match {
      case Success(tree, _) => tree
      case e: NoSuccess =>
        throw new IllegalArgumentException("Bad syntax: " + e)
    }
  }

  def parseExpr(s: String): Expr = {
    val tokens = new TokenReader(s, ScriptLexer)
    phrase(expr)(tokens) match {
      case Success(result, _) => result
      case e: NoSuccess =>
        throw new IllegalArgumentException("Bad syntax: " + e)
    }
  }

  lazy val start: PackratParser[Prog] = topStatementList ^^ {
    stmts => Prog(stmts)
  }

  lazy val topStatementList: PackratParser[List[Stmt]] = rep(topStatement)

  lazy val namespaceName: PackratParser[NamespaceName] = rep1sep(identLit, "\\") ^^ {
    path => NamespaceName(path: _*)
  }

  lazy val topStatement: PackratParser[Stmt] = statement | functionDeclarationStatement |
    classDeclarationStatement | constantDeclaration

  lazy val constantDeclaration: PackratParser[Stmt] = "const" ~> rep1(identLit ~ "=" ~ staticScalar ^^ {
    case name ~ _ ~ s => StaticAssignment(name, Some(s))
  }) ^^ {
    assignments => ConstDeclStmt(assignments)
  }

  lazy val innerStatementList: PackratParser[List[Stmt]] = rep(";") ~> rep(innerStatement)

  lazy val innerStatement: PackratParser[Stmt] = statement

  lazy val statement: PackratParser[Stmt] = identLit <~ ":" ^^ {
    label => LabelStmt(label)
  } | untickedStatement <~ rep(";")

  lazy val untickedStatement: PackratParser[Stmt] =
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
      case e ~ cases => SwitchStmt(e, cases)
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
      e => ExprStmt(e)
    } | "foreach" ~> "(" ~> exprWithoutVariable ~ "as" ~ foreachVariable ~ foreachOptionalArg ~ ")" ~ foreachStatement ^^ {
      case array ~ _ ~ valueVar ~ None ~ _ ~ stmts =>
        ForeachValueStmt(array, valueVar, stmts)
      case array ~ _ ~ keyVar ~ Some(valueVar) ~ _ ~ stmts =>
        ForeachKeyValueStmt(array, keyVar, valueVar, stmts)
    } | "try" ~> "{" ~> innerStatementList ~ "}" ~ catchStatement ~ finallyStatement ^^ {
      case tryStmts ~ _ ~ catchBlocks ~ finallyStmts => TryCatchStmt(tryStmts, catchBlocks, finallyStmts)
    } | "throw" ~> expr ^^ {
      e => ThrowStmt(e)
    }

  lazy val catchStatement: PackratParser[List[CatchBlock]] = rep(
    "catch" ~> "(" ~> fullyQualifiedClassName ~ variableLit ~ ")" ~ "{" ~ innerStatementList <~ "}" ^^ {
      case exceptionName ~ v ~ _ ~ _ ~ stmts => CatchBlock(exceptionName, v, stmts)
    })

  lazy val finallyStatement: PackratParser[List[Stmt]] =
    opt("finally" ~> "{" ~> innerStatementList <~ "}") ^^ (_.getOrElse(Nil))

  lazy val functionDeclarationStatement: PackratParser[FunctionDeclStmt] = untickedFunctionDeclarationStatement <~ rep(";")

  lazy val classDeclarationStatement: PackratParser[ClassDeclStmt] = untickedClassDeclarationStatement <~ rep(";")

  lazy val untickedFunctionDeclarationStatement: PackratParser[FunctionDeclStmt] =
    "function" ~ opt("&") ~ identLit ~ "(" ~ parameterList ~ ")" ~ "{" ~ innerStatementList <~ "}" ^^ {
      case func ~ isRef ~ name ~ _ ~ params ~ _ ~ _ ~ body => FunctionDeclStmt(NamespaceName(name), params, body)
    }

  lazy val untickedClassDeclarationStatement: PackratParser[ClassDeclStmt] =
    classEntryType ~ identLit ~ extendsFrom ~ implementsList ~ "{" ~ classStatementList <~ "}" ^^ {
      case entry ~ name ~ exts ~ impls ~ _ ~ stmts =>
        ClassDeclStmt(entry, NamespaceName(name), exts, impls, stmts)
    }

  lazy val classEntryType: PackratParser[ClassEntry.Type] =
    "class" ^^^ ClassEntry.CLASS | "abstract" ~ "class" ^^^ ClassEntry.ABSTRACT_CLASS |
      "final" ~ "class" ^^^ ClassEntry.FINAL_CLASS | "trait" ^^^ ClassEntry.TRAIT

  lazy val extendsFrom: PackratParser[Option[NamespaceName]] = opt("extends" ~> fullyQualifiedClassName)

  lazy val implementsList: PackratParser[List[NamespaceName]] = opt("implements" ~> interfaceList) ^^ {
    optInterfaces => optInterfaces.getOrElse(Nil)
  }

  lazy val interfaceList: PackratParser[List[NamespaceName]] = rep1sep(fullyQualifiedClassName, ",")

  lazy val foreachOptionalArg: PackratParser[Option[Reference]] = opt("=>" ~> variable)

  lazy val foreachVariable: PackratParser[Reference] = variable

  lazy val forStatement: PackratParser[List[Stmt]] =
    ":" ~> innerStatementList <~ "endfor" <~ ";" | statement ^^ (List(_))

  lazy val foreachStatement: PackratParser[List[Stmt]] =
    ":" ~> innerStatementList <~ "endforeach" <~ ";" | statement ^^ (List(_))

  lazy val switchCaseList: PackratParser[List[SwitchCase]] =
    "{" ~> opt(";") ~> caseList <~ "}" |
      ":" ~> opt(";") ~> caseList <~ "endswitch" <~ ";"

  lazy val caseList: PackratParser[List[SwitchCase]] = rep(
    "case" ~> expr ~ caseSeparator ~ innerStatementList ^^ {
      case e ~ _ ~ stmts => CaseBlock(e, stmts)
    } | "default" ~> caseSeparator ~> innerStatementList ^^ {
      case stmts => DefaultCaseBlock(stmts)
    })

  lazy val caseSeparator: PackratParser[Any] = ":" | ";"

  lazy val whileStatement: PackratParser[List[Stmt]] = ":" ~> innerStatementList <~ "endwhile" <~ ";" |
    statement ^^ (List(_))

  lazy val elseIfList: PackratParser[List[ElseIfBlock]] = rep("elseif" ~> parenthesisExpr ~ statement ^^ {
    case cond ~ stmt => ElseIfBlock(cond, stmt :: Nil)
  })

  lazy val newElseIfList: PackratParser[List[ElseIfBlock]] = rep("elseif" ~> parenthesisExpr ~ ":" ~ innerStatementList ^^ {
    case cond ~ _ ~ stmts => ElseIfBlock(cond, stmts)
  })

  lazy val elseSingle: PackratParser[List[Stmt]] = opt("else" ~> statement) ^^ (_.toList)

  lazy val newElseSingle: PackratParser[List[Stmt]] = opt("else" ~> ":" ~> innerStatementList) ^^ (_.toList.flatten)

  lazy val parameterList: PackratParser[List[ParameterDecl]] = repsep(
    optionalClassType ~ opt("&") ~ variableLit ~ opt("=" ~> staticScalar) ^^ {
      case typeHint ~ optRef ~ v ~ optDefault =>
        ParameterDecl(typeHint, v, optRef.isDefined, optDefault)
    }, ",")

  lazy val optionalClassType: PackratParser[Option[TypeHint]] = opt("array" ^^^ ArrayTypeHint |
    "callable" ^^^ CallableTypeHint | fullyQualifiedClassName ^^ (name => ClassTypeHint(name)))

  lazy val functionCallParameterList: PackratParser[List[Expr]] = "(" ~> repsep(exprWithoutVariable | variable, ",") <~ ")"

  lazy val globalVarList: PackratParser[List[String]] = rep1sep(variableLit, ",")

  lazy val staticVarList: PackratParser[List[StaticAssignment]] = rep1sep(variableLit ~ opt("=" ~> staticScalar) ^^ {
    case v ~ optScalar => StaticAssignment(v, optScalar)
  }, ",")

  lazy val classStatementList: PackratParser[List[Stmt]] = rep(classStatement)

  lazy val classStatement: PackratParser[Stmt] = variableModifiers ~ classVariableDeclaration <~ ";" ^^ {
    case modifiers ~ assignments => ClassVarDeclStmt(modifiers, assignments)
  } | classConstantDeclaration ^^ {
    assignments => ClassConstDeclStmt(assignments)
  } | traitUseStatement |
    methodModifiers ~ "function" ~ opt("&") ~ identLit ~ "(" ~ parameterList ~ ")" ~ methodBody ^^ {
      case modifiers ~ _ ~ optRef ~ name ~ _ ~ params ~ _ ~ stmts => ClassMethodDeclStmt(modifiers, name, params, stmts)
    }

  lazy val traitUseStatement: PackratParser[TraitUseStmt] = "use" ~> traitList <~ ";" ^^ {
    traits => TraitUseStmt(traits)
  }

  lazy val traitList: PackratParser[List[NamespaceName]] = rep1(fullyQualifiedClassName)

  lazy val methodBody: PackratParser[List[Stmt]] = ";" ^^^ Nil | "{" ~> innerStatementList <~ "}"

  lazy val variableModifiers: PackratParser[Set[MemberModifier.Type]] = nonEmptyMemberModifiers |
    "var" ^^^ Set.empty[MemberModifier.Type]

  lazy val methodModifiers: PackratParser[Set[MemberModifier.Type]] = opt(nonEmptyMemberModifiers) ^^ {
    optModifiers => optModifiers.getOrElse(Set.empty[MemberModifier.Type])
  }

  lazy val nonEmptyMemberModifiers: PackratParser[Set[MemberModifier.Type]] =
    rep1(memberModifier) ^^ (_.toSet)

  lazy val memberModifier: PackratParser[MemberModifier.Type] =
    "public" ^^^ MemberModifier.PUBLIC | "protected" ^^^ MemberModifier.PROTECTED |
      "private" ^^^ MemberModifier.PRIVATE | "static" ^^^ MemberModifier.STATIC |
      "final" ^^^ MemberModifier.FINAL | "abstract" ^^^ MemberModifier.ABSTRACT

  lazy val classVariableDeclaration: PackratParser[List[StaticAssignment]] =
    rep1sep(variableLit ~ opt("=" ~> staticScalar) ^^ {
      case v ~ optScalar =>
        StaticAssignment(v, optScalar)
    }, ",")

  lazy val classConstantDeclaration: PackratParser[List[StaticAssignment]] =
    "const" ~> rep1sep(identLit ~ "=" ~ staticScalar ^^ {
      case name ~ _ ~ s => StaticAssignment(name, Some(s))
    }, ",")

  lazy val echoExprList: PackratParser[List[Expr]] = rep1sep(expr, ",")

  lazy val forExpr: PackratParser[List[Expr]] = repsep(expr, ",")

  lazy val newExpr: PackratParser[NewExpr] = "new" ~> classNameReference ~ ctorArguments ^^ {
    case name ~ args => NewExpr(name, args)
  }

  lazy val exprWithoutVariable: PackratParser[Expr] =
    variable ~ "=" ~ expr ^^ {
      case v ~ _ ~ e => AssignExpr(v, e)
    } | variable ~ "+=" ~ expr ^^ {
      case v ~ _ ~ e => AddToExpr(v, e)
    } | variable ~ "-=" ~ expr ^^ {
      case v ~ _ ~ e => SubFromExpr(v, e)
    } | variable ~ "*=" ~ expr ^^ {
      case v ~ _ ~ e => MulByExpr(v, e)
    } | variable ~ "/=" ~ expr ^^ {
      case v ~ _ ~ e => DivByExpr(v, e)
    } | variable ~ ".=" ~ expr ^^ {
      case v ~ _ ~ e => ConcatWithExpr(v, e)
    } | binary(minPrec) | termWithoutVariable

  def binaryOp(level: Int): Parser[((Expr, Expr) => Expr)] = {
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
          "/" ^^^ ((a: Expr, b: Expr) => DivExpr(a, b)) |
          "%" ^^^ ((a: Expr, b: Expr) => ModExpr(a, b))
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

  lazy val termWithoutVariable: PackratParser[Expr] =
    expr ~ "?" ~ expr ~ ":" ~ expr ^^ {
      case cond ~ _ ~ tExpr ~ _ ~ fExpr => TernaryExpr(cond, tExpr, fExpr)
    } | "!" ~> term ^^ {
      e => BoolNotExpr(e)
    } | newExpr | internalFunctionsInYacc |
      rwVariable <~ "++" ^^ {
        ref => GetAndIncrExpr(ref)
      } | rwVariable <~ "--" ^^ {
      ref => GetAndDecrExpr(ref)
    } | "++" ~> rwVariable ^^ {
      ref => IncrAndGetExpr(ref)
    } | "--" ~> rwVariable ^^ {
      ref => DecrAndGetExpr(ref)
    } | scalar | combinedScalarOffset |
      "print" ~> expr ^^ {
        e => PrintExpr(e)
      } | constant | parenthesisExpr | "-" ~> term ^^ {
      expr => NegExpr(expr)
    } | "+" ~> term

  lazy val combinedScalarOffset: PackratParser[Expr] = combinedScalar ~ rep("[" ~> dimOffset <~ "]") ^^ {
    case s ~ dims => dims.foldLeft(s) {
      (e, dim) => IndexGetExpr(e, dim)
    }
  }

  lazy val combinedScalar: PackratParser[Expr] = "array" ~> "(" ~> arrayPairList <~ ")" ^^ {
    keyValues => ArrayCreateExpr(keyValues)
  } | "[" ~> arrayPairList <~ "]" ^^ {
    keyValues => ArrayCreateExpr(keyValues)
  }

  lazy val functionCall: PackratParser[Reference] = namespaceName ~ functionCallParameterList ^^ {
    case name ~ params => CallFunctionReference(StaticNamespaceName(name), params)
  }

  lazy val className: PackratParser[NamespaceName] = namespaceName

  lazy val fullyQualifiedClassName: PackratParser[NamespaceName] = namespaceName

  lazy val classNameReference: PackratParser[NamespaceName] = className

  lazy val ctorArguments: PackratParser[List[Expr]] = opt(functionCallParameterList) ^^ (_.getOrElse(Nil))

  lazy val commonScalar: PackratParser[Expr] =
    longNumLit ^^ {
      s => ScalarExpr(IntegerVal(s))
    } | doubleNumLit ^^ {
      s => ScalarExpr(DoubleVal(s))
    } | stringLit ^^ {
      s => ScalarExpr(StringVal(s))
    } | "__FILE__" ^^^ FileNameConstExpr() |
      "__LINE__" ^^^ LineNumberConstExpr() |
      "__CLASS__" ^^^ ClassNameConstExpr() |
      "__METHOD__" ^^^ MethodNameConstExpr() |
      hereDocStartLit ~> opt(encapsAndWhitespaceLit) <~ hereDocEndLit ^^ {
        s => ScalarExpr(StringVal(s.getOrElse("")))
      }

  lazy val staticScalar: PackratParser[Expr] =
    commonScalar | "+" ~> staticScalar ^^ {
      s => PosExpr(s)
    } | "-" ~> staticScalar ^^ {
      s => NegExpr(s)
    } | "array" ~> "(" ~> staticArrayPairList <~ ")" ^^ {
      pairs => ArrayCreateExpr(pairs)
    } | "[" ~> staticArrayPairList <~ "]" ^^ {
      pairs => ArrayCreateExpr(pairs)
    } | staticClassConstant

  lazy val staticClassConstant: PackratParser[Expr] = className ~ "::" ~ identLit ^^ {
    case cname ~ _ ~ name => ClassConstantExpr(cname, name)
  }

  lazy val scalar: PackratParser[Expr] = commonScalar | "\"" ~> encapsList <~ "\"" ^^ {
    interpolated => InterpolatedStringExpr(interpolated)
  } | hereDocStartLit ~> encapsList <~ hereDocEndLit ^^ {
    interpolated => InterpolatedStringExpr(interpolated)
  }

  lazy val staticArrayPairList: PackratParser[List[(Option[Expr], Expr)]] = repsep(
    staticScalar ~ opt("=>" ~> staticScalar) ^^ {
      case keyExpr ~ Some(valueExpr) => Some(keyExpr) -> valueExpr
      case valueExpr ~ None => None -> valueExpr
    }, ",") <~ opt(",")

  lazy val expr: PackratParser[Expr] = exprWithoutVariable ||| rVariable

  lazy val term: PackratParser[Expr] = termWithoutVariable ||| rVariable

  lazy val parenthesisExpr: PackratParser[Expr] = "(" ~> expr <~ ")"

  lazy val rVariable: PackratParser[Reference] = variable

  lazy val rwVariable: PackratParser[Reference] = variable

  lazy val variable: PackratParser[Reference] =
    baseVariableWithFunctionCalls ~ rep("->" ~> objectProperty) ~ methodOrNot ~ variableProperties ^^ {
      case v ~ refMaps ~ optMethod ~ varProps =>
        val ref = refMaps.flatten.foldLeft(v) {
          (v, refMap) => refMap(v)
        }
        val methodRef = optMethod.map {
          params =>
            ref match {
              case PropertyReference(instance, name) => CallMethodReference(instance, name, params)
              case nameExpr => CallFunctionReference(DynamicName(nameExpr), params)
            }
        }.getOrElse(ref)
        varProps.foldLeft(methodRef) {
          (v, refMap) => refMap(v)
        }
    }

  lazy val variableProperties: Parser[List[Reference => Reference]] = rep(variableProperty)

  lazy val variableProperty: Parser[Reference => Reference] = "->" ~> objectProperty ~ methodOrNot ^^ {
    case refMaps ~ optMethod =>
      v: Reference =>
        val ref = refMaps.foldLeft(v) {
          (v, refMap) => refMap(v)
        }
        optMethod.map {
          params =>
            ref match {
              case PropertyReference(instance, name) => CallMethodReference(instance, name, params)
              case nameExpr => CallFunctionReference(DynamicName(nameExpr), params)
            }
        }.getOrElse(ref)
  }

  lazy val mathod: PackratParser[List[Expr]] = functionCallParameterList

  lazy val methodOrNot: PackratParser[Option[List[Expr]]] = opt(mathod)

  lazy val variableWithoutObjects: PackratParser[Expr] = referenceVariable

  lazy val baseVariableWithFunctionCalls: PackratParser[Reference] = baseVariable | functionCall

  lazy val baseVariable: PackratParser[Reference] = referenceVariable

  lazy val referenceVariable: PackratParser[Reference] = compoundVariable ~ rep(
    "[" ~> dimOffset <~ "]" | "{" ~> expr <~ "}" ^^ Some.apply
  ) ^^ {
    case v ~ dims => dims.foldLeft(v.asInstanceOf[Reference]) {
      (ref, dim) => IndexReference(ref, dim)
    }
  }

  lazy val compoundVariable: PackratParser[Reference] = variableLit ^^ {
    v => VariableReference(StaticName(v))
  } | "$" ~> "{" ~> expr <~ "}" ^^ {
    expr => VariableReference(DynamicName(expr))
  }

  lazy val dimOffset: PackratParser[Option[Expr]] = opt(expr)

  lazy val objectProperty: PackratParser[List[Reference => Reference]] = objectDimList | variableWithoutObjects ^^ {
    v => {
      ref: Reference => PropertyReference(ref, DynamicName(v))
    } :: Nil
  }

  lazy val objectDimList: PackratParser[List[Reference => Reference]] =
    variableName ~ rep(
      "[" ~> dimOffset <~ "]" | "{" ~> expr <~ "}" ^^ Some.apply
    ) ^^ {
      case name ~ dims => {
        ref: Reference => PropertyReference(ref, name)
      } :: dims.map(d => {
        ref: Reference => IndexReference(ref, d)
      })
    }

  lazy val variableName: PackratParser[Name] = identLit ^^ StaticName.apply | "{" ~> expr <~ "}" ^^ DynamicName.apply

  lazy val simpleIndirectReference = rep1("$")


  lazy val arrayPairList: PackratParser[List[(Option[Expr], Expr)]] = repsep(
    expr ~ opt("=>" ~> expr) ^^ {
      case keyExpr ~ Some(valueExpr) => Some(keyExpr) -> valueExpr
      case valueExpr ~ None => None -> valueExpr
    }, ",") <~ opt(",")

  lazy val encapsList: PackratParser[List[Either[String, Expr]]] =
    rep(encapsVar ^^ Right.apply | encapsAndWhitespaceLit ^^ Left.apply)

  lazy val encapsVar: PackratParser[Expr] =
    variableLit ~ "[" ~ encapsVarOffset <~ "]" ^^ {
      case v ~ _ ~ idx =>
        val ref = VariableReference(StaticName(v))
        IndexReference(ref, Some(idx))
    } | variableLit ~ "->" ~ identLit ^^ {
      case v ~ _ ~ n =>
        val ref = VariableReference(StaticName(v))
        PropertyReference(ref, StaticName(n))
    } | variableLit ^^ {
      v => VariableReference(StaticName(v))
    } | "${" ~> expr <~ "}" |
      "${" ~> identLit ~ "[" ~ expr <~ "]" ^^ {
        case v ~ _ ~ e =>
          val ref = VariableReference(StaticName(v))
          IndexReference(ref, Some(e))
      } | "{$" ~> variable <~ "}"

  lazy val encapsVarOffset: PackratParser[Expr] = identLit ^^ {
    idx => ScalarExpr(StringVal(idx))
  } | longNumLit ^^ {
    idx => ScalarExpr(IntegerVal(idx))
  } | variable

  lazy val internalFunctionsInYacc: PackratParser[Expr] = "isset" ~> "(" ~> issetVariables <~ ")" ^^ {
    exprs => IsSetExpr(exprs)
  }

  lazy val issetVariables: PackratParser[List[Expr]] = rep1sep(issetVariable, ",")

  lazy val issetVariable: PackratParser[Expr] = exprWithoutVariable ||| rVariable

  lazy val constant: PackratParser[Expr] = identLit ^^ {
    name => ConstGetExpr(name)
  }


  lazy val inlineHtml: PackratParser[InlineStmt] =
    elem("inline", _.isInstanceOf[Inline]) ^^ {
      t => InlineStmt(t.chars)
    }

  /** A parser which matches a single keyword token.
    *
    * @param chars    The character string making up the matched keyword.
    * @return a `Parser` that matches the given string
    */
  //  implicit def keyword(chars: String): Parser[String] = accept(Keyword(chars)) ^^ (_.chars)
  implicit def keyword(chars: String): PackratParser[String] =
    keywordCache.getOrElseUpdate(chars, accept(Keyword(chars)) ^^ (_.chars))

  /** A parser which matches a numeric literal */
  lazy val longNumLit: PackratParser[Long] =
    elem("long number", _.isInstanceOf[LongNumLit]) ^^ (_.asInstanceOf[LongNumLit].value)

  /** A parser which matches a numeric literal */
  lazy val doubleNumLit: PackratParser[Double] =
    elem("double number", _.isInstanceOf[DoubleNumLit]) ^^ (_.asInstanceOf[DoubleNumLit].value)

  /** A parser which matches a string literal */
  lazy val stringLit: PackratParser[String] =
    elem("string literal", _.isInstanceOf[StringLit]) ^^ (_.chars)

  /** A parser which matches an identifier */
  lazy val identLit: PackratParser[String] =
    elem("identifier", _.isInstanceOf[Identifier]) ^^ (_.chars)

  lazy val variableLit: PackratParser[String] =
    elem("variable", _.isInstanceOf[Variable]) ^^ (_.asInstanceOf[Variable].name)

  lazy val encapsAndWhitespaceLit: PackratParser[String] =
    elem("encapsAntWhitespace", _.isInstanceOf[EncapsAndWhitespace]) ^^ (_.chars)

  lazy val hereDocStartLit: PackratParser[String] =
    elem("heredocstart", _.isInstanceOf[HereDocStart]) ^^ (_.chars)

  lazy val hereDocEndLit: PackratParser[String] =
    elem("heredocstart", _.isInstanceOf[HereDocEnd]) ^^ (_.chars)

  implicit def parser2packrat1[T <: Node](p: => super.Parser[T]): PackratParser[T] = {
    lazy val q = p
    memo(super.Parser {
      in => q(in) match {
        case Success(n, in1) =>
          n.position = FileNodePosition(parseCtx.fileName, in.pos.line)
          Success(n, in1)
        case ns: NoSuccess => ns
      }
    })
  }
}

object JbjParser {
  def apply(fileName: String, s: String): Prog = {
    val parser = new JbjParser(ParseContext(fileName))
    parser.parse(s)
  }

  //Simplify testing
  def test(exprstr: String) = {
    var tokens: Reader[Token] = new TokenReader(exprstr, InitialLexer)

    println("Tokens")
    var count = 0
    while (!tokens.atEnd && count < 1000) {
      println(tokens.first)
      tokens = tokens.rest
      count += 1
    }

    val tree = apply("-", exprstr)

    println("Tree")
    tree.dump(System.out, "")

    val jbj = JbjEnv()
    val context = jbj.newGlobalContext(System.out, System.err)

    context.settings.errorReporting = Settings.E_ALL
    CgiEnvironment.httpGet("?ab+cd+ef+123+test", context)
    tree.exec(context)
  }

  //A main method for testing
  def main(args: Array[String]) = {
    test( """<?php
            |  class C
            |  {
            |      function foo($a, $b)
            |      {
            |          echo "Called C::foo($a, $b)\n";
            |      }
            |  }
            |
            |  $c = new C;
            |
            |  $functions[0] = 'foo';
            |  $functions[1][2][3][4] = 'foo';
            |
            |  $c->$functions[0](1, 2);
            |  $c->$functions[1][2][3][4](3, 4);
            |
            |
            |  function foo($a, $b)
            |  {
            |      echo "Called global foo($a, $b)\n";
            |  }
            |
            |  $c->functions[0] = 'foo';
            |  $c->functions[1][2][3][4] = 'foo';
            |
            |  $c->functions[0](5, 6);
            |  $c->functions[1][2][3][4](7, 8);
            |?>""".stripMargin)
  }
}
