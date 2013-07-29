package de.leanovate.jbj.parser

import scala.util.parsing.input.{CharArrayReader, Reader}
import de.leanovate.jbj.parser.JbjTokens._
import scala.util.parsing.input.CharArrayReader.EofCh
import scala.util.parsing.combinator.Parsers
import de.leanovate.jbj.parser.JbjTokens.InterpolatedStringLit
import de.leanovate.jbj.parser.JbjTokens.Identifier
import de.leanovate.jbj.parser.JbjTokens.EOF
import de.leanovate.jbj.parser.JbjTokens.Keyword
import de.leanovate.jbj.parser.JbjTokens.StringLit
import de.leanovate.jbj.ast.FilePosition

class JbjScriptLexer(fileName: String, in: Reader[Char]) extends Reader[Token] with Parsers {
  type Elem = Char

  import JbjScriptLexer.{delimiters, reserved}

  def this(in: String) = this("-", new CharArrayReader(in.toCharArray))

  private var position = FilePosition(fileName, in.pos.line)

  private val (tok, scriptEnd, rest1, rest2) = whitespace(in) match {
    case Success(_, in1) =>
      position = FilePosition(fileName, in1.pos.line)
      token(in1) match {
        case Success((token, script), in2) => (token, script, in1, in2)
        case ns: NoSuccess => (errorToken(position, ns.msg), false, ns.next, skip(ns.next))
      }
    case ns: NoSuccess => (errorToken(position, ns.msg), false, ns.next, skip(ns.next))
  }

  private def skip(in: Reader[Char]) = if (in.atEnd) in else in.rest

  override def source: java.lang.CharSequence = in.source

  override def offset: Int = in.offset

  def first = tok

  def rest = if (scriptEnd) new JbjInitialLexer(fileName, rest2) else new JbjScriptLexer(fileName, rest2)

  def pos = rest1.pos

  def atEnd = in.atEnd || (whitespace(in) match {
    case Success(_, in1) => in1.atEnd
    case _ => false
  })

  private def token: Parser[(Token, Boolean)] =
    (str("?>") ^^^ Keyword(position, ";") -> true
      | str("%>") ^^^ Keyword(position, ";") -> true
      | str("</script") ~ rep(whitespaceChar) ~ '>' ~ opt('\n') ^^^ Keyword(position, ";") -> true
      | identChar ~ rep(identChar | digit) ^^ {
      case first ~ rest => processIdent(first :: rest mkString "") -> false
    } | rep(digit) ~ '.' ~ rep1(digit) ~ opt(exponent) ^^ {
      case first ~ dot ~ rest ~ exponent =>
        DoubleNumLit(position, first ++ (dot :: rest) ++ exponent.getOrElse(Nil) mkString "") -> false
    } | rep1(digit) ~ '.' ~ rep(digit) ~ opt(exponent) ^^ {
      case first ~ dot ~ rest ~ exponent =>
        DoubleNumLit(position, first ++ (dot :: rest) ++ exponent.getOrElse(Nil) mkString "") -> false
    } | digit ~ rep(digit) ~ exponent ^^ {
      case first ~ rest ~ exponent =>
        DoubleNumLit(position, (first :: rest) ++ exponent mkString "") -> false
    } | '0' ~ rep1(octDigit) ^^ {
      case first ~ rest => convertNum(position, first :: rest mkString "", 8) -> false
    } | '0' ~ 'b' ~ rep(binDigit) ^^ {
      case _ ~ _ ~ binary => convertNum(position, binary mkString "", 2) -> false
    } | '0' ~ 'x' ~ rep(hexDigit) ^^ {
      case _ ~ _ ~ hex => convertNum(position, hex mkString "", 16) -> false
    } | digit ~ rep(digit) ^^ {
      case first ~ rest => convertNum(position, first :: rest mkString "", 10) -> false
    } | '$' ~>  identChar ~ rep(identChar | digit) ^^ {
      case first ~ rest => Variable(position, first :: rest mkString "") -> false
    } | '\'' ~ notInterpolatedStr ~ '\'' ^^ {
      case '\'' ~ str ~ '\'' => StringLit(position, str) -> false
    } | '\"' ~ interpolatedStr ~ '\"' ^^ {
      case '\"' ~ str ~ '\"' => InterpolatedStringLit(position, str) -> false
    } | EofCh ^^^ EOF(position) -> false
      | '\'' ~> failure("unclosed string literal")
      | '\"' ~> failure("unclosed string literal")
      | delim ^^ {
      d => d -> false
    } | failure("illegal character")
      )

  private def exponent: Parser[List[Elem]] = exponentMarker ~ opt(sign) ~ rep1(digit) ^^ {
    case first ~ sign ~ exponent => first :: (sign.toList ++ exponent)
  }

  private def interpolatedCharReplacements: Parser[Char] = '\\' ~> (
    '\\' ^^^ '\\' | '\"' ^^^ '\"' | '$' ^^^ '$' |
      'n' ^^^ '\n' | 'r' ^^^ '\r' | 't' ^^^ '\t' | 'v' ^^^ '\13' | 'f' ^^^ '\f' | 'e' ^^^ '\33' |
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
      | '#' ~rep(chrExcept(EofCh, '\n'))
      | '/' ~ '*' ~ failure("unclosed comment")
  )

  /** A character-parser that matches a letter (and returns it). */
  private def letter = elem("letter", _.isLetter)

  /** A character-parser that matches a digit (and returns it). */
  private def digit = elem("digit", _.isDigit)

  private def exponentMarker = elem("exponent", ch => ch == 'e' || ch == 'E')

  private def sign = elem("sign", ch => ch == '-' || ch == '+')

  private def binDigit = elem("binDigit", ch => ch == '0' || ch == '1')

  private def octDigit = elem("octDigt", ch => ch >= '0' && ch <= '8')

  private def hexDigit = elem("hexDigt", ch => ch.isDigit || (ch >= 'a' && ch <= 'f') || (ch >= 'A' && ch <= 'F'))

  /** A character-parser that matches any character except the ones given in `cs` (and returns it). */
  private def chrExcept(cs: Char*) = elem("", ch => cs.forall(ch.!=))

  /** A character-parser that matches a white-space character (and returns it). */
  private def whitespaceChar = elem("space char", ch => ch <= ' ' && ch != EofCh)

  protected def comment: Parser[Any] =
    '*' ~ '/' ^^ {
      case _ => ' '
    } | chrExcept(EofCh) ~ comment

  protected def processIdent(name: String) =
    if (reserved contains name) Keyword(position, name) else Identifier(position, name)

  private lazy val _delim: Parser[Token] = {
    // construct parser for delimiters by |'ing together the parsers for the individual delimiters,
    // starting with the longest one -- otherwise a delimiter D will never be matched if there is
    // another delimiter that is a prefix of D
    def parseDelim(s: String): Parser[Token] = accept(s.toList) ^^ {
      x => Keyword(position, s)
    }

    val d = new Array[String](delimiters.size)
    delimiters.copyToArray(d, 0)
    scala.util.Sorting.quickSort(d)
    (d.toList map parseDelim).foldRight(failure("no matching delimiter"): Parser[Token])((x, y) => y | x)
  }

  private def delim: Parser[Token] = _delim

  private def str(str: String): Parser[Any] = accept(str.toList)

  private def convertNum(position: FilePosition, chars: String, radix: Int): Token = {
    try {
      LongNumLit(position, chars, java.lang.Long.valueOf(chars, radix))
    } catch {
      case _: NumberFormatException =>
        val bInt = BigInt(chars, radix)
        DoubleNumLit(position, chars, bInt.toDouble)
    }
  }
}

object JbjScriptLexer {
  /** The set of reserved identifiers: these will be returned as `Keyword`s. */
  val reserved = Set("static", "global", "public", "protected", "private", "var", "const",
    "class", "extends", "use", "interface", "trait", "implements", "abstract", "final",
    "exit", "die", "eval", "include", "include_once", "require", "require_once", "namespace",
    "echo", "print", "new", "clone",
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
    "||", "&&", "^", "or", "and", "xor")
}
