package de.leanovate.jbj.parser

import scala.util.parsing.input.{CharArrayReader, Reader}
import de.leanovate.jbj.parser.JbjTokens._
import scala.util.parsing.combinator.Parsers
import scala.util.parsing.input.CharArrayReader.EofCh
import de.leanovate.jbj.parser.JbjTokens.Inline
import de.leanovate.jbj.parser.JbjTokens.EOF

class JbjInitialLexer(in: Reader[Char]) extends Reader[Token] {

  import JbjInitialLexer.{Success, NoSuccess, token}

  def this(in: String) = this(new CharArrayReader(in.toCharArray))

  private val (tok: Token, mode: JbjLexerMode, rest1: Reader[Char]) = token(in) match {
    case Success((t, m), i) => (t, m, i)
    case ns: NoSuccess => (errorToken(ns.msg), JbjLexerMode.ERROR, ns.next)
  }

  def first = tok

  def rest = mode.newLexer(rest1)

  def pos = in.pos

  def atEnd = in.atEnd
}

object JbjInitialLexer extends Parsers with CommonLexerPatterns {
  def token: Parser[(Token, JbjLexerMode)] =
    (scriptStart ^^^ Inline("") -> JbjLexerMode.IN_SCRIPTING
      | str("<?php") ~ whitespaceChar ^^^ Inline("") -> JbjLexerMode.IN_SCRIPTING
      | str("<?=") ^^^ Keyword("echo") -> JbjLexerMode.IN_SCRIPTING
      | str("<?") ^^^ Inline("") -> JbjLexerMode.IN_SCRIPTING
      | str("<%=") ^^^ Keyword("echo") -> JbjLexerMode.IN_SCRIPTING
      | str("<%") ^^^ Inline("") -> JbjLexerMode.IN_SCRIPTING
      | '<' ^^^ Inline("<") -> JbjLexerMode.INITIAL
      | EofCh ^^^ EOF -> JbjLexerMode.INITIAL
      | rep(chrExcept(EofCh, '<')) ^^ {
      chars => Inline(chars mkString "") -> JbjLexerMode.INITIAL
    })

  private def scriptStart = str("<script") ~ optWhitespace ~ str("language") ~ optWhitespace ~ '=' ~
    optWhitespace ~ opt(quote) ~ str("php") ~ opt(quote) ~ optWhitespace ~ '>'

  private def whitespaceChar = elem("whitespace char", ch => ch == ' ' || ch == '\t' || ch == '\r' || ch == '\n')

  private def optWhitespace = opt(rep(whitespaceChar))

  private def quote = elem("quote", ch => ch == '"' || ch == '\'')
}