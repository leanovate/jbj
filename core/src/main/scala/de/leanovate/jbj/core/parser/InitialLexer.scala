/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.core.parser

import de.leanovate.jbj.core.parser.JbjTokens._
import scala.util.parsing.input.CharArrayReader.EofCh
import de.leanovate.jbj.core.parser.JbjTokens.Inline
import de.leanovate.jbj.core.parser.JbjTokens.EOF

case class InitialLexer(mode: InitialLexerMode) extends Lexer with CommonLexerPatterns {
  val token: Parser[(Token, Option[LexerMode])] =
    scriptStart ^^^ Inline("") -> Some(ScriptingLexerMode(mode)) |
      str("<?php") ~ whitespaceChar ^^^ Inline("") -> Some(ScriptingLexerMode(mode)) |
      str("<?=") ^^^ Keyword("echo") -> Some(ScriptingLexerMode(mode)) |
      (if (mode.shortOpenTag)
        str("<?") ^^^ Inline("") -> Some(ScriptingLexerMode(mode))
      else
        failure("No short open tag")
        ) |
      (if (mode.aspTags)
        str("<%=") ^^^ Keyword("echo") -> Some(ScriptingLexerMode(mode))
          | str("<%") ^^^ Inline("") -> Some(ScriptingLexerMode(mode))
      else
        failure("No asp tags")
        ) |
      '<' ^^^ Inline("<") -> None |
      EofCh ^^^ EOF -> None |
      rep(chrExcept(EofCh, '<')) ^^ {
        chars => Inline(chars mkString "") -> None
      }

  private def scriptStart = str("<script") ~ optWhitespace ~ str("language") ~ optWhitespace ~ '=' ~
    optWhitespace ~ opt(quote) ~ str("php") ~ opt(quote) ~ optWhitespace ~ '>'

  private def whitespaceChar = elem("whitespace char", ch => ch == ' ' || ch == '\t' || ch == '\r' || ch == '\n')

  private def optWhitespace = opt(rep(whitespaceChar))

  private def quote = elem("quote", ch => ch == '"' || ch == '\'')
}