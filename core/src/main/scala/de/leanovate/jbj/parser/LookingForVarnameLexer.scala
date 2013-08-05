package de.leanovate.jbj.parser

import scala.util.parsing.input.{CharArrayReader, Reader}
import de.leanovate.jbj.parser.JbjTokens._
import scala.util.parsing.combinator.Parsers

class LookingForVarnameLexer(in: Reader[Char], prevMode:LexerMode) extends Reader[Token] {

  import LookingForVarnameLexer.{Success, NoSuccess, token}

  private val (tok: Token, mode: LexerMode, rest1: Reader[Char]) = token(in) match {
    case Success((t, m), i) => (t, m, i)
    case ns: NoSuccess => (errorToken(ns.msg), ErrorLexerMode, ns.next)
  }

  def first = tok

  def rest = mode.newLexer(rest1)

  def pos = in.pos

  def atEnd = in.atEnd

}

object LookingForVarnameLexer extends Parsers with CommonLexerPatterns {

  private def token: Parser[(Token, LexerMode)] = ???
}