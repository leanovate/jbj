/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.core.parser

import de.leanovate.jbj.core.parser.JbjTokens._
import scala.util.parsing.input.CharArrayReader.EofCh
import de.leanovate.jbj.core.parser.JbjTokens.EOF
import de.leanovate.jbj.core.parser.JbjTokens.StringLit

case class EncapsScriptingLexer(mode: EncapsScriptingLexerMode) extends Lexer with CommonScriptLexerPatterns {
  override def token: Parser[(Token, Option[LexerMode])] =
    '}' ^^^ Keyword("}") -> mode.pop() | commonScriptToken ^^ {
      t => t -> None
    }

  def commonScriptToken: Parser[Token] =
    identChar ~ rep(identChar | digit) ^^ {
      case first ~ rest => processIdent(first :: rest mkString "")
    } | rep(digit) ~ '.' ~ rep1(digit) ~ opt(exponent) ^^ {
      case first ~ dot ~ rest ~ exponent =>
        DoubleNumLit(first ++ (dot :: rest) ++ exponent.getOrElse(Nil) mkString "")
    } | rep1(digit) ~ '.' ~ rep(digit) ~ opt(exponent) ^^ {
      case first ~ dot ~ rest ~ exponent =>
        DoubleNumLit(first ++ (dot :: rest) ++ exponent.getOrElse(Nil) mkString "")
    } | digit ~ rep(digit) ~ exponent ^^ {
      case first ~ rest ~ exponent =>
        DoubleNumLit((first :: rest) ++ exponent mkString "")
    } | '0' ~ rep1(octDigit) ^^ {
      case first ~ rest => convertNum(first :: rest mkString "", 8)
    } | '0' ~ 'b' ~ rep(binDigit) ^^ {
      case _ ~ _ ~ binary => convertNum(binary mkString "", 2)
    } | '0' ~ 'x' ~ rep(hexDigit) ^^ {
      case _ ~ _ ~ hex => convertNum(hex mkString "", 16)
    } | digit ~ rep(digit) ^^ {
      case first ~ rest => convertNum(first :: rest mkString "", 10)
    } | '$' ~> identChar ~ rep(identChar | digit) ^^ {
      case first ~ rest => Variable(first :: rest mkString "")
    } | '\'' ~ singleQuotedStr ~ '\'' ^^ {
      case '\'' ~ str ~ '\'' => StringLit(str)
    } | '(' ~> tabsOrSpaces ~> (str("int") | str("integer")) <~ tabsOrSpaces <~ ')' ^^ {
      s => IntegerCast(s)
    } | '(' ~> tabsOrSpaces ~> (str("real") | str("double") | str("float")) <~ tabsOrSpaces <~ ')' ^^ {
      s => DoubleCast(s)
    } | '(' ~> tabsOrSpaces ~> (str("string") | str("binary")) <~ tabsOrSpaces <~ ')' ^^ {
      s => StringCast(s)
    } | '(' ~> tabsOrSpaces ~> str("array") <~ tabsOrSpaces <~ ')' ^^ {
      s => ArrayCast(s)
    } | '(' ~> tabsOrSpaces ~> (str("bool") | str("boolean")) <~ tabsOrSpaces <~ ')' ^^ {
      s => BooleanCast(s)
    } | '(' ~> tabsOrSpaces ~> str("unset") <~ tabsOrSpaces <~ ')' ^^ {
      s => UnsetCast(s)
    } | EofCh ^^^ EOF  |
      '\'' ~> failure("unclosed string literal") |
      '\"' ~> failure("unclosed string literal") |
      delim ^^ {
        d => d
      } | failure("illegal character")
}
