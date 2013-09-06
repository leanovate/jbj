/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                 (Tests based on PHP)   */

package de.leanovate.jbj.core.tests

import scala.util.parsing.input.Reader
import de.leanovate.jbj.core.parser.JbjTokens.Token
import de.leanovate.jbj.core.parser.{JbjParser, ParseContext, InitialLexer, TokenReader}
import de.leanovate.jbj.core.ast.Prog
import de.leanovate.jbj.core.JbjEnv
import de.leanovate.jbj.api.JbjSettings

object TestBed {
  //Simplify testing
  def test(exprstr: String) = {
    var tokens: Reader[Token] = new TokenReader(exprstr, InitialLexer)

    println("Tokens")
    var count = 0
    while (!tokens.atEnd && count < 1000) {
      println(tokens.first)
      tokens = tokens.rest
      count += 1
    }

    val jbj = JbjEnv(TestLocator, errorStream = Some(System.err))
    val tokens2 = new TokenReader(exprstr, InitialLexer)
    val parser = new JbjParser(ParseContext("/classes/bla.php", jbj.settings))
    parser.phrase(parser.start)(tokens2) match {
      case parser.Success(tree: Prog, _) =>
        println("Tree")
        println(AstAsXmlNodeVisitor.dump(tree))

        implicit val context = jbj.newGlobalContext(System.out)

        context.settings.setErrorReporting(JbjSettings.E_ALL)
        try {
          tree.exec(context)
        } finally {
          context.cleanup()
        }
      case e: parser.NoSuccess =>
        println(e)
    }

  }

  //A main method for testing
  def main(args: Array[String]) {
    test(
      """<?php
        |
        |class ErrorCodes {
        |	const FATAL = "Fatal error\n";
        |	const WARNING = "Warning\n";
        |	const INFO = "Informational message\n";
        |
        |	static function print_fatal_error_codes() {
        |		echo "FATAL = " . FATAL . "\n";
        |		echo "self::FATAL = " . self::FATAL;
        |    }
        |}
        |
        |class ErrorCodesDerived extends ErrorCodes {
        |	const FATAL = "Worst error\n";
        |	static function print_fatal_error_codes() {
        |		echo "self::FATAL = " . self::FATAL;
        |		echo "parent::FATAL = " . parent::FATAL;
        |    }
        |}
        |
        |/* Call the static function and move into the ErrorCodes scope */
        |ErrorCodes::print_fatal_error_codes();
        |ErrorCodesDerived::print_fatal_error_codes();
        |
        |?>""".stripMargin)
  }
}
