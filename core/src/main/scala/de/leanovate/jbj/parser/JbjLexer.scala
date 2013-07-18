package de.leanovate.jbj.parser

import scala.util.parsing.input
import input.CharArrayReader.EofCh
import scala.util.parsing.combinator.lexical.Lexical
import scala.util.parsing.input.{CharArrayReader, Reader}
import scala.collection.mutable

class JbjLexer extends Lexical with JbjTokens {
  /** The set of reserved identifiers: these will be returned as `Keyword`s. */
  val reserved = Set("static", "private", "class",
    "echo",
    "return", "break", "continue",
    "if", "else", "elseif", "while", "for",
    "switch", "case", "default",
    "function")

  /** The set of delimiters (ordering does not matter). */
  val delimiters = Set(".", "+", "-", "*", "/", "(", ")", ",", ":", ";", "{", "}", "=", ">", ">=", "<", "<=", "==")

  override def token: Parser[Token] =
    ('?' ~ '>' ^^^ ScriptEnd()
      | '%' ~ '>' ^^^ ScriptEnd()
      | '<' ~ '/' ~ 's' ~ 'c' ~ 'r' ~ 'i' ~ 'p' ~ 't' ~ '>' ^^^ ScriptEnd()
      | '$' ~> identChar ~ rep(identChar | digit) ^^ {
      case first ~ rest => VarIdentifier(first :: rest mkString "")
    }
      | identChar ~ rep(identChar | digit) ^^ {
      case first ~ rest => processIdent(first :: rest mkString "")
    }
      | digit ~ rep(digit) ^^ {
      case first ~ rest => NumericLit(first :: rest mkString "")
    }
      | '\'' ~ rep(chrExcept('\'', '\n', EofCh)) ~ '\'' ^^ {
      case '\'' ~ chars ~ '\'' => StringLit(chars mkString "")
    }
      | '\"' ~ rep(chrExcept('\"', '\n', EofCh)) ~ '\"' ^^ {
      case '\"' ~ chars ~ '\"' => StringLit(chars mkString "")
    }
      | EofCh ^^^ EOF
      | '\'' ~> failure("unclosed string literal")
      | '\"' ~> failure("unclosed string literal")
      | delim
      | failure("illegal character")
      )

  /** Returns the legal identifier chars, except digits. */
  def identChar = letter | elem('_')

  // see `whitespace in `Scanners`
  def whitespace: Parser[Any] = rep(
    whitespaceChar
      | '/' ~ '*' ~ comment
      | '/' ~ '/' ~ rep(chrExcept(EofCh, '\n'))
      | '/' ~ '*' ~ failure("unclosed comment")
  )

  protected def comment: Parser[Any] = (
    '*' ~ '/' ^^ {
      case _ => ' '
    }
      | chrExcept(EofCh) ~ comment
    )

  protected def processIdent(name: String) =
    if (reserved contains name) Keyword(name) else Identifier(name)

  private lazy val _delim: Parser[Token] = {
    // construct parser for delimiters by |'ing together the parsers for the individual delimiters,
    // starting with the longest one -- otherwise a delimiter D will never be matched if there is
    // another delimiter that is a prefix of D
    def parseDelim(s: String): Parser[Token] = accept(s.toList) ^^ {
      x => Keyword(s)
    }

    val d = new Array[String](delimiters.size)
    delimiters.copyToArray(d, 0)
    scala.util.Sorting.quickSort(d)
    (d.toList map parseDelim).foldRight(failure("no matching delimiter"): Parser[Token])((x, y) => y | x)
  }

  protected def delim: Parser[Token] = _delim

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
      chars => Inline(chars mkString "")
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
