/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.core.parser

import de.leanovate.jbj.core.parser.JbjTokens._

class LookingForVarnameLexer(prevMode: LexerMode) extends Lexer {
  override def token: Parser[(Token, Option[LexerMode])] = ???
}