package de.leanovate.jbj.core.parser

import de.leanovate.jbj.core.parser.JbjTokens._
import scala.util.parsing.input.CharArrayReader.EofCh
import de.leanovate.jbj.core.parser.JbjTokens.Inline
import de.leanovate.jbj.core.parser.JbjTokens.EOF

object InitialLexer extends Lexer with CommonLexerPatterns {
  val token: Parser[(Token, Option[LexerMode])] =
    (scriptStart ^^^ Inline("") -> Some(ScriptingLexerMode)
      | str("<?php") ~ whitespaceChar ^^^ Inline("") -> Some(ScriptingLexerMode)
      | str("<?=") ^^^ Keyword("echo") -> Some(ScriptingLexerMode)
      | str("<?") ^^^ Inline("") -> Some(ScriptingLexerMode)
      | str("<%=") ^^^ Keyword("echo") -> Some(ScriptingLexerMode)
      | str("<%") ^^^ Inline("") -> Some(ScriptingLexerMode)
      | '<' ^^^ Inline("<") -> None
      | EofCh ^^^ EOF -> None
      | rep(chrExcept(EofCh, '<')) ^^ {
      chars => Inline(chars mkString "") -> None
    })

  private def scriptStart = str("<script") ~ optWhitespace ~ str("language") ~ optWhitespace ~ '=' ~
    optWhitespace ~ opt(quote) ~ str("php") ~ opt(quote) ~ optWhitespace ~ '>'

  private def whitespaceChar = elem("whitespace char", ch => ch == ' ' || ch == '\t' || ch == '\r' || ch == '\n')

  private def optWhitespace = opt(rep(whitespaceChar))

  private def quote = elem("quote", ch => ch == '"' || ch == '\'')
}