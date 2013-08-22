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

    val jbj = JbjEnv(TestLocator)
    val tokens2 = new TokenReader(exprstr, InitialLexer)
    val parser = new JbjParser(ParseContext("/classes/bla.php", jbj.settings))
    parser.phrase(parser.start)(tokens2) match {
      case parser.Success(tree: Prog, _) =>
        println("Tree")
        val pp = new scala.xml.PrettyPrinter(80, 2)
        println(pp.format(tree.toXml))

        implicit val context = jbj.newGlobalContext(System.out, System.err)

        context.settings.errorReporting = Settings.E_ALL
        CgiEnvironment.httpGet("?ab+cd+ef+123+test")
        tree.exec(context)
      case e: parser.NoSuccess =>
        println(e)
    }

  }

  //A main method for testing
  def main(args: Array[String]) {
    test(
      """<?php
        |
        |Class C {
        |	function f() {
        |		static $a = array(1,2,3);
        |		eval(' static $k = array(4,5,6); ');
        |
        |		function cfg() {
        |			static $a = array(7,8,9);
        |			eval(' static $k = array(10,11,12); ');
        |			var_dump($a, $k);
        |		}
        |		var_dump($a, $k);
        |	}
        |}
        |$c = new C;
        |$c->f();
        |cfg();
        |
        |Class D {
        |	static function f() {
        |		eval('function dfg() { static $b = array(1,2,3); var_dump($b); } ');
        |	}
        |}
        |D::f();
        |dfg();
        |
        |eval(' Class E { function f() { static $c = array(1,2,3); var_dump($c); } }');
        |$e = new E;
        |$e->f();
        |
        |?>""".stripMargin)
  }
}
