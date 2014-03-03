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
      name => Variable(name mkString "") -> mode.pushLookingForProperty()
    } | doubleQuotedStr ^^ {
      str => EncapsAndWhitespace(str) -> None
    } | '$' ~ '{' ^^^ (Keyword("${") -> None) |
      '{' ~ guard('$') ^^^ (Keyword("{$") -> mode.pushEncapsScripting()) |
      '$' ~> identChar ~ rep(identChar | digit) <~ guard('-' ~ '>' ~ identChar) ^^ {
        case first ~ rest => Variable(first :: rest mkString "") -> mode.pushLookingForProperty()
      } | '$' ~> identChar ~ rep(identChar | digit) <~ guard('[') ^^ {
      case first ~ rest => Variable(first :: rest mkString "") -> mode.pushVarOffset()
    } | '$' ~> identChar ~ rep(identChar | digit) ^^ {
      case first ~ rest => Variable(first :: rest mkString "") -> None
    } | '"' ^^^ Keyword("\"") -> mode.pop()

  private def doubleQuotedChar: Parser[Char] = encapsCharReplacements | chrExcept('\"', '$', '{', EofCh) |
    '$' <~ not(identChar | '{') | '{' <~ not('$')

  private def doubleQuotedStr: Parser[String] = rep1(doubleQuotedChar) ^^ (_ mkString "")
}