package de.leanovate.jbj.parser

import scala.util.parsing.input.{CharArrayReader, Reader}
import de.leanovate.jbj.parser.JbjTokens._
import scala.util.parsing.combinator.Parsers
import scala.util.parsing.input.CharArrayReader.EofCh
import de.leanovate.jbj.parser.JbjTokens.Inline
import de.leanovate.jbj.parser.JbjTokens.EOF

class InitialLexer(in: Reader[Char]) extends Reader[Token] {

  import InitialLexer.{Success, NoSuccess, token}

  def this(in: String) = this(new CharArrayReader(in.toCharArray))

  private val (tok: Token, mode: LexerMode, rest1: Reader[Char]) = token(in) match {
    case Success((t, m), i) => (t, m, i)
    case ns: NoSuccess => (errorToken(ns.msg), ErrorLexerMode, ns.next)
  }

  def first = tok

  def rest = mode.newLexer(rest1)

  def pos = in.pos

  def atEnd = in.atEnd
}

object InitialLexer extends Parsers with CommonLexerPatterns {
  def token: Parser[(Token, LexerMode)] =
    (scriptStart ^^^ Inline("") -> ScriptingLexerMode
      | str("<?php") ~ whitespaceChar ^^^ Inline("") -> ScriptingLexerMode
      | str("<?=") ^^^ Keyword("echo") -> ScriptingLexerMode
      | str("<?") ^^^ Inline("") -> ScriptingLexerMode
      | str("<%=") ^^^ Keyword("echo") -> ScriptingLexerMode
      | str("<%") ^^^ Inline("") -> ScriptingLexerMode
      | '<' ^^^ Inline("<") -> InitialLexerMode
      | EofCh ^^^ EOF -> InitialLexerMode
      | rep(chrExcept(EofCh, '<')) ^^ {
      chars => Inline(chars mkString "") -> InitialLexerMode
    })

  private def scriptStart = str("<script") ~ optWhitespace ~ str("language") ~ optWhitespace ~ '=' ~
    optWhitespace ~ opt(quote) ~ str("php") ~ opt(quote) ~ optWhitespace ~ '>'

  private def whitespaceChar = elem("whitespace char", ch => ch == ' ' || ch == '\t' || ch == '\r' || ch == '\n')

  private def optWhitespace = opt(rep(whitespaceChar))

  private def quote = elem("quote", ch => ch == '"' || ch == '\'')
}