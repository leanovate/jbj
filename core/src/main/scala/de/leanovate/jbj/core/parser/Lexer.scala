package de.leanovate.jbj.core.parser

import scala.util.parsing.combinator.Parsers
import de.leanovate.jbj.core.parser.JbjTokens.Token

trait Lexer extends Parsers {
  type Elem = Char

  def token: Parser[(Token, Option[LexerMode])]

  val whitespace: Parser[Any] = success(' ')
}
