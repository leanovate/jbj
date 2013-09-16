/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.core.parser

import de.leanovate.jbj.core.parser.JbjTokens._

class LookingForPropertyLexer(prevMode: LexerMode) extends Lexer with CommonLexerPatterns {
  override def token: Parser[(Token, Option[LexerMode])] =
    str("->") ^^^ Keyword("->") -> None |
      '$' ~> identChar ~ rep(identChar | digit) ^^ {
        case first ~ rest => Variable(first :: rest mkString "") -> Some(prevMode)
      } |
      '$' ^^^ Keyword("$") -> Some(prevMode) |
      identChar ~ rep(identChar | digit) ^^ {
        case first ~ rest => Identifier(first :: rest mkString "") -> Some(prevMode)
      }
}
