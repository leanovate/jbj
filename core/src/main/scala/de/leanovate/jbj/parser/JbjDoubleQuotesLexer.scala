package de.leanovate.jbj.parser

import scala.util.parsing.input.{CharArrayReader, Reader}
import de.leanovate.jbj.parser.JbjTokens._
import scala.util.parsing.combinator.Parsers
import scala.util.parsing.input.CharArrayReader._

class JbjDoubleQuotesLexer(fileName: String, in: Reader[Char])
  extends Reader[Token] {

  import JbjDoubleQuotesLexer.{Success, NoSuccess, token}

  def this(in: String) = this("-", new CharArrayReader(in.toCharArray))

  private val (tok: Token, mode: JbjLexerMode, rest1: Reader[Char]) = token(in) match {
    case Success((t, m), i) => (t, m, i)
    case ns: NoSuccess => (errorToken(ns.msg), JbjLexerMode.ERROR, ns.next)
  }

  def first = tok

  def rest = mode.newLexer(fileName, rest1)

  def pos = in.pos

  def atEnd = in.atEnd

}

object JbjDoubleQuotesLexer extends Parsers with CommonLexerPatterns {
  def token: Parser[(Token, JbjLexerMode)] =
    doubleQuotedStr ^^ {
      str => EncapsAndWhitespace(str) -> JbjLexerMode.DOUBLE_QUOTES
    } | '$' ~ '{' ^^^ (Keyword("${") -> JbjLexerMode.DOUBLE_QUOTES) |
      '{' ~ '$' ^^^ (Keyword("{$") -> JbjLexerMode.DOUBLE_QUOTES) |
      '$' ~> rep1(identChar) ^^ {
        name => Variable(name mkString "") -> JbjLexerMode.LOOKING_FOR_VARNAME
      } | '"' ^^^ Keyword("\"") -> JbjLexerMode.IN_SCRIPTING

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

  private def doubleQuotedStr: Parser[String] = rep(doubleQuotedChar) ^^ (_ mkString "")
}