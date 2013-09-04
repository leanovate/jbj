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
        |/* Part 4:
        | * Storing a reference to a new instance (that's where the name of the  test
        | * comes from). First there is the global counter $oop_global again which
        | * counts the calls to the constructor of oop_class and hence counts the
        | * creation of oop_class instances.
        | * The class oop_test uses a static reference to a oop_class instance.
        | * When another oop_test instance is created it must reuse the statically
        | * stored reference oop_value. This way oop_class gets some singleton behavior
        | * since it will be created only once for all insatnces of oop_test.
        | */
        |$oop_global = 0;
        |class oop_class {
        |	var $oop_name;
        |
        |	function oop_class() {
        |		global $oop_global;
        |		echo "oop_class()\n";
        |		$this->oop_name = 'oop:' . ++$oop_global;
        |	}
        |}
        |
        |class oop_test {
        |	static $oop_value;
        |
        |	function oop_test() {
        |		echo "oop_test()\n";
        |	}
        |
        |	function oop_static() {
        |		echo "oop_static()\n";
        |		if (!isset(self::$oop_value)) {
        |			self::$oop_value = & new oop_class;
        |		}
        |		echo self::$oop_value->oop_name;
        |	}
        |}
        |
        |$oop_tester = new oop_test;
        |print $oop_tester->oop_static()."\n";
        |?>""".stripMargin)
  }
}
