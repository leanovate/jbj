package de.leanovate.jbj.parser

import scala.util.parsing.input.{CharArrayReader, Reader}
import de.leanovate.jbj.parser.JbjTokens._
import scala.util.parsing.input.CharArrayReader.EofCh
import scala.util.parsing.combinator.Parsers
import de.leanovate.jbj.parser.JbjTokens.Identifier
import de.leanovate.jbj.parser.JbjTokens.EOF
import de.leanovate.jbj.parser.JbjTokens.Keyword
import de.leanovate.jbj.parser.JbjTokens.StringLit

object ScriptLexer extends Lexer with CommonLexerPatterns {
  /** The set of reserved identifiers: these will be returned as `Keyword`s. */
  val reserved = Set("static", "global", "public", "protected", "private", "var", "const",
    "class", "extends", "use", "interface", "trait", "implements", "abstract", "final",
    "exit", "die", "eval", "include", "include_once", "require", "require_once", "namespace",
    "echo", "print", "new", "clone", "eval", "isset", "empty",
    "return", "break", "continue", "goto", "yield",
    "try", "catch", "finally", "throw",
    "if", "else", "elseif", "endif", "do", "while", "endwhile", "for", "endfor", "foreach", "as", "endforeach",
    "declare", "enddeclare", "instanceof",
    "switch", "case", "default", "endswitch",
    "function", "array", "list", "callable",
    "__FILE__", "__LINE__", "__FUNCTION__", "__CLASS__", "__METHOD__")

  /** The set of delimiters (ordering does not matter). */
  val delimiters = Set("$", ",", ":", "::", "?", "!", ";", "{", "}", "[", "]", "=>", "->",
    ".", "+", "-", "*", "/", "%", "(", ")", ".=", "+=", "-=", "*=", "/=", "%=", "--", "++",
    "<<", ">>", "^", "|", "&", "<<=", ">>=", "^=", "&=", "|=",
    "=", ">", ">=", "<", "<=", "==", "!=", "<>", "===", "!==",
    "||", "&&", "^", "or", "and", "xor", "\\")

  val token: Parser[(Token, Option[LexerMode])] =
    str("?>") <~ opt(newLine) ^^^ Keyword(";") -> Some(InitialLexerMode) |
      str("%>") <~ opt(newLine) ^^^ Keyword(";") -> Some(InitialLexerMode) |
      str("</script") ~ rep(whitespaceChar) ~ '>' ~ opt('\n') ^^^ Keyword(";") -> Some(InitialLexerMode) |
      str("<<<") ~> rep1(chrExcept('\'', '\r', '\n', EofCh)) <~ newLine ^^ {
        endMarker => HereDocStart(endMarker.mkString("")) -> Some(HeredocLexerMode(endMarker.mkString("")))
      } | commonScriptToken ^^ {
      t => t -> None
    } | '\"' ^^^ Keyword("\"") -> Some(DoubleQuotedLexerMode)

  def commonScriptToken: Parser[Token] =
    identChar ~ rep(identChar | digit) ^^ {
      case first ~ rest => processIdent(first :: rest mkString "")
    } | rep(digit) ~ '.' ~ rep1(digit) ~ opt(exponent) ^^ {
      case first ~ dot ~ rest ~ exponent =>
        DoubleNumLit(first ++ (dot :: rest) ++ exponent.getOrElse(Nil) mkString "")
    } | rep1(digit) ~ '.' ~ rep(digit) ~ opt(exponent) ^^ {
      case first ~ dot ~ rest ~ exponent =>
        DoubleNumLit(first ++ (dot :: rest) ++ exponent.getOrElse(Nil) mkString "")
    } | digit ~ rep(digit) ~ exponent ^^ {
      case first ~ rest ~ exponent =>
        DoubleNumLit((first :: rest) ++ exponent mkString "")
    } | '0' ~ rep1(octDigit) ^^ {
      case first ~ rest => convertNum(first :: rest mkString "", 8)
    } | '0' ~ 'b' ~ rep(binDigit) ^^ {
      case _ ~ _ ~ binary => convertNum(binary mkString "", 2)
    } | '0' ~ 'x' ~ rep(hexDigit) ^^ {
      case _ ~ _ ~ hex => convertNum(hex mkString "", 16)
    } | digit ~ rep(digit) ^^ {
      case first ~ rest => convertNum(first :: rest mkString "", 10)
    } | '$' ~> identChar ~ rep(identChar | digit) ^^ {
      case first ~ rest => Variable(first :: rest mkString "")
    } | '\'' ~ singleQuotedStr ~ '\'' ^^ {
      case '\'' ~ str ~ '\'' => StringLit(str)
    } | '\"' ~ doubleQuotedStr ~ '\"' ^^ {
      case '\"' ~ str ~ '\"' => str
    } | '(' ~> tabsOrSpaces ~> (str("int") | str("integer")) <~ tabsOrSpaces <~ ')' ^^ {
      s => IntegerCast(s)
    } | '(' ~> tabsOrSpaces ~> (str("real") | str("double") | str("float")) <~ tabsOrSpaces <~ ')' ^^ {
      s => DoubleCast(s)
    } | '(' ~> tabsOrSpaces ~> (str("string") | str("binary")) <~ tabsOrSpaces <~ ')' ^^ {
      s => StringCast(s)
    } | '(' ~> tabsOrSpaces ~> str("array") <~ tabsOrSpaces <~ ')' ^^ {
      s => ArrayCast(s)
    } | '(' ~> tabsOrSpaces ~> (str("bool") | str("boolean")) <~ tabsOrSpaces <~ ')' ^^ {
      s => BooleanCast(s)
    } | '(' ~> tabsOrSpaces ~> str("unset") <~ tabsOrSpaces <~ ')' ^^ {
      s => UnsetCast(s)
    } | EofCh ^^^ EOF |
      '\'' ~> failure("unclosed string literal") |
      '\"' ~> failure("unclosed string literal") |
      delim ^^ {
        d => d
      } | failure("illegal character")

  private def exponent: Parser[List[Elem]] = exponentMarker ~ opt(sign) ~ rep1(digit) ^^ {
    case first ~ sign ~ exponent => first :: (sign.toList ++ exponent)
  }

  private def strInterpolation: Parser[String] =
    '$' ~> '{' ~> rep(chrExcept('\"', '}', EofCh)) <~ '}' ^^ {
      chars => '$' :: chars mkString ""
    } | '{' ~> '$' ~> rep(chrExcept('\"', '}', EofCh)) <~ '}' ^^ {
      chars => '$' :: chars mkString ""
    } | '$' ~ identChar ~ rep(identChar | digit) ^^ {
      case start ~ first ~ rest => start :: first :: rest mkString ""
    }


  private def singleQuotedChar: Parser[Char] = '\\' ~> ('\\' ^^^ '\\' | '\'' ^^^ '\'') | chrExcept('\'', EofCh)

  private def singleQuotedStr: Parser[String] = rep(singleQuotedChar) ^^ {
    chars => chars.mkString("")
  }

  private def doubleQuotedChar: Parser[Char] = encapsCharReplacements |
    '$' <~ not(identChar | '{') | '{' <~ not('$') | chrExcept('\"', '$', '{', EofCh)

  private def doubleQuotedStr: Parser[Token] = rep(doubleQuotedChar) ^^ {
    chs => StringLit(chs mkString "")
  }

  // see `whitespace in `Scanners`
  override val whitespace: Parser[Any] = rep(
    whitespaceChar
      | '/' ~ '*' ~ comment
      | '/' ~ '/' ~ rep(chrExcept(EofCh, '\n'))
      | '#' ~ rep(chrExcept(EofCh, '\n'))
      | '/' ~ '*' ~ failure("unclosed comment")
  )

  private def tabsOrSpaces: Parser[Any] = rep(elem("space char", ch => ch == ' ' || ch == '\t'))

  private def exponentMarker = elem("exponent", ch => ch == 'e' || ch == 'E')

  private def sign = elem("sign", ch => ch == '-' || ch == '+')

  private def binDigit = elem("binDigit", ch => ch == '0' || ch == '1')

  private def octDigit = elem("octDigt", ch => ch >= '0' && ch <= '8')

  /** A character-parser that matches a white-space character (and returns it). */
  private def whitespaceChar = elem("space char", ch => ch <= ' ' && ch != EofCh)

  protected def comment: Parser[Any] =
    '*' ~ '/' ^^ {
      case _ => ' '
    } | chrExcept(EofCh) ~ comment

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

  private def delim: Parser[Token] = _delim

  private def convertNum(chars: String, radix: Int): Token = {
    try {
      LongNumLit(chars, java.lang.Long.valueOf(chars, radix))
    } catch {
      case _: NumberFormatException =>
        val bInt = BigInt(chars, radix)
        DoubleNumLit(chars, bInt.toDouble)
    }
  }
}
