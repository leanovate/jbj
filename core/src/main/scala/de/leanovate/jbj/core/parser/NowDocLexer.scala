package de.leanovate.jbj.core.parser

import de.leanovate.jbj.core.parser.JbjTokens._
import scala.util.parsing.input.CharArrayReader._
import de.leanovate.jbj.core.parser.JbjTokens.EncapsAndWhitespace
import scala.Some
import de.leanovate.jbj.core.parser.JbjTokens.HereDocEnd

class NowDocLexer(mode: NowdocLexerMode) extends Lexer with CommonLexerPatterns {
  val token: Parser[(Token, Option[LexerMode])] =
    opt(newLine) ~> str(mode.endMarker) <~ guard(';') ^^ {
      s => HereDocEnd(s) -> Some(mode.prevMode)
    } | nowDocStr ^^ {
      str => EncapsAndWhitespace(str) -> None
    } | newLine ^^ {
      str => EncapsAndWhitespace(str) -> None
    }

  private def nowDocChar: Parser[Char] =
    chrExcept('\n', EofCh) | '\n' <~ not(str(mode.endMarker))

  private def nowDocStr: Parser[String] = rep1(nowDocChar) ^^ (_ mkString "")
}