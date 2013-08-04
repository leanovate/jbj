package de.leanovate.jbj.parser

import scala.util.parsing.input.Reader
import de.leanovate.jbj.parser.JbjTokens.Token
import de.leanovate.jbj.runtime.exception.ParseJbjException

trait LexerMode {
  def newLexer(in: Reader[Char]): Reader[Token]
}

object LexerMode {
  val INITIAL = new LexerMode {
    def newLexer(in: Reader[Char]) = new InitialLexer(in)
  }

  val IN_SCRIPTING = new LexerMode {
    def newLexer(in: Reader[Char]) = new ScriptLexer(in)
  }

  val DOUBLE_QUOTES = new LexerMode {
    def newLexer(in: Reader[Char]) = new DoubleQuotesLexer(in)
  }

  val LOOKING_FOR_VARNAME = new LexerMode {
    def newLexer( in: Reader[Char]) = new LookingForVarnameLexer(in)
  }

  val ERROR = new LexerMode {
    def newLexer(in: Reader[Char]) = throw new ParseJbjException("Lexer in error mode")
  }
}
