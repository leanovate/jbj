package de.leanovate.jbj.parser

import scala.util.parsing.input.Reader
import de.leanovate.jbj.parser.JbjTokens.Token
import de.leanovate.jbj.runtime.exception.ParseJbjException

sealed trait LexerMode {
  def newLexer(in: Reader[Char]): Reader[Token]
}

object InitialLexerMode extends LexerMode {
  def newLexer(in: Reader[Char]) = new InitialLexer(in)
}

object ScriptingLexerMode extends LexerMode {
  def newLexer(in: Reader[Char]) = new ScriptLexer(in)
}

object DoubleQuotedLexerMode extends LexerMode {
  def newLexer(in: Reader[Char]) = new DoubleQuotesLexer(in)
}

case class HeredocLexerMode(endMarker:String) extends LexerMode {
  def newLexer(in: Reader[Char]) = new HereDocLexer(in, endMarker)
}

case class EncapsScriptingLexerMode(prevMode: LexerMode) extends LexerMode {
  def newLexer(in: Reader[Char]) = new EncapsScriptingLexer(in, prevMode)
}

case class LookingForPropertyLexerMode(prevMode: LexerMode) extends LexerMode {
  def newLexer(in: Reader[Char]) = new LookingForPropertyLexer(in, prevMode)
}

case class LookingForVarnameLexerMode(prevMode: LexerMode) extends LexerMode {
  def newLexer(in: Reader[Char]) = new LookingForVarnameLexer(in, prevMode)
}

object ErrorLexerMode extends LexerMode {
  def newLexer(in: Reader[Char]) = throw new ParseJbjException("Lexer in error mode")
}
