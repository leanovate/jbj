package de.leanovate.jbj.tests

import scala.util.parsing.input.Reader
import de.leanovate.jbj.parser.JbjTokens.Token
import de.leanovate.jbj.parser.{JbjParser, ParseContext, InitialLexer, TokenReader}
import de.leanovate.jbj.ast.Prog
import de.leanovate.jbj.JbjEnv
import de.leanovate.jbj.runtime.Settings
import de.leanovate.jbj.runtime.env.CgiEnvironment

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

    val tokens2 = new TokenReader(exprstr, InitialLexer)
    val parser = new JbjParser(ParseContext("/classes/bla.php"))
    parser.phrase(parser.start)(tokens2) match {
      case parser.Success(tree: Prog, _) =>
        println("Tree")
        tree.dump(System.out, "")

        val jbj = JbjEnv(TestLocator)
        val context = jbj.newGlobalContext(System.out, System.err)

        context.settings.errorReporting = Settings.E_ALL
        CgiEnvironment.httpGet("?ab+cd+ef+123+test", context)
        tree.exec(context)
      case e: parser.NoSuccess =>
        println(e)
    }

  }

  //A main method for testing
  def main(args: Array[String]) {
    test( """<?php
            |class C {
            |    public static $x = 'C::$x';
            |    protected static $y = 'C::$y';
            |}
            |
            |$c = new C;
            |
            |echo "\n--> Access visible static prop like instance prop:\n";
            |var_dump(isset($c->x));
            |unset($c->x);
            |echo $c->x;
            |$c->x = 1;
            |$ref = 'ref';
            |$c->x =& $ref;
            |var_dump($c->x, C::$x);
            |
            |echo "\n--> Access non-visible static prop like instance prop:\n";
            |var_dump(isset($c->y));
            |//unset($c->y);		// Fatal error, tested in static_properties_003_error1.phpt
            |//echo $c->y;		// Fatal error, tested in static_properties_003_error2.phpt
            |//$c->y = 1;		// Fatal error, tested in static_properties_003_error3.phpt
            |//$c->y =& $ref;	// Fatal error, tested in static_properties_003_error4.phpt
            |?>""".stripMargin)
  }
}
