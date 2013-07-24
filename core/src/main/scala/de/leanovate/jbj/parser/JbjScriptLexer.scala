package de.leanovate.jbj.parser

import scala.util.parsing.input.{CharArrayReader, Reader}
import JbjTokens.{Token, ScriptEnd, VarIdentifier, NumericLit, StringLit, InterpolatedStringLit, EOF, Keyword, Identifier, errorToken}
import scala.util.parsing.input.CharArrayReader.EofCh
import scala.util.parsing.combinator.Parsers

class JbjScriptLexer(in: Reader[Char]) extends Reader[Token] with Parsers {
  type Elem = Char

  import JbjScriptLexer.{delimiters, reserved}

  def this(in: String) = this(new CharArrayReader(in.toCharArray()))

  private val (tok, rest1, rest2) = whitespace(in) match {
    case Success(_, in1) =>
      token(in1) match {
        case Success(tok, in2) => (tok, in1, in2)
        case ns: NoSuccess => (errorToken(ns.msg), ns.next, skip(ns.next))
      }
    case ns: NoSuccess => (errorToken(ns.msg), ns.next, skip(ns.next))
  }

  private def skip(in: Reader[Char]) = if (in.atEnd) in else in.rest

  override def source: java.lang.CharSequence = in.source

  override def offset: Int = in.offset

  def first = tok

  def rest = tok match {
    case ScriptEnd() => new JbjInitialLexer(rest2)
    case _ => new JbjScriptLexer(rest2)
  }

  def pos = rest1.pos

  def atEnd = in.atEnd || (whitespace(in) match {
    case Success(_, in1) => in1.atEnd
    case _ => false
  })

  private def token: Parser[Token] =
    ('?' ~ '>' ^^^ ScriptEnd()
      | '%' ~ '>' ^^^ ScriptEnd()
      | '<' ~ '/' ~ 's' ~ 'c' ~ 'r' ~ 'i' ~ 'p' ~ 't' ~ '>' ^^^ ScriptEnd()
      | '$' ~> identChar ~ rep(identChar | digit) ^^ {
      case first ~ rest => VarIdentifier(first :: rest mkString "")
    }
      | identChar ~ rep(identChar | digit) ^^ {
      case first ~ rest => processIdent(first :: rest mkString "")
    }
      | digit ~ rep(digit) ^^ {
      case first ~ rest => NumericLit(first :: rest mkString "")
    }
      | '\'' ~ notInterpolatedStr ~ '\'' ^^ {
      case '\'' ~ str ~ '\'' => StringLit(str)
    }
      | '\"' ~ interpolatedStr ~ '\"' ^^ {
      case '\"' ~ str ~ '\"' => InterpolatedStringLit(str)
    }
      | EofCh ^^^ EOF
      | '\'' ~> failure("unclosed string literal")
      | '\"' ~> failure("unclosed string literal")
      | delim
      | failure("illegal character")
      )

  private def interpolatedCharReplacements: Parser[Char] = '\\' ~> (
    '\\' ^^^ '\\' | '\"' ^^^ '\"' | '$' ^^^ '$' |
      'n' ^^^ '\n' | 'r' ^^^ '\r' | 't' ^^^ '\t' | 'v' ^^^ '\13' | 'f' ^^^ '\f' |
      digit ~ opt(digit) ~ opt(digit) ^^ {
        case d1 ~ optD2 ~ optD3 =>
          var d = Character.digit(d1, 8)
          optD2.foreach(d2 => d = 8 * d + Character.digit(d2, 8))
          optD3.foreach(d3 => d = 8 * d + Character.digit(d3, 8))
          d.toChar
      } |
      'x' ~> hexDigit ~ opt(hexDigit) ^^ {
        case d1 ~ optD2 =>
          var d = Character.digit(d1, 16)
          optD2.foreach(d2 => d = 16 * d + Character.digit(d2, 16))
          d.toChar
      }
    )

  private def strInterpolation: Parser[String] =
    '$' ~> '{' ~> rep(chrExcept('\"', '}', EofCh)) <~ '}' ^^ {
      chars => '$' :: chars mkString ""
    } | '{' ~> '$' ~> rep(chrExcept('\"', '}', EofCh)) <~ '}' ^^ {
      chars => '$' :: chars mkString ""
    } | '$' ~ identChar ~ rep(identChar | digit) ^^ {
      case start ~ first ~ rest => start :: first :: rest mkString ""
    }

  private def interpolatedChar: Parser[Either[Char, String]] = interpolatedCharReplacements ^^ (ch => Left(ch)) |
    strInterpolation ^^ (s => Right(s)) |
    chrExcept('\"', EofCh) ^^ (ch => Left(ch))

  private def notInterpolatedChar: Parser[Char] = '\\' ~> ('\\' ^^^ '\\' | '\'' ^^^ '\'') | chrExcept('\'', EofCh)

  private def notInterpolatedStr: Parser[String] = rep(notInterpolatedChar) ^^ {
    chars => chars.mkString("")
  }

  private def interpolatedStr: Parser[List[Either[Char, String]]] = rep(interpolatedChar)

  /** Returns the legal identifier chars, except digits. */
  private def identChar = letter | elem('_')

  // see `whitespace in `Scanners`
  private def whitespace: Parser[Any] = rep(
    whitespaceChar
      | '/' ~ '*' ~ comment
      | '/' ~ '/' ~ rep(chrExcept(EofCh, '\n'))
      | '/' ~ '*' ~ failure("unclosed comment")
  )

  /** A character-parser that matches a letter (and returns it). */
  private def letter = elem("letter", _.isLetter)

  /** A character-parser that matches a digit (and returns it). */
  private def digit = elem("digit", _.isDigit)

  private def hexDigit = elem("hexDigt", ch => ch.isDigit || (ch >= 'a' && ch <= 'f') || (ch >= 'A' && ch <= 'F'))

  /** A character-parser that matches any character except the ones given in `cs` (and returns it). */
  private def chrExcept(cs: Char*) = elem("", ch => cs.forall(ch != _))

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
}

object JbjScriptLexer {
  /** The set of reserved identifiers: these will be returned as `Keyword`s. */
  val reserved = Set("static", "global", "public", "private", "class", "var",
    "echo",
    "return", "break", "continue",
    "if", "else", "elseif", "while", "for", "foreach", "as",
    "switch", "case", "default",
    "function", "array")

  /** The set of delimiters (ordering does not matter). */
  val delimiters = Set(",", ":", ";", "{", "}", "[", "]","=>",
    ".", "+", "-", "*", "/", "(", ")",
    "=", ">", ">=", "<", "<=", "==",
    "|", "||", "&", "&&")
}
