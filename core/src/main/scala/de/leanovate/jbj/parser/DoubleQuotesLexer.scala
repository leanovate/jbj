package de.leanovate.jbj.parser

import scala.util.parsing.input.{CharArrayReader, Reader}
import de.leanovate.jbj.parser.JbjTokens._
import scala.util.parsing.combinator.Parsers
import scala.util.parsing.input.CharArrayReader._
import de.leanovate.jbj.parser.DoubleQuotedLexerMode

class DoubleQuotesLexer(in: Reader[Char]) extends Reader[Token] {

  import DoubleQuotesLexer.{Success, NoSuccess, token}

  private val (tok: Token, mode: LexerMode, rest1: Reader[Char]) = token(in) match {
    case Success((t, m), i) => (t, m, i)
    case ns: NoSuccess => (errorToken(ns.msg), ErrorLexerMode, ns.next)
  }

  def first = tok

  def rest = mode.newLexer(rest1)

  def pos = in.pos

  def atEnd = in.atEnd
}

object DoubleQuotesLexer extends Parsers with CommonLexerPatterns {
  def token: Parser[(Token, LexerMode)] =
    '$' ~> rep1(identChar) <~ guard(str("->") ~ identChar) ^^ {
      name => Variable(name mkString "") -> LookingForPropertyLexerMode(DoubleQuotedLexerMode)
    } | doubleQuotedStr ^^ {
      str => EncapsAndWhitespace(str) ->DoubleQuotedLexerMode
    } | '$' ~ '{' ^^^ (Keyword("${") -> DoubleQuotedLexerMode) |
      '{' ~ '$' ^^^ (Keyword("{$") -> DoubleQuotedLexerMode) |
      '$' ~> rep1(identChar) ^^ {
        name => Variable(name mkString "") -> DoubleQuotedLexerMode
      } | '"' ^^^ Keyword("\"") -> ScriptingLexerMode

  private def doubleQuotedChar: Parser[Char] = encapsCharReplacements | chrExcept('\"', '$', '{', EofCh) |
    '$' <~ not(identChar | '{') | '{' <~ not('$')

  private def doubleQuotedStr: Parser[String] = rep1(doubleQuotedChar) ^^ (_ mkString "")
}