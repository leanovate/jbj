package de.leanovate.jbj.parser

import scala.util.parsing.combinator.Parsers

trait CommonLexerPatterns extends Lexer {
  def str(str: String): Parser[String] = accept(str.toList) ^^ (_ mkString "")

  /** A character-parser that matches any character except the ones given in `cs` (and returns it). */
  def chrExcept(cs: Char*) = elem("", ch => cs.forall(ch.!=))

  /** A character-parser that matches a letter (and returns it). */
  def letter = elem("letter", _.isLetter)

  /** A character-parser that matches a digit (and returns it). */
  def digit = elem("digit", _.isDigit)

  def hexDigit = elem("hexDigit", ch => ch.isDigit || (ch >= 'a' && ch <= 'f') || (ch >= 'A' && ch <= 'F'))

  /** Returns the legal identifier chars, except digits. */
  def identChar = letter | elem('_')

  def encapsCharReplacements: Parser[Char] = '\\' ~> (
    '\\' ^^^ '\\' | '\"' ^^^ '\"' | '$' ^^^ '$' |
      'n' ^^^ '\n' | 'r' ^^^ '\r' | 't' ^^^ '\t' | 'v' ^^^ '\13' | 'f' ^^^ '\f' | 'e' ^^^ '\33' |
      digit ~ opt(digit) ~ opt(digit) ^^ {
        case d1 ~ optD2 ~ optD3 =>
          var d = Character.digit(d1, 8)
          optD2.foreach(d2 => d = 8 * d + Character.digit(d2, 8))
          optD3.foreach(d3 => d = 8 * d + Character.digit(d3, 8))
          d.toChar
      } |
      'x' ~> hexDigit ~ opt(hexDigit) ^^ {
        case d1 ~ optD2 =>
          var d = Character.digit(d1, 16)
          optD2.foreach(d2 => d = 16 * d + Character.digit(d2, 16))
          d.toChar
      }
    )

  def newLine: Parser[String] = opt('\r') ~ '\n' ^^ {
    case Some(_) ~ _ => "\r\n"
    case None ~ _ => "\n"
  }
}
