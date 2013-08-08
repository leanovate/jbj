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
