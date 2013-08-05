package de.leanovate.jbj.parser

import scala.util.parsing.input.Reader
import de.leanovate.jbj.parser.JbjTokens._
import scala.util.parsing.combinator.Parsers
import scala.util.parsing.input.CharArrayReader._
import de.leanovate.jbj.parser.JbjTokens.Variable
import de.leanovate.jbj.parser.JbjTokens.EncapsAndWhitespace
import de.leanovate.jbj.parser.JbjTokens.Keyword

class HereDocLexer(in: Reader[Char], endMarker: String) extends Reader[Token] with Parsers with CommonLexerPatterns {
  private val (tok: Token, mode: LexerMode, rest1: Reader[Char]) = token(in) match {
    case Success((t, m), i) => (t, m, i)
    case ns: NoSuccess => (errorToken(ns.msg), ErrorLexerMode, ns.next)
  }

  def first = tok

  def rest = mode.newLexer(rest1)

  def pos = in.pos

  def atEnd = in.atEnd

  def token: Parser[(Token, LexerMode)] =
    '$' ~> rep1(identChar) <~ guard(str("->") ~ identChar) ^^ {
      name => Variable(name mkString "") -> LookingForPropertyLexerMode(HeredocLexerMode(endMarker))
    } | '$' ~ '{' ^^^ (Keyword("${") -> HeredocLexerMode(endMarker)) |
      '{' ~ '$' ^^^ (Keyword("{$") -> HeredocLexerMode(endMarker)) |
      '$' ~> rep1(identChar) ^^ {
        name => Variable(name mkString "") -> HeredocLexerMode(endMarker)
      } | newLine ~> str(endMarker) ^^ {
      s => HereDocEnd(s) -> ScriptingLexerMode
    } | hereDocStr ^^ {
      str => EncapsAndWhitespace(str) -> HeredocLexerMode(endMarker)
    }

  private def hereDocChar: Parser[Char] = encapsCharReplacements |
    chrExcept('\n', '$', '{', EofCh) | '\n' <~ not(str(endMarker)) | '$' <~ not(identChar | '{') | '{' <~ not('$')

  private def hereDocStr: Parser[String] = rep1(hereDocChar) ^^ (_ mkString "")

  private def newLine: Parser[String] = opt('\r') ~ '\n' ^^ {
    case Some(_) ~ _ => "\r\n"
    case None ~ _ => "\n"
  }
}
