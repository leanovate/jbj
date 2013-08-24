package de.leanovate.jbj.core.parser

import de.leanovate.jbj.core.parser.JbjTokens._

class LookingForVarnameLexer(prevMode: LexerMode) extends Lexer {
  override def token: Parser[(Token, Option[LexerMode])] = ???
}