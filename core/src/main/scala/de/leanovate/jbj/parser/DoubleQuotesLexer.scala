package de.leanovate.jbj.parser

import scala.util.parsing.input.{CharArrayReader, Reader}
import de.leanovate.jbj.parser.JbjTokens._
import scala.util.parsing.combinator.Parsers
import scala.util.parsing.input.CharArrayReader._

class DoubleQuotesLexer(in: Reader[Char]) extends Reader[Token] {

  import DoubleQuotesLexer.{Success, NoSuccess, token}

  def this(in: String) = this(new CharArrayReader(in.toCharArray))

  private val (tok: Token, mode: LexerMode, rest1: Reader[Char]) = token(in) match {
    case Success((t, m), i) => (t, m, i)
    case ns: NoSuccess => (errorToken(ns.msg), LexerMode.ERROR, ns.next)
  }

  def first = tok

  def rest = mode.newLexer(rest1, LexerMode.DOUBLE_QUOTES)

  def pos = in.pos

  def atEnd = in.atEnd
}

object DoubleQuotesLexer extends Parsers with CommonLexerPatterns {
  def token: Parser[(Token, LexerMode)] =
    '$' ~> rep1(identChar) <~ guard(str("->") ~ identChar) ^^ {
      name => Variable(name mkString "") -> LexerMode.LOOKING_FOR_PROPERTY
    } | doubleQuotedStr ^^ {
      str => EncapsAndWhitespace(str) -> LexerMode.DOUBLE_QUOTES
    } | '$' ~ '{' ^^^ (Keyword("${") -> LexerMode.DOUBLE_QUOTES) |
      '{' ~ '$' ^^^ (Keyword("{$") -> LexerMode.DOUBLE_QUOTES) |
      '$' ~> rep1(identChar) ^^ {
        name => Variable(name mkString "") -> LexerMode.DOUBLE_QUOTES
      } | '"' ^^^ Keyword("\"") -> LexerMode.IN_SCRIPTING

  private def strInterpolation: Parser[String] =
    '$' ~> '{' ~> rep(chrExcept('\"', '}', EofCh)) <~ '}' ^^ {
      chars => '$' :: chars mkString ""
    } | '{' ~> '$' ~> rep(chrExcept('\"', '}', EofCh)) <~ '}' ^^ {
      chars => '$' :: chars mkString ""
    } | '$' ~ identChar ~ rep(identChar | digit) ^^ {
      case start ~ first ~ rest => start :: first :: rest mkString ""
    }

  private def doubleQuotedChar: Parser[Char] = encapsCharReplacements | chrExcept('\"', '$', '{', EofCh) |
    '$' <~ not(identChar | '{') | '{' <~ not('$')

  private def doubleQuotedStr: Parser[String] = rep1(doubleQuotedChar) ^^ (_ mkString "")
}