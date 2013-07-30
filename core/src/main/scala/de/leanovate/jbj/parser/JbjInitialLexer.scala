package de.leanovate.jbj.parser

import scala.util.parsing.input.{CharArrayReader, Reader}
import de.leanovate.jbj.parser.JbjTokens._
import scala.util.parsing.combinator.Parsers
import scala.util.parsing.input.CharArrayReader.EofCh
import de.leanovate.jbj.parser.JbjTokens.Inline
import de.leanovate.jbj.parser.JbjTokens.EOF

class JbjInitialLexer(fileName: String, in: Reader[Char]) extends Reader[Token] with Parsers {
  type Elem = Char

  def this(in: String) = this("-", new CharArrayReader(in.toCharArray))

  private val (token, startScript, rest1) = inline(in) match {
    case Success((tok, script), i) => (tok, script, i)
    case ns: NoSuccess => (errorToken(ns.msg), false, ns.next)
  }

  def first = token

  def rest = if (startScript) new JbjScriptLexer(fileName, rest1) else new JbjInitialLexer(fileName, rest1)

  def pos = in.pos

  def atEnd = in.atEnd

  private def inline: Parser[(Token, Boolean)] =
    (scriptStart ^^^ Inline("") -> true
      | str("<?php") ~ witespaceChar ^^^ Inline("") -> true
      | str("<?=") ^^^ Keyword("echo") -> true
      | str("<?") ^^^ Inline("") -> true
      | str("<%=") ^^^ Keyword("echo") -> true
      | str("<%") ^^^ Inline("") -> true
      | '<' ^^^ Inline("<") -> false
      | EofCh ^^^ EOF -> false
      | rep(chrExcept(EofCh, '<')) ^^ {
      chars => Inline(chars mkString "") -> false
    })

  private def scriptStart = str("<script") ~ optWhitespace ~ str("language") ~ optWhitespace ~ '=' ~
    optWhitespace ~ opt(quote) ~ str("php") ~ opt(quote) ~ optWhitespace ~ '>'

  private def witespaceChar = elem("whitespace char", ch => ch == ' ' || ch == '\t' || ch == '\r' || ch == '\n')

  private def optWhitespace = opt(rep(witespaceChar))

  private def quote = elem("quote", ch => ch == '"' || ch == '\'')

  private def php = 'p' ~ 'h' ~ 'p'

  /** A character-parser that matches any character except the ones given in `cs` (and returns it). */
  private def chrExcept(cs: Char*) = elem("", ch => cs.forall(ch.!=))

  private def str(str: String): Parser[Any] = accept(str.toList)
}
