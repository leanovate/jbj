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
        tree.dump(System.out, "")

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
    test( """<?php
            |class Test
            |{
            |	protected $x;
            |
            |	function __get($name) {
            |		echo __METHOD__ . "\n";
            |		if (isset($this->x[$name])) {
            |			return $this->x[$name];
            |		}
            |		else
            |		{
            |			return NULL;
            |		}
            |	}
            |
            |	function __set($name, $val) {
            |		echo __METHOD__ . "\n";
            |		$this->x[$name] = $val;
            |	}
            |}
            |
            |class AutoGen
            |{
            |	protected $x;
            |
            |	function __get($name) {
            |		echo __METHOD__ . "\n";
            |		if (!isset($this->x[$name])) {
            |			$this->x[$name] = new Test();
            |		}
            |		return $this->x[$name];
            |	}
            |
            |	function __set($name, $val) {
            |		echo __METHOD__ . "\n";
            |		$this->x[$name] = $val;
            |	}
            |}
            |
            |$foo = new AutoGen();
            |$foo->bar->baz = "Check";
            |
            |var_dump($foo->bar);
            |var_dump($foo->bar->baz);
            |
            |?>
            |===DONE===""".stripMargin)
  }
}
