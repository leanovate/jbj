/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.core.parser

import scala.util.parsing.combinator.Parsers
import de.leanovate.jbj.core.parser.JbjTokens.Token

trait Lexer extends Parsers {
  type Elem = Char

  def token: Parser[(Token, Option[LexerMode])]

  val whitespace: Parser[Any] = success(Unit)
}
