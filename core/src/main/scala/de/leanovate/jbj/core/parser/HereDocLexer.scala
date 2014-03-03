/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.core.parser

import de.leanovate.jbj.core.parser.JbjTokens._
import scala.util.parsing.input.CharArrayReader._
import de.leanovate.jbj.core.parser.JbjTokens.Variable
import de.leanovate.jbj.core.parser.JbjTokens.EncapsAndWhitespace
import de.leanovate.jbj.core.parser.JbjTokens.Keyword

class HereDocLexer(mode: HeredocLexerMode) extends Lexer with CommonLexerPatterns {
  val token: Parser[(Token, Option[LexerMode])] =
    '$' ~> rep1(identChar) <~ guard(str("->") ~ identChar) ^^ {
      name => Variable(name mkString "") -> mode.pushLookingForProperty()
    } | '$' ~ '{' ^^^ (Keyword("${") -> None) |
      '{' ~ guard('$') ^^^ (Keyword("{$") -> mode.pushEncapsScripting()) |
      '$' ~> identChar ~ rep(identChar | digit) <~ guard('-' ~ '>' ~ identChar) ^^ {
        case first ~ rest => Variable(first :: rest mkString "") -> mode.pushLookingForProperty()
      } | '$' ~> identChar ~ rep(identChar | digit) <~ guard('[') ^^ {
      case first ~ rest => Variable(first :: rest mkString "") -> mode.pushVarOffset()
    } | '$' ~> identChar ~ rep(identChar | digit) ^^ {
      case first ~ rest => Variable(first :: rest mkString "") -> None
    } | opt(newLine) ~> str(mode.endMarker) <~ guard(';') ^^ {
      s => HereDocEnd(s) -> mode.pop()
    } | hereDocStr ^^ {
      str => EncapsAndWhitespace(str) -> None
    } | newLine ^^ {
      str => EncapsAndWhitespace(str) -> None
    }

  private def hereDocChar: Parser[Char] = encapsCharReplacements |
    chrExcept('\n', '$', '{', EofCh) | '\n' <~ not(str(mode.endMarker)) | '$' <~ not(identChar | '{') | '{' <~ not('$')

  private def hereDocStr: Parser[String] = rep1(hereDocChar) ^^ (_ mkString "")
}
