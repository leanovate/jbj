/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.core.parser

import de.leanovate.jbj.core.parser.JbjTokens.{Identifier, Keyword, Variable}

class VarOffsetLexer(mode: VarOffsetLexerMode) extends Lexer with CommonScriptLexerPatterns {
  def token = '$' ~> identChar ~ rep(identChar | digit) ^^ {
    case first ~ rest => Variable(first :: rest mkString "") -> None
  } | identChar ~ rep(identChar | digit) ^^ {
    case first ~ rest => Identifier(first :: rest mkString "") -> None
  } | '0' ~ rep1(octDigit) ^^ {
    case first ~ rest => convertNum(first :: rest mkString "", 8) -> None
  } | '0' ~ 'b' ~ rep(binDigit) ^^ {
    case _ ~ _ ~ binary => convertNum(binary mkString "", 2) -> None
  } | '0' ~ 'x' ~ rep(hexDigit) ^^ {
    case _ ~ _ ~ hex => convertNum(hex mkString "", 16) -> None
  } | digit ~ rep(digit) ^^ {
    case first ~ rest => convertNum(first :: rest mkString "", 10) -> None
  } | ']' ^^^ {
    Keyword("]") -> mode.pop()
  } | delim ^^ {
    d => d -> None
  }
}
