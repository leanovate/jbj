package de.leanovate.jbj.parser

import scala.util.parsing.input.Reader
import de.leanovate.jbj.parser.JbjTokens.Token
import de.leanovate.jbj.runtime.exception.ParseJbjException

trait LexerMode {
  def newLexer(in: Reader[Char], prevMode: LexerMode): Reader[Token]
}

object LexerMode {
  val INITIAL = new LexerMode {
    def newLexer(in: Reader[Char], prevMode: LexerMode) = new InitialLexer(in)
  }

  val IN_SCRIPTING = new LexerMode {
    def newLexer(in: Reader[Char], prevMode: LexerMode) = new ScriptLexer(in)
  }

  val DOUBLE_QUOTES = new LexerMode {
    def newLexer(in: Reader[Char], prevMode: LexerMode) = new DoubleQuotesLexer(in)
  }

  val ENCAPS_SCRIPTING = new LexerMode {
    def newLexer(in: Reader[Char], prevMode: LexerMode) = new EncapsScriptingLexer(in, prevMode)
  }

  val LOOKING_FOR_PROPERTY = new LexerMode {
    def newLexer(in: Reader[Char], prevMode: LexerMode) = new LookingForPropertyLexer(in, prevMode)
  }

  val LOOKING_FOR_VARNAME = new LexerMode {
    def newLexer(in: Reader[Char], prevMode: LexerMode) = new LookingForVarnameLexer(in, prevMode)
  }

  val ERROR = new LexerMode {
    def newLexer(in: Reader[Char], prevMode: LexerMode) = throw new ParseJbjException("Lexer in error mode")
  }
}
