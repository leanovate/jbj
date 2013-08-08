package de.leanovate.jbj.tests

import scala.util.parsing.input.Reader
import de.leanovate.jbj.parser.JbjTokens.Token
import de.leanovate.jbj.parser.{JbjParser, ParseContext, InitialLexer, TokenReader}
import de.leanovate.jbj.ast.{NodePosition, Prog}
import de.leanovate.jbj.JbjEnv
import de.leanovate.jbj.runtime.{Context, Settings}
import de.leanovate.jbj.runtime.env.CgiEnvironment
import de.leanovate.jbj.runtime.buildin.OutputFunctions

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
            |
            |static $a = array(7,8,9);
            |
            |function f1() {
            |	static $a = array(1,2,3);
            |
            |	function g1() {
            |		static $a = array(4,5,6);
            |		var_dump($a);
            |	}
            |
            |	var_dump($a);
            |
            |}
            |
            |f1();
            |g1();
            |var_dump($a);
            |
            |eval(' static $b = array(10,11,12); ');
            |
            |function f2() {
            |	eval(' static $b = array(1,2,3); ');
            |
            |	function g2a() {
            |		eval(' static $b = array(4,5,6); ');
            |		var_dump($b);
            |	}
            |
            |	eval('function g2b() { static $b = array(7, 8, 9); var_dump($b); } ');
            |	var_dump($b);
            |}
            |
            |f2();
            |g2a();
            |g2b();
            |var_dump($b);
            |
            |
            |eval(' function f3() { static $c = array(1,2,3); var_dump($c); }');
            |f3();
            |
            |?>""".stripMargin)
  }
}
