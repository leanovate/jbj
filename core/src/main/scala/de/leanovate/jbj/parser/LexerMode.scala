package de.leanovate.jbj.parser

import scala.util.parsing.input.Reader
import de.leanovate.jbj.parser.JbjTokens.Token
import de.leanovate.jbj.runtime.exception.ParseJbjException

sealed trait LexerMode {
  def newLexer(): Lexer
}

object InitialLexerMode extends LexerMode {
  def newLexer() =  InitialLexer
}

object ScriptingLexerMode extends LexerMode {
  def newLexer() =  ScriptLexer
}

object DoubleQuotedLexerMode extends LexerMode {
  def newLexer() =  DoubleQuotesLexer
}

case class HeredocLexerMode(endMarker: String) extends LexerMode {
  def newLexer() =  new HereDocLexer(endMarker)
}

case class EncapsScriptingLexerMode(prevMode: LexerMode) extends LexerMode {
  def newLexer() =  new EncapsScriptingLexer(prevMode)
}

case class LookingForPropertyLexerMode(prevMode: LexerMode) extends LexerMode {
  def newLexer() =  new LookingForPropertyLexer(prevMode)
}

case class LookingForVarnameLexerMode(prevMode: LexerMode) extends LexerMode {
  def newLexer() =  new LookingForVarnameLexer(prevMode)
}

object ErrorLexerMode extends LexerMode {
  def newLexer() = throw new ParseJbjException("Lexer in error mode")
}
