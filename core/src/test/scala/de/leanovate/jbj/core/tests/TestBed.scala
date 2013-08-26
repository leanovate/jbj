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
        val pp = new scala.xml.PrettyPrinter(80, 2)
        println(pp.format(tree.toXml))

        implicit val context = jbj.newGlobalContext(System.out)

        context.settings.setErrorReporting(JbjSettings.E_ALL)
        tree.exec(context)
        context.cleanup()
      case e: parser.NoSuccess =>
        println(e)
    }

  }

  //A main method for testing
  def main(args: Array[String]) {
    test(
      """"<?php
        |
        |// This test checks for:
        |// - inherited constructors/destructors are not called automatically
        |// - base classes know about derived properties in constructor/destructor
        |// - base class constructors/destructors know the instanciated class name
        |
        |class base {
        |	public $name;
        |
        |	function __construct() {
        |		echo __CLASS__ . "::" . __FUNCTION__ . "\n";
        |		$this->name = 'base';
        |		print_r($this);
        |	}
        |
        |	function __destruct() {
        |		echo __CLASS__ . "::" . __FUNCTION__ . "\n";
        |		print_r($this);
        |	}
        |}
        |
        |class derived extends base {
        |	public $other;
        |
        |	function __construct() {
        |		$this->name = 'init';
        |		$this->other = 'other';
        |		print_r($this);
        |		parent::__construct();
        |		echo __CLASS__ . "::" . __FUNCTION__ . "\n";
        |		$this->name = 'derived';
        |		print_r($this);
        |	}
        |
        |	function __destruct() {
        |		parent::__destruct();
        |		echo __CLASS__ . "::" . __FUNCTION__ . "\n";
        |		print_r($this);
        |	}
        |}
        |
        |echo "Testing class base\n";
        |$t = new base();
        |unset($t);
        |echo "Testing class derived\n";
        |$t = new derived();
        |unset($t);
        |
        |echo "Done\n";
        |?>""".stripMargin)
  }
}
