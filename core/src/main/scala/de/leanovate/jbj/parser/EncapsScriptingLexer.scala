package de.leanovate.jbj.parser

import scala.util.parsing.input.{CharArrayReader, Reader}
import de.leanovate.jbj.parser.JbjTokens._
import scala.util.parsing.combinator.Parsers

case class EncapsScriptingLexer(prevMode: LexerMode) extends Lexer {
  override def token: Parser[(Token, Option[LexerMode])] = ???
}
