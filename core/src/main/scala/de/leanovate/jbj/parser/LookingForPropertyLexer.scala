package de.leanovate.jbj.parser

import scala.util.parsing.input.Reader
import de.leanovate.jbj.parser.JbjTokens._
import scala.util.parsing.combinator.Parsers

class LookingForPropertyLexer(in: Reader[Char], prevMode: LexerMode) extends Reader[Token] with Parsers with CommonLexerPatterns {

  private val (tok: Token, mode: LexerMode, rest1: Reader[Char]) = token(in) match {
    case Success((t, m), i) => (t, m, i)
    case ns: NoSuccess => (errorToken(ns.msg), LexerMode.ERROR, ns.next)
  }

  def first = tok

  def rest = mode.newLexer(rest1, prevMode)

  def pos = in.pos

  def atEnd = in.atEnd

  private def token: Parser[(Token, LexerMode)] =
    str("->") ^^^ Keyword("->") -> LexerMode.LOOKING_FOR_PROPERTY |
      identChar ~ rep(identChar | digit) ^^ {
        case first ~ rest => Identifier(first :: rest mkString "") -> prevMode
      }
}
