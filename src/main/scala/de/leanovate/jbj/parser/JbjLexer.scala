package de.leanovate.jbj.parser

import scala.util.parsing.input
import input.CharArrayReader.EofCh
import scala.util.parsing.combinator.lexical.StdLexical
import scala.util.parsing.input.{CharArrayReader, Reader}

class JbjLexer extends StdLexical with JbjTokens {
  override def token: Parser[Token] =
    ('?' ~ '>' ^^^ (ScriptEnd())
      | '%' ~ '>' ^^^ (ScriptEnd())
      | super.token
      )

  def inline: Parser[Token] =
    (inlineScriptStart ^^^ ScriptStart()
      | '<' ~ '?' ~ inlinePhp ~ inlineWhitespaceChar ^^^ ScriptStart()
      | '<' ~ '?' ~ '=' ~ inlineWhitespaceChar ^^^ ScriptStartEcho()
      | '<' ~ '?' ^^^ ScriptStart()
      | '<' ~ '%' ~ '=' ^^^ ScriptStartEcho()
      | '<' ~ '%' ^^^ ScriptStart()
      | '<' ^^^ Inline("<")
      | EofCh ^^^ EOF
      | rep(chrExcept(EofCh, '<')) ^^ {
      chars => Inline(chars.mkString)
    })

  def inlineScriptStart = inlineScript ~ inlineOptWhitespace ~ inlineLanguage ~ inlineOptWhitespace ~ '=' ~
    inlineOptWhitespace ~ opt(inlineQuote) ~ inlinePhp ~ opt(inlineQuote) ~ inlineOptWhitespace ~ '>'

  def inlineScript = '<' ~ 's' ~ 'c' ~ 'r' ~ 'i' ~ 'p' ~ 't'

  def inlineLanguage = 'l' ~ 'a' ~ 'n' ~ 'g' ~ 'u' ~ 'a' ~ 'g' ~ 'e'

  def inlineWhitespaceChar = elem("whitespace char", ch => ch == ' ' || ch == '\t' || ch == '\r' || ch == '\n')

  def inlineOptWhitespace = opt(rep(inlineWhitespaceChar))

  def inlineQuote = elem("quote", ch => ch == '"' || ch == '\'')

  def inlinePhp = 'p' ~ 'h' ~ 'p'

  class InitialScanner(in: Reader[Char]) extends Reader[Token] {
    def this(in: String) = this(new CharArrayReader(in.toCharArray))

    private val (tok, rest1) = inline(in) match {
      case Success(tok, in) => (tok, in)
      case ns: NoSuccess => (errorToken(ns.msg), ns.next)
    }

    def first = tok

    def rest = tok match {
      case ScriptStart() => new ScriptScanner(rest1)
      case ScriptStartEcho() => new ScriptScanner(rest1)
      case _ => new InitialScanner(rest1)
    }

    def pos = rest1.pos

    def atEnd = in.atEnd
  }

  class ScriptScanner(in: Reader[Char]) extends Reader[Token] {
    def this(in: String) = this(new CharArrayReader(in.toCharArray()))

    private val (tok, rest1, rest2) = whitespace(in) match {
      case Success(_, in1) =>
        token(in1) match {
          case Success(tok, in2) => (tok, in1, in2)
          case ns: NoSuccess => (errorToken(ns.msg), ns.next, skip(ns.next))
        }
      case ns: NoSuccess => (errorToken(ns.msg), ns.next, skip(ns.next))
    }

    private def skip(in: Reader[Char]) = if (in.atEnd) in else in.rest

    override def source: java.lang.CharSequence = in.source

    override def offset: Int = in.offset

    def first = tok

    def rest = tok match {
      case ScriptEnd() => new InitialScanner(rest2)
      case _ => new ScriptScanner(rest2)
    }

    def pos = rest1.pos

    def atEnd = in.atEnd || (whitespace(in) match {
      case Success(_, in1) => in1.atEnd
      case _ => false
    })
  }

}
