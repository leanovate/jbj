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
        |function handler($errno, $errstr, $errfile, $errline, $context)
        |{
        |	echo __FUNCTION__ . "($errstr)\n";
        |}
        |
        |set_error_handler('handler');
        |
        |function foo($x) {
        |	return "foo";
        |}
        |
        |$output = array();
        |++$output[foo("bar")];
        |
        |print_r($output);
        |
        |echo "Done";
        |?>""".stripMargin)
  }
}
