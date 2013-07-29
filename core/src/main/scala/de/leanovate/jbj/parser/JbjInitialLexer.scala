package de.leanovate.jbj.parser

import scala.util.parsing.input.{CharArrayReader, Reader}
import de.leanovate.jbj.parser.JbjTokens._
import scala.util.parsing.combinator.Parsers
import scala.util.parsing.input.CharArrayReader.EofCh
import de.leanovate.jbj.parser.JbjTokens.Inline
import de.leanovate.jbj.parser.JbjTokens.EOF
import de.leanovate.jbj.ast.FilePosition

class JbjInitialLexer(fileName: String, in: Reader[Char]) extends Reader[Token] with Parsers {
  type Elem = Char

  def this(in: String) = this("-", new CharArrayReader(in.toCharArray))

  private val position = FilePosition(fileName, in.pos.line)

  private val (token, startScript, rest1) = inline(in) match {
    case Success((tok, script), i) => (tok, script, i)
    case ns: NoSuccess => (errorToken(position, ns.msg), false, ns.next)
  }

  def first = token

  def rest = if (startScript) new JbjScriptLexer(fileName, rest1) else new JbjInitialLexer(fileName, rest1)

  def pos = rest1.pos

  def atEnd = in.atEnd

  private def inline: Parser[(Token, Boolean)] =
    (scriptStart ^^^ Inline(position, "") -> true
      | str("<?php") ~ witespaceChar ^^^ Inline(position, "") -> true
      | str("<?=") ^^^ Keyword(position, "echo") -> true
      | str("<?") ^^^ Inline(position, "") -> true
      | str("<%=") ^^^ Keyword(position, "echo") -> true
      | str("<%") ^^^ Inline(position, "") -> true
      | '<' ^^^ Inline(position, "<") -> false
      | EofCh ^^^ EOF(position) -> false
      | rep(chrExcept(EofCh, '<')) ^^ {
      chars => Inline(position, chars mkString "") -> false
    })

  private def scriptStart = script ~ optWhitespace ~ language ~ optWhitespace ~ '=' ~
    optWhitespace ~ opt(quote) ~ php ~ opt(quote) ~ optWhitespace ~ '>'

  private def script = '<' ~ 's' ~ 'c' ~ 'r' ~ 'i' ~ 'p' ~ 't'

  private def language = 'l' ~ 'a' ~ 'n' ~ 'g' ~ 'u' ~ 'a' ~ 'g' ~ 'e'

  private def witespaceChar = elem("whitespace char", ch => ch == ' ' || ch == '\t' || ch == '\r' || ch == '\n')

  private def optWhitespace = opt(rep(witespaceChar))

  private def quote = elem("quote", ch => ch == '"' || ch == '\'')

  private def php = 'p' ~ 'h' ~ 'p'

  /** A character-parser that matches any character except the ones given in `cs` (and returns it). */
  private def chrExcept(cs: Char*) = elem("", ch => cs.forall(ch.!=))

  private def str(str: String): Parser[Any] = accept(str.toList)
}
