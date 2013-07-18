package de.leanovate.jbj.parser

import scala.util.parsing.combinator.token.Tokens

trait JbjTokens extends Tokens {


  case class Keyword(chars: String) extends Token {
    override def toString = "`"+chars+"'"
  }

  case class NumericLit(chars: String) extends Token {
    override def toString = chars
  }

  case class StringLit(chars: String) extends Token {
    override def toString = "\""+chars+"\""
  }

  case class Identifier(chars: String) extends Token {
    override def toString = "identifier "+chars
  }

  case class Inline(chars: String) extends Token {
    override def toString = "\"" + chars + "\""
  }

  case class ScriptStart() extends Token {
    def chars = "<?php>"
  }

  case class ScriptStartEcho() extends Token {
    def chars = "<?="
  }

  case class ScriptEnd() extends Token {
    def chars = "?>"
  }

  case class VarIdentifier(name: String) extends Token {
    def chars = "$" + name

    override def toString = "variable identifier " + name
  }
}
