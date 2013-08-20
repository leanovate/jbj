package de.leanovate.jbj.parser

import de.leanovate.jbj.ast._
import de.leanovate.jbj.ast.stmt.cond._
import de.leanovate.jbj.parser.JbjTokens._
import de.leanovate.jbj.ast.stmt._
import de.leanovate.jbj.runtime.value._
import scala.collection.mutable
import scala.util.parsing.combinator.{PackratParsers, Parsers}
import scala.language.implicitConversions
import de.leanovate.jbj.runtime.exception.ParseJbjException
import de.leanovate.jbj.runtime.value.StringVal
import de.leanovate.jbj.ast.NamespaceName
import de.leanovate.jbj.runtime.Settings
import de.leanovate.jbj.ast.expr._
import de.leanovate.jbj.ast.expr.VariableReferableExpr
import de.leanovate.jbj.parser.JbjTokens.ArrayCast
import de.leanovate.jbj.ast.stmt.cond.DefaultCaseBlock
import de.leanovate.jbj.ast.expr.calc.AddExpr
import de.leanovate.jbj.ast.expr.comp.InstanceOfExpr
import de.leanovate.jbj.ast.name.StaticName
import de.leanovate.jbj.parser.JbjTokens.DoubleCast
import de.leanovate.jbj.ast.stmt.ReturnStmt
import de.leanovate.jbj.ast.expr.calc.SubExpr
import de.leanovate.jbj.ast.stmt.cond.SwitchStmt
import scala.Some
import de.leanovate.jbj.ast.stmt.ClassTypeHint
import de.leanovate.jbj.ast.expr.AssignReferableExpr
import de.leanovate.jbj.ast.expr.IndexReferableExpr
import de.leanovate.jbj.ast.expr.value.ClassNameConstExpr
import de.leanovate.jbj.parser.JbjTokens.Inline
import de.leanovate.jbj.ast.expr.AssignRefReferableExpr
import de.leanovate.jbj.ast.stmt.UnsetStmt
import de.leanovate.jbj.ast.stmt.StaticVarDeclStmt
import de.leanovate.jbj.ast.Prog
import de.leanovate.jbj.ast.expr.calc.BitAndExpr
import de.leanovate.jbj.ast.stmt.ExprStmt
import de.leanovate.jbj.ast.expr.calc.ConcatExpr
import de.leanovate.jbj.ast.stmt.CatchBlock
import de.leanovate.jbj.ast.expr.calc.NegExpr
import de.leanovate.jbj.parser.JbjTokens.StringCast
import de.leanovate.jbj.ast.expr.calc.SubFromReferableExpr
import de.leanovate.jbj.ast.expr.IsSetExpr
import de.leanovate.jbj.ast.expr.calc.MulExpr
import de.leanovate.jbj.ast.name.DynamicName
import de.leanovate.jbj.ast.expr.CallStaticMethodReferableExpr
import de.leanovate.jbj.ast.expr.cast.StringCastExpr
import de.leanovate.jbj.ast.expr.PropertyReferableExpr
import de.leanovate.jbj.ast.stmt.loop.ForeachValueStmt
import de.leanovate.jbj.parser.JbjTokens.Identifier
import de.leanovate.jbj.ast.expr.PrintExpr
import de.leanovate.jbj.ast.expr.comp.LeExpr
import de.leanovate.jbj.ast.expr.include.RequireExpr
import de.leanovate.jbj.parser.JbjTokens.LongNumLit
import de.leanovate.jbj.ast.expr.calc.PosExpr
import de.leanovate.jbj.ast.expr.comp.EqExpr
import de.leanovate.jbj.ast.stmt.TraitUseDecl
import de.leanovate.jbj.ast.name.StaticNamespaceName
import de.leanovate.jbj.ast.expr.value.MethodNameConstExpr
import de.leanovate.jbj.ast.expr.comp.NotEqExpr
import de.leanovate.jbj.ast.stmt.loop.ForeachKeyValueStmt
import de.leanovate.jbj.ast.expr.comp.BoolOrExpr
import de.leanovate.jbj.ast.stmt.GlobalVarDeclAssignStmt
import de.leanovate.jbj.ast.expr.value.FunctionNameConstExpr
import de.leanovate.jbj.ast.expr.include.IncludeOnceExpr
import de.leanovate.jbj.runtime.value.IntegerVal
import de.leanovate.jbj.ast.stmt.ClassVarDecl
import de.leanovate.jbj.ast.expr.StaticClassVarReferableExpr
import de.leanovate.jbj.ast.expr.comp.GtExpr
import de.leanovate.jbj.ast.expr.calc.DivExpr
import de.leanovate.jbj.ast.stmt.EchoStmt
import de.leanovate.jbj.ast.expr.IndexGetExpr
import de.leanovate.jbj.parser.JbjTokens.HereDocStart
import de.leanovate.jbj.ast.FileNodePosition
import de.leanovate.jbj.ast.stmt.loop.ForStmt
import de.leanovate.jbj.ast.stmt.ParameterDecl
import de.leanovate.jbj.ast.expr.calc.BitNotExpr
import de.leanovate.jbj.ast.stmt.ConstDeclStmt
import de.leanovate.jbj.ast.expr.comp.BoolAndExpr
import de.leanovate.jbj.ast.stmt.cond.IfStmt
import de.leanovate.jbj.ast.expr.cast.BooleanCastExpr
import de.leanovate.jbj.ast.stmt.FunctionDeclStmt
import de.leanovate.jbj.ast.stmt.ClassMethodDecl
import de.leanovate.jbj.ast.expr.CallMethodReferableExpr
import de.leanovate.jbj.ast.expr.comp.TernaryExpr
import de.leanovate.jbj.ast.expr.value.ClassConstantExpr
import de.leanovate.jbj.ast.expr.cast.ArrayCastExpr
import de.leanovate.jbj.parser.JbjTokens.HereDocEnd
import de.leanovate.jbj.ast.expr.calc.ModExpr
import de.leanovate.jbj.ast.stmt.TryCatchStmt
import de.leanovate.jbj.ast.expr.value.InterpolatedStringExpr
import de.leanovate.jbj.ast.expr.comp.BoolNotExpr
import de.leanovate.jbj.ast.expr.value.ConstGetExpr
import de.leanovate.jbj.ast.stmt.BlockStmt
import de.leanovate.jbj.parser.JbjTokens.BooleanCast
import de.leanovate.jbj.parser.JbjTokens.Variable
import de.leanovate.jbj.ast.stmt.ClassDeclStmt
import de.leanovate.jbj.ast.stmt.BreakStmt
import de.leanovate.jbj.ast.expr.include.IncludeExpr
import de.leanovate.jbj.ast.expr.comp.LtExpr
import de.leanovate.jbj.ast.stmt.loop.DoWhileStmt
import de.leanovate.jbj.ast.expr.comp.GeExpr
import de.leanovate.jbj.ast.stmt.loop.WhileStmt
import de.leanovate.jbj.ast.expr.calc.AddToReferableExpr
import de.leanovate.jbj.ast.expr.calc.DivByReferableExpr
import de.leanovate.jbj.ast.stmt.cond.ElseIfBlock
import de.leanovate.jbj.ast.stmt.ClassConstDecl
import de.leanovate.jbj.ast.expr.value.ScalarExpr
import de.leanovate.jbj.parser.JbjTokens.IntegerCast
import de.leanovate.jbj.ast.expr.CloneExpr
import de.leanovate.jbj.ast.expr.EvalExpr
import de.leanovate.jbj.ast.stmt.ThrowStmt
import de.leanovate.jbj.ast.stmt.LabelStmt
import de.leanovate.jbj.parser.JbjTokens.EncapsAndWhitespace
import de.leanovate.jbj.ast.expr.NewReferableExpr
import de.leanovate.jbj.ast.expr.value.FileNameConstExpr
import de.leanovate.jbj.ast.expr.CallFunctionReferableExpr
import de.leanovate.jbj.ast.stmt.StaticAssignment
import de.leanovate.jbj.ast.expr.calc.BitOrExpr
import de.leanovate.jbj.ast.stmt.cond.CaseBlock
import de.leanovate.jbj.ast.expr.GetAndDecrExpr
import de.leanovate.jbj.ast.stmt.ContinueStmt
import de.leanovate.jbj.ast.expr.calc.BitXorExpr
import de.leanovate.jbj.ast.expr.IncrAndGetExpr
import de.leanovate.jbj.ast.expr.calc.ConcatWithReferableExpr
import de.leanovate.jbj.ast.expr.ClassNameExpr
import de.leanovate.jbj.ast.expr.DecrAndGetExpr
import de.leanovate.jbj.ast.expr.calc.MulByReferableExpr
import de.leanovate.jbj.ast.expr.include.RequireOnceExpr
import de.leanovate.jbj.ast.expr.ArrayCreateExpr
import de.leanovate.jbj.ast.stmt.InlineStmt
import de.leanovate.jbj.ast.expr.value.LineNumberConstExpr
import de.leanovate.jbj.ast.expr.cast.DoubleCastExpr
import de.leanovate.jbj.parser.JbjTokens.Keyword
import de.leanovate.jbj.ast.expr.cast.IntegerCastExpr
import de.leanovate.jbj.parser.JbjTokens.StringLit
import de.leanovate.jbj.ast.expr.comp.BoolXorExpr
import de.leanovate.jbj.ast.expr.GetAndIncrExpr

class JbjParser(parseCtx: ParseContext) extends Parsers with PackratParsers {
  type Elem = JbjTokens.Token

  private val keywordCache = mutable.HashMap[String, Parser[String]]()

  def parse(s: String): Prog = {
    val tokens = new TokenReader(s, InitialLexer)
    phrase(start)(tokens) match {
      case Success(tree, _) => tree
      case e: NoSuccess =>
        throw new ParseJbjException(e.msg, FileNodePosition(parseCtx.fileName, e.next.pos.line))
    }
  }

  def parseStmt(s: String): Prog = {
    val tokens = new TokenReader(s, ScriptLexer)
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
    path => NamespaceName(relative = true, path: _*)
  }

  lazy val topStatement: PackratParser[Stmt] = statement | functionDeclarationStatement |
    classDeclarationStatement | constantDeclaration

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
    } | "foreach" ~> "(" ~> variable ~ "as" ~ foreachVariable ~ foreachOptionalArg ~ ")" ~ foreachStatement ^^ {
      case array ~ _ ~ valueVar ~ None ~ _ ~ stmts =>
        ForeachValueStmt(array, valueVar, stmts)
      case array ~ _ ~ keyVar ~ Some(valueVar) ~ _ ~ stmts =>
        ForeachKeyValueStmt(array, keyVar, valueVar, stmts)
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

  lazy val unsetVariables: PackratParser[List[ReferableExpr]] = rep1sep(variable, ",")

  lazy val functionDeclarationStatement: PackratParser[FunctionDeclStmt] = untickedFunctionDeclarationStatement <~ rep(";")

  lazy val classDeclarationStatement: PackratParser[ClassDeclStmt] = untickedClassDeclarationStatement <~ rep(";")

  lazy val untickedFunctionDeclarationStatement: PackratParser[FunctionDeclStmt] =
    "function" ~ opt("&") ~ identLit ~ "(" ~ parameterList ~ ")" ~ "{" ~ innerStatementList <~ "}" ^^ {
      case func ~ isRef ~ name ~ _ ~ params ~ _ ~ _ ~ body =>
        FunctionDeclStmt(NamespaceName(relative = true, name), isRef.isDefined, params, body)
    }

  lazy val untickedClassDeclarationStatement: PackratParser[ClassDeclStmt] =
    classEntryType ~ identLit ~ extendsFrom ~ implementsList ~ "{" ~ classStatementList <~ "}" ^^ {
      case entry ~ name ~ exts ~ impls ~ _ ~ decls =>
        ClassDeclStmt(entry, NamespaceName(relative = true, name), exts, impls, decls)
    }

  lazy val classEntryType: PackratParser[ClassEntry.Type] =
    "class" ^^^ ClassEntry.CLASS | "abstract" ~ "class" ^^^ ClassEntry.ABSTRACT_CLASS |
      "final" ~ "class" ^^^ ClassEntry.FINAL_CLASS | "trait" ^^^ ClassEntry.TRAIT

  lazy val extendsFrom: PackratParser[Option[NamespaceName]] = opt("extends" ~> fullyQualifiedClassName)

  lazy val implementsList: PackratParser[List[NamespaceName]] = opt("implements" ~> interfaceList) ^^ {
    optInterfaces => optInterfaces.getOrElse(Nil)
  }

  lazy val interfaceList: PackratParser[List[NamespaceName]] = rep1sep(fullyQualifiedClassName, ",")

  lazy val foreachOptionalArg: PackratParser[Option[ReferableExpr]] = opt("=>" ~> variable)

  lazy val foreachVariable: PackratParser[ReferableExpr] =
    variable | "&" ~> variable | "list" ~> "(" ~> assignmentList <~ ")" ^^ {
      refs => ListReferableExpr(refs)
    }

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

  lazy val globalVar: PackratParser[Name] = variableLit ^^ {
    v => StaticName(v)
  } | "$" ~> rVariable ^^ {
    e => DynamicName(e)
  } | "$" ~> "{" ~> expr <~ "}" ^^ {
    e => DynamicName(e)
  }

  lazy val globalVarList: PackratParser[List[Name]] = rep1sep(globalVar, ",")

  lazy val staticVarList: PackratParser[List[StaticAssignment]] = rep1sep(variableLit ~ opt("=" ~> staticScalar) ^^ {
    case v ~ optScalar => StaticAssignment(v, optScalar)
  }, ",")

  lazy val classStatementList: PackratParser[List[ClassMemberDecl]] = rep(classStatement)

  lazy val classStatement: PackratParser[ClassMemberDecl] = variableModifiers ~ classVariableDeclaration <~ ";" ^^ {
    case modifiers ~ assignments => ClassVarDecl(modifiers, assignments)
  } | classConstantDeclaration ^^ {
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

  lazy val newExpr: PackratParser[NewReferableExpr] = "new" ~> classNameReference ~ ctorArguments ^^ {
    case name ~ args => NewReferableExpr(name, args)
  }

  lazy val exprWithoutVariable: PackratParser[Expr] =
    expr ~ "?" ~ expr ~ ":" ~ expr ^^ {
      case cond ~ _ ~ tExpr ~ _ ~ fExpr => TernaryExpr(cond, tExpr, fExpr)
    } | "list" ~> "(" ~> assignmentList ~ ")" ~ "=" ~ expr ^^ {
      case refs ~ _ ~ _ ~e => AssignReferableExpr(ListReferableExpr(refs), e)
    }| variable ~ "=" ~ expr ^^ {
      case v ~ _ ~ e => AssignReferableExpr(v, e)
    } | variable ~ "=" ~ "&" ~ variable ^^ {
      case v ~ _ ~ _ ~ ref => AssignRefReferableExpr(v, ref)
    } | "clone" ~> expr ^^ {
      e => CloneExpr(e)
    } | variable ~ "+=" ~ expr ^^ {
      case v ~ _ ~ e => AddToReferableExpr(v, e)
    } | variable ~ "-=" ~ expr ^^ {
      case v ~ _ ~ e => SubFromReferableExpr(v, e)
    } | variable ~ "*=" ~ expr ^^ {
      case v ~ _ ~ e => MulByReferableExpr(v, e)
    } | variable ~ "/=" ~ expr ^^ {
      case v ~ _ ~ e => DivByReferableExpr(v, e)
    } | variable ~ ".=" ~ expr ^^ {
      case v ~ _ ~ e => ConcatWithReferableExpr(v, e)
    } | binary(minPrec) | termWithoutVariable

  def binaryOp(level: Int): Parser[((Expr, Expr) => Expr)] = {
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
    } | rwVariable <~ "++" ^^ {
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

  lazy val functionCall: PackratParser[ReferableExpr] = namespaceName ~ functionCallParameterList ^^ {
    case name ~ params => CallFunctionReferableExpr(StaticNamespaceName(name), params)
  } | "namespace" ~> "\\" ~> namespaceName ~ functionCallParameterList ^^ {
    case name ~ params => CallFunctionReferableExpr(StaticNamespaceName(NamespaceName(relative = false, name.path: _*)), params)
  } | "\\" ~> namespaceName ~ functionCallParameterList ^^ {
    case name ~ params => CallFunctionReferableExpr(StaticNamespaceName(NamespaceName(relative = false, name.path: _*)), params)
  } | className ~ "::" ~ variableName ~ functionCallParameterList ^^ {
    case cname ~ _ ~ method ~ params => CallStaticMethodReferableExpr(cname, method, params)
  } | className ~ "::" ~ variableWithoutObjects ~ functionCallParameterList ^^ {
    case cname ~ _ ~ ((n, dims)) ~ params =>
      val method = DynamicName(dims.foldLeft(VariableReferableExpr(n).asInstanceOf[ReferableExpr]) {
        (ref, dim) => IndexReferableExpr(ref, dim)
      })
      CallStaticMethodReferableExpr(cname, method, params)
  } | variableClassName ~ "::" ~ variableName ~ functionCallParameterList ^^ {
    case cname ~ _ ~ method ~ params => CallStaticMethodReferableExpr(cname, method, params)
  } | variableClassName ~ "::" ~ variableWithoutObjects ~ functionCallParameterList ^^ {
    case cname ~ _ ~ ((n, dims)) ~ params =>
      val method = DynamicName(dims.foldLeft(VariableReferableExpr(n).asInstanceOf[ReferableExpr]) {
        (ref, dim) => IndexReferableExpr(ref, dim)
      })
      CallStaticMethodReferableExpr(cname, method, params)
  } | variableWithoutObjects ~ functionCallParameterList ^^ {
    case ((n, dims)) ~ params =>
      val func = DynamicName(dims.foldLeft(VariableReferableExpr(n).asInstanceOf[ReferableExpr]) {
        (ref, dim) => IndexReferableExpr(ref, dim)
      })
      CallFunctionReferableExpr(func, params)
  }

  lazy val className: PackratParser[Name] =
    namespaceName ^^ StaticNamespaceName.apply | "namespace" ~> "\\" ~> namespaceName ^^ {
      n => StaticNamespaceName(NamespaceName(relative = false, n.path: _*))
    } | "\\" ~> namespaceName ^^ {
      n => StaticNamespaceName(NamespaceName(relative = false, n.path: _*))
    }

  lazy val fullyQualifiedClassName: PackratParser[NamespaceName] =
    namespaceName |
      "namespace" ~> "\\" ~> namespaceName ^^ {
        n => NamespaceName(relative = false, n.path: _*)
      } | "\\" ~> namespaceName ^^ {
      n => NamespaceName(relative = false, n.path: _*)
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
      s => ScalarExpr(StringVal(s.getBytes(parseCtx.settings.charset)))
    } | "__FILE__" ^^^ FileNameConstExpr() |
      "__LINE__" ^^^ LineNumberConstExpr() |
      "__FUNCTION__" ^^^ FunctionNameConstExpr() |
      "__METHOD__" ^^^ MethodNameConstExpr() |
      hereDocStartLit ~> opt(encapsAndWhitespaceLit) <~ hereDocEndLit ^^ {
        s => ScalarExpr(StringVal(s.getOrElse("").getBytes(parseCtx.settings.charset)))
      }

  lazy val staticScalar: PackratParser[Expr] =
    commonScalar | staticClassNameScalar | namespaceName ^^ {
      name => ConstGetExpr(name)
    } | "namespace" ~> "\\" ~> namespaceName ^^ {
      name => ConstGetExpr(name, relative = false)
    } | "\\" ~> namespaceName ^^ {
      name => ConstGetExpr(name, relative = false)
    } | "+" ~> staticScalar ^^ {
      s => PosExpr(s)
    } | "-" ~> staticScalar ^^ {
      s => NegExpr(s)
    } | "array" ~> "(" ~> staticArrayPairList <~ ")" ^^ {
      pairs => ArrayCreateExpr(pairs)
    } | "[" ~> staticArrayPairList <~ "]" ^^ {
      pairs => ArrayCreateExpr(pairs)
    } | staticClassConstant | "__CLASS__" ^^^ ClassNameConstExpr()

  lazy val staticClassConstant: PackratParser[Expr] = className ~ "::" ~ identLit ^^ {
    case cname ~ _ ~ name => ClassConstantExpr(cname, name)
  }

  lazy val scalar: PackratParser[Expr] = classNameScalar | classConstant | namespaceName ^^ {
    name => ConstGetExpr(name)
  } | "namespace" ~> "\\" ~> namespaceName ^^ {
    name => ConstGetExpr(name, relative = false)
  } | "\\" ~> namespaceName ^^ {
    name => ConstGetExpr(name, relative = false)
  } | commonScalar | "\"" ~> encapsList <~ "\"" ^^ {
    interpolated => InterpolatedStringExpr(interpolated)
  } | hereDocStartLit ~> encapsList <~ hereDocEndLit ^^ {
    interpolated => InterpolatedStringExpr(interpolated)
  } | "__CLASS__" ^^^ ClassNameConstExpr()

  lazy val staticArrayPairList: PackratParser[List[ArrayKeyValue]] = repsep(
    opt(staticScalar <~ "=>") ~ staticScalar ^^ {
      case keyExpr ~ valueExpr => ArrayKeyValue(keyExpr, valueExpr, isRef = false)
    }, ",") <~ opt(",")

  lazy val expr: PackratParser[Expr] = exprWithoutVariable ||| rVariable

  lazy val term: PackratParser[Expr] = termWithoutVariable ||| rVariable

  lazy val parenthesisExpr: PackratParser[Expr] = "(" ~> expr <~ ")"

  lazy val rVariable: PackratParser[ReferableExpr] = variable

  lazy val rwVariable: PackratParser[ReferableExpr] = variable

  lazy val variable: PackratParser[ReferableExpr] =
    baseVariableWithFunctionCalls ~ rep("->" ~> objectProperty) ~ methodOrNot ~ variableProperties ^^ {
      case v ~ refMaps ~ optMethod ~ varProps =>
        val ref = refMaps.flatten.foldLeft(v) {
          (v, refMap) => refMap(v)
        }
        val methodRef = optMethod.map {
          params =>
            ref match {
              case PropertyReferableExpr(instance, name) => CallMethodReferableExpr(instance, name, params)
              case nameExpr => CallFunctionReferableExpr(DynamicName(nameExpr), params)
            }
        }.getOrElse(ref)
        varProps.foldLeft(methodRef) {
          (v, refMap) => refMap(v)
        }
    }

  lazy val variableProperties: Parser[List[ReferableExpr => ReferableExpr]] = rep(variableProperty)

  lazy val variableProperty: Parser[ReferableExpr => ReferableExpr] = "->" ~> objectProperty ~ methodOrNot ^^ {
    case refMaps ~ optMethod =>
      v: ReferableExpr =>
        val ref = refMaps.foldLeft(v) {
          (v, refMap) => refMap(v)
        }
        optMethod.map {
          params =>
            ref match {
              case PropertyReferableExpr(instance, name) => CallMethodReferableExpr(instance, name, params)
              case nameExpr => CallFunctionReferableExpr(DynamicName(nameExpr), params)
            }
        }.getOrElse(ref)
  }

  lazy val mathod: PackratParser[List[Expr]] = functionCallParameterList

  lazy val methodOrNot: PackratParser[Option[List[Expr]]] = opt(mathod)

  lazy val variableWithoutObjects: PackratParser[(Name, List[Option[Expr]])] =
    referenceVariable | simpleIndirectReference ~ referenceVariable ^^ {
      case indirects ~ ((n, dims)) => DynamicName(indirects.foldLeft(VariableReferableExpr(n).asInstanceOf[ReferableExpr]) {
        (v, indirect) =>
          indirect(v)
      }) -> dims
    }

  lazy val staticMember: PackratParser[ReferableExpr] = className ~ "::" ~ variableWithoutObjects ^^ {
    case cname ~ _ ~ ((n, dims)) =>
      dims.foldLeft(StaticClassVarReferableExpr(cname, n).asInstanceOf[ReferableExpr]) {
        (ref, dim) => IndexReferableExpr(ref, dim)
      }
  } | variableClassName ~ "::" ~ variableWithoutObjects ^^ {
    case cname ~ _ ~ ((n, dims)) =>
      dims.foldLeft(StaticClassVarReferableExpr(cname, n).asInstanceOf[ReferableExpr]) {
        (ref, dim) => IndexReferableExpr(ref, dim)
      }
  }

  lazy val variableClassName: PackratParser[Name] = referenceVariable ^^ {
    case (n, dims) => DynamicName(dims.foldLeft(VariableReferableExpr(n).asInstanceOf[ReferableExpr]) {
      (ref, dim) => IndexReferableExpr(ref, dim)
    })
  }

  lazy val baseVariableWithFunctionCalls: PackratParser[ReferableExpr] = functionCall | baseVariable

  lazy val baseVariable: PackratParser[ReferableExpr] = referenceVariable ^^ {
    case (n, dims) => dims.foldLeft(VariableReferableExpr(n).asInstanceOf[ReferableExpr]) {
      (ref, dim) => IndexReferableExpr(ref, dim)
    }
  } | simpleIndirectReference ~ referenceVariable ^^ {
    case indirects ~ ((n, dims)) =>
      val ref = indirects.foldLeft(VariableReferableExpr(n).asInstanceOf[ReferableExpr]) {
        (v, indirect) =>
          indirect(v)
      }
      dims.foldLeft(ref) {
        (ref, dim) => IndexReferableExpr(ref, dim)
      }
  } | staticMember

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

  lazy val objectProperty: PackratParser[List[ReferableExpr => ReferableExpr]] = objectDimList | variableWithoutObjects ^^ {
    case (n, dims) =>
      val name = DynamicName(dims.foldLeft(VariableReferableExpr(n).asInstanceOf[ReferableExpr]) {
        (ref, dim) =>
          IndexReferableExpr(ref, dim)
      })
      List({
        ref: ReferableExpr => PropertyReferableExpr(ref, name)
      })
  }

  lazy val objectDimList: PackratParser[List[ReferableExpr => ReferableExpr]] =
    variableName ~ rep(
      "[" ~> dimOffset <~ "]" | "{" ~> expr <~ "}" ^^ Some.apply
    ) ^^ {
      case name ~ dims => {
        ref: ReferableExpr => PropertyReferableExpr(ref, name)
      } :: dims.map(d => {
        ref: ReferableExpr => IndexReferableExpr(ref, d)
      })
    }

  lazy val variableName: PackratParser[Name] = identLit ^^ StaticName.apply | "{" ~> expr <~ "}" ^^ DynamicName.apply

  lazy val simpleIndirectReference: PackratParser[List[Expr => ReferableExpr]] =
    rep1("$" ^^^ {
      e: Expr => VariableReferableExpr(DynamicName(e))
    })

  lazy val assignmentList: PackratParser[List[ReferableExpr]] = rep1sep(assignmentListElement, ",")

  lazy val assignmentListElement: PackratParser[ReferableExpr] = variable | "list" ~> "(" ~> assignmentList <~ ")" ^^ {
    refs => ListReferableExpr(refs)
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
        val ref = VariableReferableExpr(StaticName(v))
        IndexReferableExpr(ref, Some(idx))
    } | variableLit ~ "->" ~ identLit ^^ {
      case v ~ _ ~ n =>
        val ref = VariableReferableExpr(StaticName(v))
        PropertyReferableExpr(ref, StaticName(n))
    } | variableLit ^^ {
      v => VariableReferableExpr(StaticName(v))
    } | "${" ~> expr <~ "}" |
      "${" ~> identLit ~ "[" ~ expr <~ "]" ^^ {
        case v ~ _ ~ e =>
          val ref = VariableReferableExpr(StaticName(v))
          IndexReferableExpr(ref, Some(e))
      } | "{$" ~> variable <~ "}"

  lazy val encapsVarOffset: PackratParser[Expr] = identLit ^^ {
    idx => ScalarExpr(StringVal(idx.getBytes(parseCtx.settings.charset)))
  } | longNumLit ^^ {
    idx => ScalarExpr(IntegerVal(idx))
  } | variable

  lazy val internalFunctionsInYacc: PackratParser[Expr] = "isset" ~> "(" ~> issetVariables <~ ")" ^^ {
    exprs => IsSetExpr(exprs)
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

  lazy val issetVariable: PackratParser[Expr] = exprWithoutVariable ||| rVariable

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
  def apply(fileName: String, s: String, settings: Settings): Prog = {
    val parser = new JbjParser(ParseContext(fileName, settings))
    parser.parse(s)
  }
}
