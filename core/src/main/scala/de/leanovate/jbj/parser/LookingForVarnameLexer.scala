package de.leanovate.jbj.parser

import de.leanovate.jbj.parser.JbjTokens._

class LookingForVarnameLexer(prevMode: LexerMode) extends Lexer {
  override def token: Parser[(Token, Option[LexerMode])] = ???
}