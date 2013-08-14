package de.leanovate.jbj.parser

import de.leanovate.jbj.parser.JbjTokens._
import de.leanovate.jbj.parser.JbjTokens.Keyword
import de.leanovate.jbj.parser.JbjTokens.Identifier
import de.leanovate.jbj.parser.JbjTokens.LongNumLit
import scala.util.parsing.input.CharArrayReader.EofCh

trait CommonScriptLexerPatterns extends CommonLexerPatterns {
  /** The set of reserved identifiers: these will be returned as `Keyword`s. */
  val reserved = Set("static", "global", "public", "protected", "private", "var", "const",
    "class", "extends", "use", "interface", "trait", "implements", "abstract", "final",
    "exit", "die", "eval", "include", "include_once", "require", "require_once", "namespace",
    "echo", "print", "new", "clone", "eval", "isset", "empty", "unset",
    "return", "break", "continue", "goto", "yield",
    "try", "catch", "finally", "throw",
    "if", "else", "elseif", "endif", "do", "while", "endwhile", "for", "endfor", "foreach", "as", "endforeach",
    "declare", "enddeclare", "instanceof",
    "switch", "case", "default", "endswitch",
    "function", "array", "list", "callable",
    "__FILE__", "__LINE__", "__FUNCTION__", "__CLASS__", "__METHOD__")

  /** The set of delimiters (ordering does not matter). */
  val delimiters = Set("$", ",", ":", "::", "?", "!", "~", ";", "{", "}", "[", "]", "=>", "->",
    ".", "+", "-", "*", "/", "%", "(", ")", ".=", "+=", "-=", "*=", "/=", "%=", "--", "++",
    "<<", ">>", "^", "|", "&", "<<=", ">>=", "^=", "&=", "|=",
    "=", ">", ">=", "<", "<=", "==", "!=", "<>", "===", "!==",
    "||", "&&", "^", "or", "and", "xor", "\\")

  def exponent: Parser[List[Elem]] = exponentMarker ~ opt(sign) ~ rep1(digit) ^^ {
    case first ~ sign ~ exponent => first :: (sign.toList ++ exponent)
  }

  def tabsOrSpaces: Parser[Any] = rep(elem("space char", ch => ch == ' ' || ch == '\t'))

  def exponentMarker = elem("exponent", ch => ch == 'e' || ch == 'E')

  def sign = elem("sign", ch => ch == '-' || ch == '+')

  def binDigit = elem("binDigit", ch => ch == '0' || ch == '1')

  def octDigit = elem("octDigt", ch => ch >= '0' && ch <= '8')

  /** A character-parser that matches a white-space character (and returns it). */
  def whitespaceChar = elem("space char", ch => ch <= ' ' && ch != EofCh)

  def singleQuotedChar: Parser[Char] = '\\' ~> ('\\' ^^^ '\\' | '\'' ^^^ '\'') | chrExcept('\'', EofCh)

  def singleQuotedStr: Parser[String] = rep(singleQuotedChar) ^^ {
    chars => chars.mkString("")
  }

  def processIdent(name: String) =
    if (reserved contains name) Keyword(name) else Identifier(name)

  def convertNum(chars: String, radix: Int): Token = {
    try {
      LongNumLit(chars, java.lang.Long.valueOf(chars, radix))
    } catch {
      case _: NumberFormatException =>
        val bInt = BigInt(chars, radix)
        DoubleNumLit(chars, bInt.toDouble)
    }
  }

  lazy val _delim: Parser[Token] = {
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

  def delim: Parser[Token] = _delim

}
