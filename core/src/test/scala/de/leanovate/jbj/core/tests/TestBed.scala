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
        println( AstAsXmlNodeVisitor.dump(tree))

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
      """<?php
        |class Base_php4 {
        |  function Base_php4() {
        |    var_dump('Base constructor');
        |  }
        |}
        |
        |class Child_php4 extends Base_php4 {
        |  function Child_php4() {
        |    var_dump('Child constructor');
        |    parent::Base_php4();
        |  }
        |}
        |
        |class Base_php5 {
        |  function __construct() {
        |    var_dump('Base constructor');
        |  }
        |  }
        |
        |class Child_php5 extends Base_php5 {
        |  function __construct() {
        |    var_dump('Child constructor');
        |    parent::__construct();
        |  }
        |  }
        |
        |class Child_mx1 extends Base_php4 {
        |  function __construct() {
        |    var_dump('Child constructor');
        |    parent::Base_php4();
        |  }
        |}
        |
        |class Child_mx2 extends Base_php5 {
        |  function Child_mx2() {
        |    var_dump('Child constructor');
        |    parent::__construct();
        |  }
        |}
        |
        |echo "### PHP 4 style\n";
        |$c4= new Child_php4();
        |
        |echo "### PHP 5 style\n";
        |$c5= new Child_php5();
        |
        |echo "### Mixed style 1\n";
        |$cm= new Child_mx1();
        |
        |echo "### Mixed style 2\n";
        |$cm= new Child_mx2();
        |?>""".stripMargin)
  }
}
