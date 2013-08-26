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
        |class test {
        |  protected $x;
        |
        |  static private $test = NULL;
        |  static private $cnt = 0;
        |
        |  static function factory($x) {
        |    if (test::$test) {
        |      return test::$test;
        |    } else {
        |      test::$test = new test($x);
        |      return test::$test;
        |    }
        |  }
        |
        |  protected function __construct($x) {
        |    test::$cnt++;
        |    $this->x = $x;
        |  }
        |
        |  static function destroy() {
        |    test::$test = NULL;
        |  }
        |
        |  protected function __destruct() {
        |  	test::$cnt--;
        |  }
        |
        |  public function get() {
        |    return $this->x;
        |  }
        |
        |  static public function getX() {
        |    if (test::$test) {
        |      return test::$test->x;
        |    } else {
        |      return NULL;
        |    }
        |  }
        |
        |  static public function count() {
        |    return test::$cnt;
        |  }
        |}
        |
        |echo "Access static members\n";
        |var_dump(test::getX());
        |var_dump(test::count());
        |
        |echo "Create x and y\n";
        |$x = test::factory(1);
        |$y = test::factory(2);
        |var_dump(test::getX());
        |var_dump(test::count());
        |var_dump($x->get());
        |var_dump($y->get());
        |
        |echo "Destruct x\n";
        |$x = NULL;
        |var_dump(test::getX());
        |var_dump(test::count());
        |var_dump($y->get());
        |
        |echo "Destruct y\n";
        |$y = NULL;
        |var_dump(test::getX());
        |var_dump(test::count());
        |
        |echo "Destruct static\n";
        |test::destroy();
        |var_dump(test::getX());
        |var_dump(test::count());
        |
        |echo "Done\n";
        |?>""".stripMargin)
  }
}
