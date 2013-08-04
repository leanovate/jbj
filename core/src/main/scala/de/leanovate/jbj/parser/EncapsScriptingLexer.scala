package de.leanovate.jbj.parser

import scala.util.parsing.input.{CharArrayReader, Reader}
import de.leanovate.jbj.parser.JbjTokens._
import scala.util.parsing.combinator.Parsers

case class EncapsScriptingLexer(in: Reader[Char]) extends Reader[Token] with Parsers {
  type Elem = Char

  def this(in: String) = this(new CharArrayReader(in.toCharArray))

  private val (tok, mode, rest1) = token(in) match {
    case Success((tok, m), i) => (tok, m, i)
    case ns: NoSuccess => (errorToken(ns.msg), LexerMode.ERROR, ns.next)
  }

  def first = tok

  def rest = mode.newLexer(rest1)

  def pos = in.pos

  def atEnd = in.atEnd

  private def token: Parser[(Token, LexerMode)] = ???
}
