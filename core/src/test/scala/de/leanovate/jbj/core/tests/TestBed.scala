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
        |class Test
        |{
        |	static function f1(array $ar)
        |	{
        |		echo __METHOD__ . "()\n";
        |		var_dump($ar);
        |	}
        |
        |	static function f2(array $ar = NULL)
        |	{
        |		echo __METHOD__ . "()\n";
        |		var_dump($ar);
        |	}
        |
        |	static function f3(array $ar = array())
        |	{
        |		echo __METHOD__ . "()\n";
        |		var_dump($ar);
        |	}
        |
        |	static function f4(array $ar = array(25))
        |	{
        |		echo __METHOD__ . "()\n";
        |		var_dump($ar);
        |	}
        |}
        |
        |Test::f1(array(42));
        |Test::f2(NULL);
        |Test::f2();
        |Test::f3();
        |Test::f4();
        |Test::f1(1);
        |
        |?>""".stripMargin)
  }
}
