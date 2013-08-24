package de.leanovate.jbj.core.parser

import scala.util.parsing.input.{CharArrayReader, Reader}
import de.leanovate.jbj.core.parser.JbjTokens._

class TokenReader(in: Reader[Char], lexer: Lexer) extends Reader[Token] {
  import lexer.{Success, NoSuccess, token, whitespace}

  def this(in: String, lexer:Lexer) = this(new CharArrayReader(in.toCharArray), lexer)

  private val (tok: Token, mode: Option[LexerMode], rest1: Reader[Char], rest2: Reader[Char]) = whitespace(in) match {
    case Success(_, in1) =>
      token(in1) match {
        case Success((token, m), in2) => (token, m, in1, in2)
        case ns: NoSuccess => (errorToken(ns.msg), None, ns.next, skip(ns.next))
      }
    case ns: NoSuccess => (errorToken(ns.msg), None, ns.next, skip(ns.next))
  }

  private def skip(in: Reader[Char]) = if (in.atEnd) in else in.rest

  override def source: java.lang.CharSequence = in.source

  override def offset: Int = in.offset

  def first = tok

  def rest = new TokenReader(rest2, mode.map(_.newLexer()).getOrElse(lexer))

  def pos = rest1.pos

  def atEnd = in.atEnd || (whitespace(in) match {
    case Success(_, in1) => in1.atEnd
    case _ => false
  })
}
