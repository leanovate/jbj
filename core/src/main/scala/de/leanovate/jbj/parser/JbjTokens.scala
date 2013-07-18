package de.leanovate.jbj.parser

object JbjTokens {

  /** Objects of this type are produced by a lexical parser or ``scanner'', and consumed by a parser.
    *
    * @see [[scala.util.parsing.combinator.syntactical.TokenParsers]]
    */
  abstract class Token {
    def chars: String
  }

  /** A class of error tokens. Error tokens are used to communicate
    * errors detected during lexical analysis
    */
  case class ErrorToken(msg: String) extends Token {
    def chars = "*** error: " + msg
  }

  /** A class for end-of-file tokens */
  case object EOF extends Token {
    def chars = "<eof>"
  }

  case class Keyword(chars: String) extends Token {
    override def toString = "`" + chars + "'"
  }

  case class NumericLit(chars: String) extends Token {
    override def toString = chars
  }

  case class StringLit(chars: String) extends Token {
    override def toString = "\"" + chars + "\""
  }

  case class InterpolatedStringLit(charOrInterpolations: List[Either[Char, String]]) extends Token {
    override def chars = charOrInterpolations.map {
      case Left(ch) => ch.toString
      case Right(s) => s
    }.mkString("")

    override def toString = "\"" + chars + "\""
  }

  case class Identifier(chars: String) extends Token {
    override def toString = "identifier " + chars
  }

  case class Inline(chars: String) extends Token {
    override def toString = "\"" + chars + "\""
  }

  case class ScriptStart() extends Token {
    def chars = "<?php>"
  }

  case class ScriptStartEcho() extends Token {
    def chars = "<?="
  }

  case class ScriptEnd() extends Token {
    def chars = "?>"
  }

  case class VarIdentifier(name: String) extends Token {
    def chars = "$" + name

    override def toString = "variable identifier " + name
  }

  /** This token is produced by a scanner `Scanner` when scanning failed. */
  def errorToken(msg: String): Token = new ErrorToken(msg)
}
