package de.leanovate.jbj.core.parser

import de.leanovate.jbj.core.parser.JbjTokens._
import scala.util.parsing.input.CharArrayReader._
import de.leanovate.jbj.core.parser.JbjTokens.Variable
import de.leanovate.jbj.core.parser.JbjTokens.EncapsAndWhitespace
import de.leanovate.jbj.core.parser.JbjTokens.Keyword

class HereDocLexer(endMarker: String) extends Lexer with CommonLexerPatterns {
  val token: Parser[(Token, Option[LexerMode])] =
    '$' ~> rep1(identChar) <~ guard(str("->") ~ identChar) ^^ {
      name => Variable(name mkString "") -> Some(LookingForPropertyLexerMode(HeredocLexerMode(endMarker)))
    } | '$' ~ '{' ^^^ (Keyword("${") -> None) |
      '{' ~ '$' ^^^ (Keyword("{$") -> None) |
      '$' ~> rep1(identChar) ^^ {
        name => Variable(name mkString "") -> None
      } | newLine ~> str(endMarker) ^^ {
      s => HereDocEnd(s) -> Some(ScriptingLexerMode)
    } | hereDocStr ^^ {
      str => EncapsAndWhitespace(str) -> None
    }

  private def hereDocChar: Parser[Char] = encapsCharReplacements |
    chrExcept('\n', '$', '{', EofCh) | '\n' <~ not(str(endMarker)) | '$' <~ not(identChar | '{') | '{' <~ not('$')

  private def hereDocStr: Parser[String] = rep1(hereDocChar) ^^ (_ mkString "")
}
