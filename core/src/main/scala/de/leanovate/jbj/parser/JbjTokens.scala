package de.leanovate.jbj.parser

import de.leanovate.jbj.ast.FilePosition

object JbjTokens {

  /** Objects of this type are produced by a lexical parser or ``scanner``, and consumed by a parser.

    * @see [[scala.util.parsing.combinator.syntactical.TokenParsers]]
    */
  abstract class Token {
    def position: FilePosition

    def chars: String
  }

  /** A class of error tokens. Error tokens are used to communicate
    * errors detected during lexical analysis
    */
  case class ErrorToken(position: FilePosition, msg: String) extends Token {
    def chars = "*** error: " + msg
  }

  /** A class for end-of-file tokens */
  case class EOF(position: FilePosition) extends Token {
    def chars = "<eof>"
  }

  case class Keyword(position: FilePosition, chars: String) extends Token {
    override def toString = "`" + chars + "'"
  }

  case class NumericLit(position: FilePosition, chars: String) extends Token {
    override def toString = chars
  }

  case class StringLit(position: FilePosition, chars: String) extends Token {
    override def toString = "\"" + chars + "\""
  }

  case class InterpolatedStringLit(position: FilePosition, charOrInterpolations: List[Either[Char, String]]) extends Token {
    override def chars = charOrInterpolations.map {
      case Left(ch) => ch.toString
      case Right(s) => s
    }.mkString("")

    override def toString = "\"" + chars + "\""
  }

  case class Identifier(position: FilePosition, chars: String) extends Token {
    override def toString = "identifier " + chars
  }

  case class Inline(position: FilePosition, chars: String) extends Token {
    override def toString = "\"" + chars + "\""
  }

  case class ScriptStart(position: FilePosition) extends Token {
    def chars = "<?php>"
  }

  case class ScriptStartEcho(position: FilePosition) extends Token {
    def chars = "<?="
  }

  case class ScriptEnd(position: FilePosition) extends Token {
    def chars = "?>"
  }

  case class VarIdentifier(position: FilePosition, name: String) extends Token {
    def chars = "$" + name

    override def toString = "variable identifier " + name
  }

  /** This token is produced by a scanner `Scanner` when scanning failed. */
  def errorToken(position: FilePosition, msg: String): Token = new ErrorToken(position, msg)
}
