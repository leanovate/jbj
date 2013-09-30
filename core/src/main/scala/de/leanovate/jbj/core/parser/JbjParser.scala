/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.core.parser

import de.leanovate.jbj.core.ast._
import de.leanovate.jbj.core.ast.decl._
import de.leanovate.jbj.core.ast.expr._
import de.leanovate.jbj.core.ast.expr.cast._
import de.leanovate.jbj.core.ast.expr.comp._
import de.leanovate.jbj.core.ast.expr.calc._
import de.leanovate.jbj.core.ast.expr.include._
import de.leanovate.jbj.core.ast.expr.value._
import de.leanovate.jbj.core.ast.name._
import de.leanovate.jbj.core.ast.stmt._
import de.leanovate.jbj.core.ast.stmt.cond._
import de.leanovate.jbj.core.ast.stmt.loop._
import de.leanovate.jbj.core.parser.JbjTokens._
import de.leanovate.jbj.runtime.value._
import scala.collection.mutable
import scala.util.parsing.combinator.{PackratParsers, Parsers}
import scala.language.implicitConversions
import de.leanovate.jbj.runtime.exception.ParseJbjException
import de.leanovate.jbj.runtime.value.StringVal
import de.leanovate.jbj.core.parser.JbjTokens.ArrayCast
import de.leanovate.jbj.core.parser.JbjTokens.DoubleCast
import de.leanovate.jbj.runtime.{FileNodePosition, NamespaceName}
import de.leanovate.jbj.runtime.types.{ClassTypeHint, CallableTypeHint, ArrayTypeHint, TypeHint}
import de.leanovate.jbj.api.http.JbjSettings

class JbjParser(parseCtx: ParseContext) extends Parsers with PackratParsers {
  type Elem = JbjTokens.Token

  private val keywordCache = mutable.HashMap[String, Parser[String]]()

  def parse(s: String): Prog = {
    val tokens = new TokenReader(s, InitialLexerMode(parseCtx.settings.isShortOpenTag, parseCtx.settings.isAspTags).newLexer())
    phrase(start)(tokens) match {
      case Success(tree, _) => tree
      case e: NoSuccess =>
        throw new ParseJbjException(e.msg, FileNodePosition(parseCtx.fileName, e.next.pos.line))
    }
  }

  def parseStmt(s: String): Prog = {
    val tokens = new TokenReader(s, ScriptingLexerMode(InitialLexerMode(parseCtx.settings.isShortOpenTag, parseCtx.settings.isAspTags)).newLexer())
    phrase(start)(tokens) match {
      case Success(result, _) => result
      case e: NoSuccess =>
        throw new ParseJbjException(e.msg, FileNodePosition(parseCtx.fileName, e.next.pos.line))
    }
  }

  lazy val start: PackratParser[Prog] = topStatementList ^^ {
    stmts => Prog(parseCtx.fileName, stmts)
  }

  lazy val topStatementList: PackratParser[List[Stmt]] = rep(topStatement)

  lazy val namespaceName: PackratParser[NamespaceName] = rep1sep(identLit, "\\") ^^ {
    path => NamespaceName(relative = true, prefixed = false, path: _*)
  }

  lazy val topStatement: PackratParser[Stmt] =
    statement | functionDeclarationStatement | classDeclarationStatement |
      "namespace" ~> namespaceName <~ ";" ^^ {
        name => SetNamespaceDeclStmt(name)
      } | "namespace" ~> namespaceName ~ "{" ~ topStatementList <~ "}" ^^ {
      case name ~ _ ~ stmts => NamespaceDeclStmt(name, stmts)
    } | "namespace" ~> "{" ~> topStatementList <~ "}" ^^ {
      stmts => NamespaceDeclStmt(NamespaceName(relative = false, prefixed = false), stmts)
    } | "use" ~> rep(useDeclaration) <~ ";" ^^ {
      useAsDecls => UseDeclStmt(useAsDecls)
    } | constantDeclaration <~ ";"

  lazy val useDeclaration: PackratParser[UseAsDecl] =
    namespaceName ~ opt("as" ~> identLit) ^^ {
      case name ~ alias => UseAsDecl(name, alias)
    } | "\\" ~> namespaceName ~ opt("as" ~> identLit) ^^ {
      case name ~ alias => UseAsDecl(NamespaceName(relative = false, prefixed = false, name.path: _*), alias)
    }

  lazy val constantDeclaration: PackratParser[Stmt] = "const" ~> rep1(identLit ~ "=" ~ staticScalar ^^ {
    case name ~ _ ~ s => StaticAssignment(name, Some(s))
  }) ^^ {
    assignments => ConstDeclStmt(assignments)
  }

  lazy val innerStatementList: PackratParser[List[Stmt]] = rep(";") ~> rep(innerStatement)

  lazy val innerStatement: PackratParser[Stmt] = statement | functionDeclarationStatement | classDeclarationStatement

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
    } | "unset" ~> "(" ~> unsetVariables <~ ")" ^^ {
      case vars => UnsetStmt(vars)
    } | "foreach" ~> "(" ~> expr ~ "as" ~ foreachVariable ~ foreachOptionalArg ~ ")" ~ foreachStatement ^^ {
      case array ~ _ ~ valueAssign ~ None ~ _ ~ stmts =>
        ForeachStmt(array, None, valueAssign, stmts)
      case array ~ _ ~ keyAssign ~ Some(valueAssign) ~ _ ~ stmts =>
        ForeachStmt(array, Some(keyAssign), valueAssign, stmts)
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

  lazy val unsetVariables: PackratParser[List[RefExpr]] = rep1sep(variable, ",")

  lazy val functionDeclarationStatement: PackratParser[FunctionDeclStmt] = untickedFunctionDeclarationStatement <~ rep(";")

  lazy val classDeclarationStatement: PackratParser[Stmt] = untickedClassDeclarationStatement <~ rep(";")

  lazy val untickedFunctionDeclarationStatement: PackratParser[FunctionDeclStmt] =
    "function" ~ opt("&") ~ identLit ~ "(" ~ parameterList ~ ")" ~ "{" ~ innerStatementList <~ "}" ^^ {
      case func ~ isRef ~ name ~ _ ~ params ~ _ ~ _ ~ body =>
        FunctionDeclStmt(NamespaceName(relative = true, prefixed = false, name), isRef.isDefined, params, body)
    }

  lazy val untickedClassDeclarationStatement: PackratParser[Stmt] =
    classEntryType ~ identLit ~ extendsFrom ~ implementsList ~ "{" ~ classStatementList <~ "}" ^^ {
      case entry ~ name ~ exts ~ impls ~ _ ~ decls =>
        ClassDeclStmt(entry, NamespaceName(relative = true, prefixed = false, name), exts, impls, decls)
    } | "interface" ~> identLit ~ interfaceExtendsList ~ "{" ~ classStatementList <~ "}" ^^ {
      case name ~ exts ~ _ ~ decls =>
        InterfaceDeclStmt(NamespaceName(relative = true, prefixed = false, name), exts, decls)
    }

  lazy val classEntryType: PackratParser[ClassEntry.Type] =
    "class" ^^^ ClassEntry.CLASS | "abstract" ~ "class" ^^^ ClassEntry.ABSTRACT_CLASS |
      "final" ~ "class" ^^^ ClassEntry.FINAL_CLASS | "trait" ^^^ ClassEntry.TRAIT

  lazy val extendsFrom: PackratParser[Option[NamespaceName]] = opt("extends" ~> fullyQualifiedClassName)

  lazy val interfaceExtendsList: PackratParser[List[NamespaceName]] = opt("extends" ~> interfaceList) ^^ {
    optInterfaces => optInterfaces.getOrElse(Nil)
  }

  lazy val implementsList: PackratParser[List[NamespaceName]] = opt("implements" ~> interfaceList) ^^ {
    optInterfaces => optInterfaces.getOrElse(Nil)
  }

  lazy val interfaceList: PackratParser[List[NamespaceName]] = rep1sep(fullyQualifiedClassName, ",")

  lazy val foreachOptionalArg: PackratParser[Option[ForeachAssignment]] = opt("=>" ~> foreachVariable)

  lazy val foreachVariable: PackratParser[ForeachAssignment] =
    variable ^^ {
      ref => ValueForeachAssignment(ref)
    } | "&" ~> variable ^^ {
      ref => RefForeachAssignment(ref)
    } | "list" ~> "(" ~> assignmentList <~ ")" ^^ {
      refs => ListForeachAssignment(ListRefExpr(refs))
    }

  lazy val forStatement: PackratParser[List[Stmt]] =
    ":" ~> innerStatementList <~ "endfor" <~ ";" | statement ^^ (List(_)) | ";" ^^^ Nil

  lazy val foreachStatement: PackratParser[List[Stmt]] =
    ":" ~> innerStatementList <~ "endforeach" <~ ";" | statement ^^ (List(_)) | ";" ^^^ Nil

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
    statement ^^ (List(_)) | ";" ^^^ Nil

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

  lazy val functionCallParameterList: PackratParser[List[Expr]] = "(" ~> repsep(expr, ",") <~ ")"

  lazy val globalVar: PackratParser[Name] = variableLit ^^ {
    v => StaticName(v)
  } | "$" ~> variable ^^ {
    e => DynamicName(e)
  } | "$" ~> "{" ~> expr <~ "}" ^^ {
    e => DynamicName(e)
  }

  lazy val globalVarList: PackratParser[List[Name]] = rep1sep(globalVar, ",")

  lazy val staticVarList: PackratParser[List[StaticAssignment]] = rep1sep(variableLit ~ opt("=" ~> staticScalar) ^^ {
    case v ~ optScalar => StaticAssignment(v, optScalar)
  }, ",")

  lazy val classStatementList: PackratParser[List[ClassMemberDecl]] = rep(classStatement <~ rep(";"))

  lazy val classStatement: PackratParser[ClassMemberDecl] = variableModifiers ~ classVariableDeclaration <~ ";" ^^ {
    case modifiers ~ assignments => ClassVarDecl(modifiers, assignments)
  } | classConstantDeclaration <~ ";" ^^ {
    assignments => ClassConstDecl(assignments)
  } | traitUseStatement |
    methodModifiers ~ "function" ~ opt("&") ~ identLit ~ "(" ~ parameterList ~ ")" ~ methodBody ^^ {
      case modifiers ~ _ ~ isRef ~ name ~ _ ~ params ~ _ ~ stmts =>
        ClassMethodDecl(modifiers, name, isRef.isDefined, params, stmts)
    }

  lazy val traitUseStatement: PackratParser[TraitUseDecl] = "use" ~> traitList <~ ";" ^^ {
    traits => TraitUseDecl(traits)
  }

  lazy val traitList: PackratParser[List[NamespaceName]] = rep1(fullyQualifiedClassName)

  lazy val methodBody: PackratParser[Option[List[Stmt]]] = ";" ^^^ None |
    "{" ~> innerStatementList <~ "}" ^^ Some.apply

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

  lazy val newExpr: PackratParser[NewRefExpr] = "new" ~> classNameReference ~ ctorArguments ^^ {
    case name ~ args => NewRefExpr(name, args)
  }

  lazy val expr: PackratParser[Expr] =
    expr ~ "?" ~ expr ~ ":" ~ expr ^^ {
      case cond ~ _ ~ tExpr ~ _ ~ fExpr => TernaryExpr(cond, tExpr, fExpr)
    } | "list" ~> "(" ~> assignmentList ~ ")" ~ "=" ~ expr ^^ {
      case refs ~ _ ~ _ ~ e => AssignRefExpr(ListRefExpr(refs), e)
    } | variable ~ "=" ~ expr ^^ {
      case v ~ _ ~ e => AssignRefExpr(v, e)
    } | variable ~ "=" ~ "&" ~ variable ^^ {
      case v ~ _ ~ _ ~ ref => RefAssignRefExpr(v, ref)
    } | variable ~ "=" ~ "&" ~ "new" ~ classNameReference ~ ctorArguments ^^ {
      case v ~ _ ~ _ ~ _ ~ name ~ args =>
        val result = RefAssignRefExpr(v, NewRefExpr(name, args))
        result.deprecated = Some("Assigning the return value of new by reference is deprecated")
        result
    } | "clone" ~> expr ^^ {
      e => CloneExpr(e)
    } | variable ~ "+=" ~ expr ^^ {
      case v ~ _ ~ e => AddToRefExpr(v, e)
    } | variable ~ "-=" ~ expr ^^ {
      case v ~ _ ~ e => SubFromRefExpr(v, e)
    } | variable ~ "*=" ~ expr ^^ {
      case v ~ _ ~ e => MulByRefExpr(v, e)
    } | variable ~ "/=" ~ expr ^^ {
      case v ~ _ ~ e => DivByRefExpr(v, e)
    } | variable ~ ".=" ~ expr ^^ {
      case v ~ _ ~ e => ConcatWithRefExpr(v, e)
    } | binary(minPrec) | term

  def binaryOp(level: Int): PackratParser[((Expr, Expr) => Expr)] = {
    level match {
      case 1 => "or" ^^^ ((a: Expr, b: Expr) => BoolOrExpr(a, b))
      case 2 => "xor" ^^^ ((a: Expr, b: Expr) => BoolXorExpr(a, b))
      case 3 => "and" ^^^ ((a: Expr, b: Expr) => BoolAndExpr(a, b))
      case 4 => "||" ^^^ ((a: Expr, b: Expr) => BoolOrExpr(a, b))
      case 5 => "&&" ^^^ ((a: Expr, b: Expr) => BoolAndExpr(a, b))
      case 6 => "|" ^^^ ((a: Expr, b: Expr) => BitOrExpr(a, b))
      case 7 => "^" ^^^ ((a: Expr, b: Expr) => BitXorExpr(a, b))
      case 8 => "&" ^^^ ((a: Expr, b: Expr) => BitAndExpr(a, b))
      case 9 =>
        "==" ^^^ ((a: Expr, b: Expr) => EqExpr(a, b)) |
          "!=" ^^^ ((a: Expr, b: Expr) => NotEqExpr(a, b)) |
          "<>" ^^^ ((a: Expr, b: Expr) => NotEqExpr(a, b)) |
          "===" ^^^ ((a: Expr, b: Expr) => IdenticalExpr(a, b)) |
          "!==" ^^^ ((a: Expr, b: Expr) => NotIdenticalExpr(a, b))
      case 10 =>
        ">" ^^^ ((a: Expr, b: Expr) => GtExpr(a, b)) |
          ">=" ^^^ ((a: Expr, b: Expr) => GeExpr(a, b)) |
          "<" ^^^ ((a: Expr, b: Expr) => LtExpr(a, b)) |
          "<=" ^^^ ((a: Expr, b: Expr) => LeExpr(a, b))
      case 11 =>
        ">>" ^^^ ((a: Expr, b: Expr) => BitShiftRightExpr(a, b)) |
          "<<" ^^^ ((a: Expr, b: Expr) => BitShiftLeftExpr(a, b))
      case 12 =>
        "." ^^^ ((a: Expr, b: Expr) => ConcatExpr(a, b)) |
          "+" ^^^ ((a: Expr, b: Expr) => AddExpr(a, b)) |
          "-" ^^^ ((a: Expr, b: Expr) => SubExpr(a, b))
      case 13 =>
        "*" ^^^ ((a: Expr, b: Expr) => MulExpr(a, b)) |
          "/" ^^^ ((a: Expr, b: Expr) => DivExpr(a, b)) |
          "%" ^^^ ((a: Expr, b: Expr) => ModExpr(a, b))
      case _ => throw new RuntimeException("bad precedence level " + level)
    }
  }

  val minPrec = 1

  val maxPrec = 13

  def binary(level: Int): PackratParser[Expr] =
    if (level > maxPrec) {
      term
    }
    else {
      binary(level + 1) * binaryOp(level)
    }

  lazy val term: PackratParser[Expr] =
    "+" ~> term | "-" ~> term ^^ {
      expr => NegExpr(expr)
    } | "!" ~> term ^^ {
      e => BoolNotExpr(e)
    } | "~" ~> term ^^ {
      e => BitNotExpr(e)
    } | expr ~ "instanceof" ~ classNameReference ^^ {
      case e ~ _ ~ cname => InstanceOfExpr(e, cname)
    } | parenthesisExpr | newExpr | internalFunctionsInYacc | integerCastLit ~> term ^^ {
      e => IntegerCastExpr(e)
    } | doubleCastLit ~> term ^^ {
      e => DoubleCastExpr(e)
    } | stringCastLit ~> term ^^ {
      e => StringCastExpr(e)
    } | arrayCastLit ~> term ^^ {
      e => ArrayCastExpr(e)
    } | booleanCastLit ~> term ^^ {
      e => BooleanCastExpr(e)
    } | "exit" ~> expr ^^ {
      e => ExitExpr(e)
    } | "@" ~> expr ^^ {
      e => SilentExpr(e)
    } | variable <~ "++" ^^ {
      ref => GetAndIncrExpr(ref)
    } | variable <~ "--" ^^ {
      ref => GetAndDecrExpr(ref)
    } | "++" ~> variable ^^ {
      ref => IncrAndGetExpr(ref)
    } | "--" ~> variable ^^ {
      ref => DecrAndGetExpr(ref)
    } | variable ||| scalar | combinedScalarOffset |
      "print" ~> expr ^^ {
        e => PrintExpr(e)
      } | parenthesisExpr

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

  lazy val functionCall: PackratParser[RefExpr] = namespaceName ~ functionCallParameterList ^^ {
    case name ~ params => CallFunctionRefExpr(StaticNamespaceName(name), params)
  } | "namespace" ~> "\\" ~> namespaceName ~ functionCallParameterList ^^ {
    case name ~ params => CallFunctionRefExpr(StaticNamespaceName(NamespaceName(relative = false, prefixed = true, name.path: _*)), params)
  } | "\\" ~> namespaceName ~ functionCallParameterList ^^ {
    case name ~ params => CallFunctionRefExpr(StaticNamespaceName(NamespaceName(relative = false, prefixed = false, name.path: _*)), params)
  } | className ~ "::" ~ variableName ~ functionCallParameterList ^^ {
    case cname ~ _ ~ method ~ params => CallStaticMethodRefExpr(cname, method, params)
  } | className ~ "::" ~ variableWithoutObjects ~ functionCallParameterList ^^ {
    case cname ~ _ ~ ((n, dims)) ~ params =>
      val method = DynamicName(dims.foldLeft(VariableRefExpr(n).asInstanceOf[RefExpr]) {
        (ref, dim) => DimRefExpr(ref, dim)
      })
      CallStaticMethodRefExpr(cname, method, params)
  } | variableClassName ~ "::" ~ variableName ~ functionCallParameterList ^^ {
    case cname ~ _ ~ method ~ params => CallStaticMethodRefExpr(cname, method, params)
  } | variableClassName ~ "::" ~ variableWithoutObjects ~ functionCallParameterList ^^ {
    case cname ~ _ ~ ((n, dims)) ~ params =>
      val method = DynamicName(dims.foldLeft(VariableRefExpr(n).asInstanceOf[RefExpr]) {
        (ref, dim) => DimRefExpr(ref, dim)
      })
      CallStaticMethodRefExpr(cname, method, params)
  } | variableWithoutObjects ~ functionCallParameterList ^^ {
    case ((n, dims)) ~ params =>
      val func = DynamicName(dims.foldLeft(VariableRefExpr(n).asInstanceOf[RefExpr]) {
        (ref, dim) => DimRefExpr(ref, dim)
      })
      CallFunctionRefExpr(func, params)
  }

  lazy val className: PackratParser[Name] =
    namespaceName ^^ {
      case NamespaceName("self") => SelfName
      case NamespaceName("parent") => ParentName
      case name => StaticNamespaceName(name)
    } | "namespace" ~> "\\" ~> namespaceName ^^ {
      n => StaticNamespaceName(NamespaceName(relative = false, prefixed = true, n.path: _*))
    } | "\\" ~> namespaceName ^^ {
      n => StaticNamespaceName(NamespaceName(relative = false, prefixed = false, n.path: _*))
    }

  lazy val fullyQualifiedClassName: PackratParser[NamespaceName] =
    namespaceName |
      "namespace" ~> "\\" ~> namespaceName ^^ {
        n => NamespaceName(relative = false, prefixed = true, n.path: _*)
      } | "\\" ~> namespaceName ^^ {
      n => NamespaceName(relative = false, prefixed = false, n.path: _*)
    }

  lazy val classNameReference: PackratParser[Name] = className | dynamicClassNameReference


  lazy val dynamicClassNameReference: PackratParser[Name] =
    baseVariable ~ rep("->" ~> objectProperty) ^^ {
      case v ~ props => DynamicName(props.flatten.foldLeft(v) {
        (ref, prop) =>
          prop(ref)
      })
    }

  lazy val ctorArguments: PackratParser[List[Expr]] = opt(functionCallParameterList) ^^ (_.getOrElse(Nil))

  lazy val commonScalar: PackratParser[Expr] =
    longNumLit ^^ {
      s => ScalarExpr(IntegerVal(s))
    } | doubleNumLit ^^ {
      s => ScalarExpr(DoubleVal(s))
    } | stringLit ^^ {
      s => ScalarExpr(StringVal(s.getBytes(parseCtx.settings.getCharset)))
    } | "__FILE__" ^^^ FileNameConstExpr() |
      "__LINE__" ^^^ LineNumberConstExpr() |
      "__FUNCTION__" ^^^ FunctionNameConstExpr() |
      "__METHOD__" ^^^ MethodNameConstExpr() |
      "__NAMESPACE__" ^^^ NamespaceNameConstExpr() |
      hereDocStartLit ~> opt(encapsAndWhitespaceLit) <~ hereDocEndLit ^^ {
        s => ScalarExpr(StringVal(s.getOrElse("").getBytes(parseCtx.settings.getCharset)))
      }

  lazy val staticScalar: PackratParser[Expr] =
    commonScalar | staticClassNameScalar | staticClassConstant | namespaceName ^^ {
      name => ConstGetExpr(name)
    } | "namespace" ~> "\\" ~> namespaceName ^^ {
      name => ConstGetExpr(NamespaceName(relative = false, prefixed = true, name.path :_*))
    } | "\\" ~> namespaceName ^^ {
      name => ConstGetExpr(NamespaceName(relative = false, prefixed = false, name.path :_*))
    } | "+" ~> staticScalar ^^ {
      s => PosExpr(s)
    } | "-" ~> staticScalar ^^ {
      s => NegExpr(s)
    } | "array" ~> "(" ~> staticArrayPairList <~ ")" ^^ {
      pairs => ArrayCreateExpr(pairs)
    } | "[" ~> staticArrayPairList <~ "]" ^^ {
      pairs => ArrayCreateExpr(pairs)
    } | "__CLASS__" ^^^ ClassNameConstExpr()

  lazy val staticClassConstant: PackratParser[Expr] = className ~ "::" ~ identLit ^^ {
    case cname ~ _ ~ name => ClassConstantExpr(cname, name)
  }

  lazy val scalar: PackratParser[Expr] = classNameScalar | classConstant | namespaceName ^^ {
    name => ConstGetExpr(name)
  } | "namespace" ~> "\\" ~> namespaceName ^^ {
    name => ConstGetExpr(NamespaceName(relative = false, prefixed = true, name.path :_*))
  } | "\\" ~> namespaceName ^^ {
    name => ConstGetExpr(NamespaceName(relative = false, prefixed = false, name.path :_*))
  } | commonScalar | "\"" ~> encapsList <~ "\"" ^^ {
    interpolated => InterpolatedStringExpr(interpolated)
  } | hereDocStartLit ~> encapsList <~ hereDocEndLit ^^ {
    interpolated => InterpolatedStringExpr(interpolated)
  } | "__CLASS__" ^^^ ClassNameConstExpr()

  lazy val staticArrayPairList: PackratParser[List[ArrayKeyValue]] = repsep(
    opt(staticScalar <~ "=>") ~ staticScalar ^^ {
      case keyExpr ~ valueExpr => ArrayKeyValue(keyExpr, valueExpr, isRef = false)
    }, ",") <~ opt(",")

  lazy val parenthesisExpr: PackratParser[Expr] = "(" ~> expr <~ ")"

  lazy val variable: PackratParser[RefExpr] =
    baseVariableWithFunctionCalls ~ "->" ~ objectProperty ~ methodOrNot ~ variableProperties ^^ {
      case v ~ _ ~ refMaps ~ optMethod ~ varProps =>
        val ref = refMaps.foldLeft(v) {
          (v, refMap) => refMap(v)
        }
        val methodRef = optMethod.map {
          params =>
            ref match {
              case PropertyRefExpr(instance, name) => CallMethodRefExpr(instance, name, params)
              case nameExpr => CallFunctionRefExpr(DynamicName(nameExpr), params)
            }
        }.getOrElse(ref)
        varProps.foldLeft(methodRef) {
          (v, refMap) => refMap(v)
        }
    } | baseVariableWithFunctionCalls

  lazy val variableProperties: PackratParser[List[RefExpr => RefExpr]] = rep(variableProperty)

  lazy val variableProperty: PackratParser[RefExpr => RefExpr] = "->" ~> objectProperty ~ methodOrNot ^^ {
    case refMaps ~ optMethod =>
      v: RefExpr =>
        val ref = refMaps.foldLeft(v) {
          (v, refMap) => refMap(v)
        }
        optMethod.map {
          params =>
            ref match {
              case PropertyRefExpr(instance, name) => CallMethodRefExpr(instance, name, params)
              case nameExpr => CallFunctionRefExpr(DynamicName(nameExpr), params)
            }
        }.getOrElse(ref)
  }

  lazy val mathod: PackratParser[List[Expr]] = functionCallParameterList

  lazy val methodOrNot: PackratParser[Option[List[Expr]]] = opt(mathod)

  lazy val variableWithoutObjects: PackratParser[(Name, List[Option[Expr]])] =
    referenceVariable | simpleIndirectReference ~ referenceVariable ^^ {
      case indirects ~ ((n, dims)) => DynamicName(indirects.foldLeft(VariableRefExpr(n).asInstanceOf[RefExpr]) {
        (v, indirect) =>
          indirect(v)
      }) -> dims
    }

  lazy val staticMember: PackratParser[RefExpr] = className ~ "::" ~ variableWithoutObjects ^^ {
    case cname ~ _ ~ ((n, dims)) =>
      dims.foldLeft(StaticClassVarRefExpr(cname, n).asInstanceOf[RefExpr]) {
        (ref, dim) => DimRefExpr(ref, dim)
      }
  } | variableClassName ~ "::" ~ variableWithoutObjects ^^ {
    case cname ~ _ ~ ((n, dims)) =>
      dims.foldLeft(StaticClassVarRefExpr(cname, n).asInstanceOf[RefExpr]) {
        (ref, dim) => DimRefExpr(ref, dim)
      }
  }

  lazy val variableClassName: PackratParser[Name] = referenceVariable ^^ {
    case (n, dims) => DynamicName(dims.foldLeft(VariableRefExpr(n).asInstanceOf[RefExpr]) {
      (ref, dim) => DimRefExpr(ref, dim)
    })
  }

  lazy val baseVariableWithFunctionCalls: PackratParser[RefExpr] = functionCall | baseVariable

  lazy val baseVariable: PackratParser[RefExpr] = staticMember | referenceVariable ^^ {
    case (n, dims) => dims.foldLeft(VariableRefExpr(n).asInstanceOf[RefExpr]) {
      (ref, dim) => DimRefExpr(ref, dim)
    }
  } | simpleIndirectReference ~ referenceVariable ^^ {
    case indirects ~ ((n, dims)) =>
      val ref = indirects.foldLeft(VariableRefExpr(n).asInstanceOf[RefExpr]) {
        (v, indirect) =>
          indirect(v)
      }
      dims.foldLeft(ref) {
        (ref, dim) => DimRefExpr(ref, dim)
      }
  }

  lazy val referenceVariable: PackratParser[(Name, List[Option[Expr]])] =
    compoundVariable ~ rep("[" ~> dimOffset <~ "]" | "{" ~> expr <~ "}" ^^ Some.apply) ^^ {
      case n ~ dims => n -> dims
    }

  lazy val compoundVariable: PackratParser[Name] = variableLit ^^ {
    v => StaticName(v)
  } | "$" ~> "{" ~> expr <~ "}" ^^ {
    expr => DynamicName(expr)
  }

  lazy val dimOffset: PackratParser[Option[Expr]] = opt(expr)

  lazy val objectProperty: PackratParser[List[RefExpr => RefExpr]] = objectDimList | variableWithoutObjects ^^ {
    case (n, dims) =>
      val name = DynamicName(dims.foldLeft(VariableRefExpr(n).asInstanceOf[RefExpr]) {
        (ref, dim) =>
          DimRefExpr(ref, dim)
      })
      List({
        ref: RefExpr => PropertyRefExpr(ref, name)
      })
  }

  lazy val objectDimList: PackratParser[List[RefExpr => RefExpr]] =
    variableName ~ rep(
      "[" ~> dimOffset <~ "]" | "{" ~> expr <~ "}" ^^ Some.apply
    ) ^^ {
      case name ~ dims => {
        ref: RefExpr => PropertyRefExpr(ref, name)
      } :: dims.map(d => {
        ref: RefExpr => DimRefExpr(ref, d)
      })
    }

  lazy val variableName: PackratParser[Name] = identLit ^^ StaticName.apply | "{" ~> expr <~ "}" ^^ DynamicName.apply

  lazy val simpleIndirectReference: PackratParser[List[Expr => RefExpr]] =
    rep1("$" ^^^ {
      e: Expr => VariableRefExpr(DynamicName(e))
    })

  lazy val assignmentList: PackratParser[List[Option[RefExpr]]] = rep1sep(opt(assignmentListElement), ",")

  lazy val assignmentListElement: PackratParser[RefExpr] = variable | "list" ~> "(" ~> assignmentList <~ ")" ^^ {
    refs => ListRefExpr(refs)
  }

  lazy val arrayPairList: PackratParser[List[ArrayKeyValue]] = repsep(
    opt(expr <~ "=>") ~ expr ^^ {
      case keyExpr ~ valueExpr => ArrayKeyValue(keyExpr, valueExpr, isRef = false)
    } | opt(expr <~ "=>") ~ "&" ~ variable ^^ {
      case keyExpr ~ _ ~ v => ArrayKeyValue(keyExpr, v, isRef = true)
    }, ",") <~ opt(",")

  lazy val encapsList: PackratParser[List[Either[String, Expr]]] =
    rep(encapsVar ^^ Right.apply | encapsAndWhitespaceLit ^^ Left.apply)

  lazy val encapsVar: PackratParser[Expr] =
    variableLit ~ "[" ~ encapsVarOffset <~ "]" ^^ {
      case v ~ _ ~ idx =>
        val ref = VariableRefExpr(StaticName(v))
        DimRefExpr(ref, Some(idx))
    } | variableLit ~ "->" ~ identLit ^^ {
      case v ~ _ ~ n =>
        val ref = VariableRefExpr(StaticName(v))
        PropertyRefExpr(ref, StaticName(n))
    } | variableLit ^^ {
      v => VariableRefExpr(StaticName(v))
    } | "${" ~> expr <~ "}" |
      "${" ~> identLit ~ "[" ~ expr <~ "]" ^^ {
        case v ~ _ ~ e =>
          val ref = VariableRefExpr(StaticName(v))
          DimRefExpr(ref, Some(e))
      } | "{$" ~> variable <~ "}"

  lazy val encapsVarOffset: PackratParser[Expr] = identLit ^^ {
    idx => ScalarExpr(StringVal(idx.getBytes(parseCtx.settings.getCharset)))
  } | longNumLit ^^ {
    idx => ScalarExpr(IntegerVal(idx))
  } | variable

  lazy val internalFunctionsInYacc: PackratParser[Expr] = "isset" ~> "(" ~> issetVariables <~ ")" ^^ {
    exprs => IsSetExpr(exprs)
  } | "empty" ~> "(" ~> expr <~ ")" ^^ {
    expr => EmptyExpr(expr)
  } | "include" ~> expr ^^ {
    e => IncludeExpr(e)
  } | "include_once" ~> expr ^^ {
    e => IncludeOnceExpr(e)
  } | "eval" ~> "(" ~> expr <~ ")" ^^ {
    e => EvalExpr(e)
  } | "require" ~> expr ^^ {
    e => RequireExpr(e)
  } | "require_once" ~> expr ^^ {
    e => RequireOnceExpr(e)
  }

  lazy val issetVariables: PackratParser[List[Expr]] = rep1sep(issetVariable, ",")

  lazy val issetVariable: PackratParser[Expr] = expr

  lazy val classConstant: PackratParser[Expr] = className ~ "::" ~ identLit ^^ {
    case cname ~ _ ~ n => ClassConstantExpr(cname, n)
  } | variableClassName ~ "::" ~ identLit ^^ {
    case cname ~ _ ~ n => ClassConstantExpr(cname, n)
  }

  lazy val staticClassNameScalar: PackratParser[Expr] = className <~ "::" <~ "class" ^^ {
    cname => ClassNameExpr(cname)
  }

  lazy val classNameScalar: PackratParser[Expr] = className <~ "::" <~ "class" ^^ {
    cname => ClassNameExpr(cname)
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

  lazy val integerCastLit: PackratParser[String] =
    elem("integerCast", _.isInstanceOf[IntegerCast]) ^^ (_.chars)

  lazy val doubleCastLit: PackratParser[String] =
    elem("doubleCast", _.isInstanceOf[DoubleCast]) ^^ (_.chars)

  lazy val stringCastLit: PackratParser[String] =
    elem("stringCast", _.isInstanceOf[StringCast]) ^^ (_.chars)

  lazy val arrayCastLit: PackratParser[String] =
    elem("arrayCast", _.isInstanceOf[ArrayCast]) ^^ (_.chars)

  lazy val booleanCastLit: PackratParser[String] =
    elem("booleanCast", _.isInstanceOf[BooleanCast]) ^^ (_.chars)

  implicit def parser2packrat1[T <: HasNodePosition](p: => super.Parser[T]): PackratParser[T] = {
    lazy val q = p
    memo(super.Parser {
      in =>
        val pos = FileNodePosition(parseCtx.fileName, in.pos.line)
        q(in) match {
          case Success(n, in1) =>
            n.position = pos
            Success(n, in1)
          case ns: NoSuccess => ns
        }
    })
  }
}

object JbjParser {
  def apply(fileName: String, s: String, settings: JbjSettings): Prog = {
    val parser = new JbjParser(ParseContext(fileName, settings))
    parser.parse(s)
  }
}
