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
import de.leanovate.jbj.core.parser.JbjTokens.Keyword
import de.leanovate.jbj.core.parser.JbjTokens.StringLit

case class ScriptLexer(mode: ScriptingLexerMode) extends Lexer with CommonScriptLexerPatterns {
  val token: Parser[(Token, Option[LexerMode])] =
    str("?>") <~ opt(newLine) ^^^ Keyword(";") -> Some(mode.prevMode) |
      str("%>") <~ opt(newLine) ^^^ Keyword(";") -> Some(mode.prevMode) |
      str("</script") ~ rep(whitespaceChar) ~ '>' ~ opt('\n') ^^^ Keyword(";") -> Some(mode.prevMode) |
      opt('b') ~> str("<<<") ~> '"' ~> rep1(chrExcept('\'', '\r', '\n', '"', EofCh)) <~ '"' <~ newLine ^^ {
        endMarker => HereDocStart(endMarker.mkString("")) -> Some(HeredocLexerMode(endMarker.mkString(""), mode))
      } |
      opt('b') ~> str("<<<") ~> rep1(chrExcept('\'', '\r', '\n', '"', EofCh)) <~ newLine ^^ {
        endMarker => HereDocStart(endMarker.mkString("")) -> Some(HeredocLexerMode(endMarker.mkString("").trim, mode))
      } |
      str("->") ^^^ Keyword("->") -> Some(LookingForPropertyLexerMode(mode)) |
      commonScriptToken ^^ {
        t => t -> None
      } | '\"' ^^^ Keyword("\"") -> Some(DoubleQuotedLexerMode(mode))

  def commonScriptToken: Parser[Token] =
    str("__HALT_COMPILER") ~> tabsOrSpaces ~> '(' ~> tabsOrSpaces ~> ')' ~> tabsOrSpaces ~> ';' ~> rep(chrExcept(EofCh)) ^^^ {
      Keyword("__HALT_COMPILER")
    } | identChar ~ rep(identChar | digit) ^^ {
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
    } | '\"' ~ doubleQuotedStr ~ '\"' ^^ {
      case '\"' ~ str ~ '\"' => str
    } | '(' ~> tabsOrSpaces ~> (str("int") | str("integer")) <~ tabsOrSpaces <~ ')' ^^ {
      s => IntegerCast(s)
    } | '(' ~> tabsOrSpaces ~> (str("real") | str("double") | str("float")) <~ tabsOrSpaces <~ ')' ^^ {
      s => DoubleCast(s)
    } | '(' ~> tabsOrSpaces ~> (str("string") | str("binary")) <~ tabsOrSpaces <~ ')' ^^ {
      s => StringCast(s)
    } | '(' ~> tabsOrSpaces ~> str("array") <~ tabsOrSpaces <~ ')' ^^ {
      s => ArrayCast(s)
    } | '(' ~> tabsOrSpaces ~> str("bool") <~ opt(str("ean")) <~ tabsOrSpaces <~ ')' ^^ {
      s => BooleanCast(s)
    } | '(' ~> tabsOrSpaces ~> str("unset") <~ tabsOrSpaces <~ ')' ^^ {
      s => UnsetCast(s)
    } | EofCh ^^^ EOF |
      '\'' ~> failure("unclosed string literal") |
      '\"' ~> failure("unclosed string literal") |
      delim ^^ {
        d => d
      } | failure("illegal character")

  private def doubleQuotedChar: Parser[Char] = encapsCharReplacements |
    '$' <~ not(identChar | '{') | '{' <~ not('$') | chrExcept('\"', '$', '{', EofCh)

  private def doubleQuotedStr: Parser[Token] = rep(doubleQuotedChar) ^^ {
    chs => StringLit(chs mkString "")
  }

  // see `whitespace in `Scanners`
  override val whitespace: Parser[Any] = rep(
    whitespaceChar
      | '/' ~ '*' ~ comment
      | '/' ~ '/' ~ rep(chrExcept(EofCh, '\n'))
      | '#' ~ rep(chrExcept(EofCh, '\n'))
      | '/' ~ '*' ~ failure("unclosed comment")
  )

  protected def comment: Parser[Any] = rep(chrExcept(EofCh, '*') | '*' ~ not('/')) ~ '*' ~ '/' ^^^ ' '

}
