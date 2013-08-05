package de.leanovate.jbj.parser

import scala.util.parsing.input.{CharArrayReader, Reader}
import de.leanovate.jbj.parser.JbjTokens._
import scala.util.parsing.input.CharArrayReader.EofCh
import scala.util.parsing.combinator.Parsers
import de.leanovate.jbj.parser.JbjTokens.Identifier
import de.leanovate.jbj.parser.JbjTokens.EOF
import de.leanovate.jbj.parser.JbjTokens.Keyword
import de.leanovate.jbj.parser.JbjTokens.StringLit

class ScriptLexer(in: Reader[Char]) extends Reader[Token] {

  import ScriptLexer.{Success, NoSuccess, token, whitespace}

  def this(in: String) = this(new CharArrayReader(in.toCharArray))

  private val (tok: Token, mode: LexerMode, rest1: Reader[Char], rest2: Reader[Char]) = whitespace(in) match {
    case Success(_, in1) =>
      token(in1) match {
        case Success((token, m), in2) => (token, m, in1, in2)
        case ns: NoSuccess => (errorToken(ns.msg), LexerMode.ERROR, ns.next, skip(ns.next))
      }
    case ns: NoSuccess => (errorToken(ns.msg), LexerMode.ERROR, ns.next, skip(ns.next))
  }

  private def skip(in: Reader[Char]) = if (in.atEnd) in else in.rest

  override def source: java.lang.CharSequence = in.source

  override def offset: Int = in.offset

  def first = tok

  def rest = mode.newLexer(rest2, LexerMode.IN_SCRIPTING)

  def pos = rest1.pos

  def atEnd = in.atEnd || (whitespace(in) match {
    case Success(_, in1) => in1.atEnd
    case _ => false
  })
}

object ScriptLexer extends Parsers with CommonLexerPatterns {
  /** The set of reserved identifiers: these will be returned as `Keyword`s. */
  val reserved = Set("static", "global", "public", "protected", "private", "var", "const",
    "class", "extends", "use", "interface", "trait", "implements", "abstract", "final",
    "exit", "die", "eval", "include", "include_once", "require", "require_once", "namespace",
    "echo", "print", "new", "clone", "eval", "isset",
    "return", "break", "continue", "goto", "yield",
    "try", "catch", "finally", "throw",
    "if", "else", "elseif", "endif", "while", "endwhile", "for", "endfor", "foreach", "as", "endforeach",
    "declare", "enddeclare", "instanceof",
    "switch", "case", "default", "endswitch",
    "function", "array", "list", "callable")

  /** The set of delimiters (ordering does not matter). */
  val delimiters = Set("$", ",", ":", "::", "?", "!", ";", "{", "}", "[", "]", "=>", "->",
    ".", "+", "-", "*", "/", "%", "(", ")", ".=", "+=", "-=", "*=", "/=", "%=", "--", "++",
    "<<", ">>", "^", "|", "&", "<<=", ">>=", "^=", "&=", "|=",
    "=", ">", ">=", "<", "<=", "==", "!=", "<>", "===", "!==",
    "||", "&&", "^", "or", "and", "xor", "\\")

  def token: Parser[(Token, LexerMode)] =
    str("?>") ^^^ Keyword(";") -> LexerMode.INITIAL |
      str("%>") ^^^ Keyword(";") -> LexerMode.INITIAL |
      str("</script") ~ rep(whitespaceChar) ~ '>' ~ opt('\n') ^^^ Keyword(";") -> LexerMode.INITIAL |
      commonScriptToken ^^ {
        t => t -> LexerMode.IN_SCRIPTING
      } | '\"' ^^^  Keyword("\"") -> LexerMode.DOUBLE_QUOTES

  def commonScriptToken: Parser[Token] =
    identChar ~ rep(identChar | digit) ^^ {
      case first ~ rest => processIdent(first :: rest mkString "")
    } | rep(digit) ~ '.' ~ rep1(digit) ~ opt(exponent) ^^ {
      case first ~ dot ~ rest ~ exponent =>
        DoubleNumLit(first ++ (dot :: rest) ++ exponent.getOrElse(Nil) mkString "")
    } | rep1(digit) ~ '.' ~ rep(digit) ~ opt(exponent) ^^ {
      case first ~ dot ~ rest ~ exponent =>
        DoubleNumLit(first ++ (dot :: rest) ++ exponent.getOrElse(Nil) mkString "")
    } | digit ~ rep(digit) ~ exponent ^^ {
      case first ~ rest ~ exponent =>
        DoubleNumLit((first :: rest) ++ exponent mkString "")
    } | '0' ~ rep1(octDigit) ^^ {
      case first ~ rest => convertNum(first :: rest mkString "", 8)
    } | '0' ~ 'b' ~ rep(binDigit) ^^ {
      case _ ~ _ ~ binary => convertNum(binary mkString "", 2)
    } | '0' ~ 'x' ~ rep(hexDigit) ^^ {
      case _ ~ _ ~ hex => convertNum(hex mkString "", 16)
    } | digit ~ rep(digit) ^^ {
      case first ~ rest => convertNum(first :: rest mkString "", 10)
    } | '$' ~> identChar ~ rep(identChar | digit) ^^ {
      case first ~ rest => Variable(first :: rest mkString "")
    } | '\'' ~ singleQuotedStr ~ '\'' ^^ {
      case '\'' ~ str ~ '\'' => StringLit(str)
    } | '\"' ~ doubleQuotedStr ~ '\"' ^^ {
      case '\"' ~ str ~ '\"' => str
    } | '(' ~> tabsOrSpaces ~> (str("int") | str("integer")) <~ tabsOrSpaces <~ ')' ^^ {
      s => IntegerCast(s)
    } | '(' ~> tabsOrSpaces ~> (str("real") | str("double") | str("float")) <~ tabsOrSpaces <~ ')' ^^ {
      s => DoubleCast(s)
    } | '(' ~> tabsOrSpaces ~> (str("string") | str("binary")) <~ tabsOrSpaces <~ ')' ^^ {
      s => StringCast(s)
    } | '(' ~> tabsOrSpaces ~> str("array") <~ tabsOrSpaces <~ ')' ^^ {
      s => ArrayCast(s)
    } | '(' ~> tabsOrSpaces ~> (str("bool") | str("boolean")) <~ tabsOrSpaces <~ ')' ^^ {
      s => BooleanCast(s)
    } | '(' ~> tabsOrSpaces ~> str("unset") <~ tabsOrSpaces <~ ')' ^^ {
      s => UnsetCast(s)
    } | EofCh ^^^ EOF |
      '\'' ~> failure("unclosed string literal") |
      '\"' ~> failure("unclosed string literal") |
      delim ^^ {
        d => d
      } | failure("illegal character")

  private def exponent: Parser[List[Elem]] = exponentMarker ~ opt(sign) ~ rep1(digit) ^^ {
    case first ~ sign ~ exponent => first :: (sign.toList ++ exponent)
  }

  private def strInterpolation: Parser[String] =
    '$' ~> '{' ~> rep(chrExcept('\"', '}', EofCh)) <~ '}' ^^ {
      chars => '$' :: chars mkString ""
    } | '{' ~> '$' ~> rep(chrExcept('\"', '}', EofCh)) <~ '}' ^^ {
      chars => '$' :: chars mkString ""
    } | '$' ~ identChar ~ rep(identChar | digit) ^^ {
      case start ~ first ~ rest => start :: first :: rest mkString ""
    }


  private def singleQuotedChar: Parser[Char] = '\\' ~> ('\\' ^^^ '\\' | '\'' ^^^ '\'') | chrExcept('\'', EofCh)

  private def singleQuotedStr: Parser[String] = rep(singleQuotedChar) ^^ {
    chars => chars.mkString("")
  }

  private def doubleQuotedChar: Parser[Char] = encapsCharReplacements |
    '$' <~ not(identChar | '{') | '{' <~ not('$') | chrExcept('\"', '$', '{', EofCh)

  private def doubleQuotedStr: Parser[Token] = rep(doubleQuotedChar) ^^ {
    chs => StringLit(chs mkString "")
  }

  // see `whitespace in `Scanners`
  def whitespace: Parser[Any] = rep(
    whitespaceChar
      | '/' ~ '*' ~ comment
      | '/' ~ '/' ~ rep(chrExcept(EofCh, '\n'))
      | '#' ~ rep(chrExcept(EofCh, '\n'))
      | '/' ~ '*' ~ failure("unclosed comment")
  )

  private def tabsOrSpaces: Parser[Any] = rep(elem("space char", ch => ch == ' ' || ch == '\t'))

  private def exponentMarker = elem("exponent", ch => ch == 'e' || ch == 'E')

  private def sign = elem("sign", ch => ch == '-' || ch == '+')

  private def binDigit = elem("binDigit", ch => ch == '0' || ch == '1')

  private def octDigit = elem("octDigt", ch => ch >= '0' && ch <= '8')

  /** A character-parser that matches a white-space character (and returns it). */
  private def whitespaceChar = elem("space char", ch => ch <= ' ' && ch != EofCh)

  protected def comment: Parser[Any] =
    '*' ~ '/' ^^ {
      case _ => ' '
    } | chrExcept(EofCh) ~ comment

  protected def processIdent(name: String) =
    if (reserved contains name) Keyword(name) else Identifier(name)

  private lazy val _delim: Parser[Token] = {
    // construct parser for delimiters by |'ing together the parsers for the individual delimiters,
    // starting with the longest one -- otherwise a delimiter D will never be matched if there is
    // another delimiter that is a prefix of D
    def parseDelim(s: String): Parser[Token] = accept(s.toList) ^^ {
      x => Keyword(s)
    }

    val d = new Array[String](delimiters.size)
    delimiters.copyToArray(d, 0)
    scala.util.Sorting.quickSort(d)
    (d.toList map parseDelim).foldRight(failure("no matching delimiter"): Parser[Token])((x, y) => y | x)
  }

  private def delim: Parser[Token] = _delim

  private def convertNum(chars: String, radix: Int): Token = {
    try {
      LongNumLit(chars, java.lang.Long.valueOf(chars, radix))
    } catch {
      case _: NumberFormatException =>
        val bInt = BigInt(chars, radix)
        DoubleNumLit(chars, bInt.toDouble)
    }
  }
}
