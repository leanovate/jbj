/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.core.parser

import de.leanovate.jbj.core.parser.JbjTokens._
import scala.util.parsing.input.CharArrayReader._

case class DoubleQuotesLexer(mode: DoubleQuotedLexerMode) extends Lexer with CommonLexerPatterns {
  val token: Parser[(Token, Option[LexerMode])] =
    '$' ~> rep1(identChar) <~ guard(str("->") ~ identChar) ^^ {
      name => Variable(name mkString "") -> Some(LookingForPropertyLexerMode(mode))
    } | doubleQuotedStr ^^ {
      str => EncapsAndWhitespace(str) -> None
    } | '$' ~ '{' ^^^ (Keyword("${") -> None) |
      '{' ~ guard('$') ^^^ (Keyword("{$") -> Some(EncapsScriptingLexerMode(mode))) |
      '$' ~> rep1(identChar) ^^ {
        name => Variable(name mkString "") -> None
      } | '"' ^^^ Keyword("\"") -> Some(mode.prevMode)

  private def doubleQuotedChar: Parser[Char] = encapsCharReplacements | chrExcept('\"', '$', '{', EofCh) |
    '$' <~ not(identChar | '{') | '{' <~ not('$')

  private def doubleQuotedStr: Parser[String] = rep1(doubleQuotedChar) ^^ (_ mkString "")
}