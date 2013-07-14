package de.leanovate.jbj.parser

import scala.util.parsing.combinator.token.StdTokens

trait JbjTokens extends StdTokens {

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

}
