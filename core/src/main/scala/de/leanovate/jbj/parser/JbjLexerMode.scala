package de.leanovate.jbj.parser

import scala.util.parsing.input.Reader
import de.leanovate.jbj.parser.JbjTokens.Token
import de.leanovate.jbj.runtime.exception.ParseJbjException

trait JbjLexerMode {
  def newLexer(fileName: String, in: Reader[Char]): Reader[Token]
}

object JbjLexerMode {
  val INITIAL = new JbjLexerMode {
    def newLexer(fileName: String, in: Reader[Char]) = new JbjInitialLexer(fileName, in)
  }

  val IN_SCRIPTING = new JbjLexerMode {
    def newLexer(fileName: String, in: Reader[Char]) = new JbjScriptLexer(fileName, in)
  }

  val DOUBLE_QUOTES = new JbjLexerMode {
    def newLexer(fileName: String, in: Reader[Char]) = new JbjDoubleQuotesLexer(fileName, in)
  }

  val LOOKING_FOR_VARNAME = new JbjLexerMode {
    def newLexer(fileName: String, in: Reader[Char]) = new JbjLookingForVarnameLexer(fileName, in)
  }

  val ERROR = new JbjLexerMode {
    def newLexer(fileName: String, in: Reader[Char]) = throw new ParseJbjException("Lexer in error mode")
  }
}
