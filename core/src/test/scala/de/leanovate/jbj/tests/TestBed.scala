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
            |
            |echo "Simple test for standard compare object handler\n";
            |
            |class class1{}
            |
            |class class2{}
            |
            |class class3{
            |	public $aaa;
            |	private $bbb;
            |	protected $ccc;
            |}
            |
            |class class4 extends class3{
            |}
            |
            |class class5 extends class3{
            |	public $ddd;
            |	private $eee;
            |}
            |
            |// Define a bunch of objects all of which will use standard compare object handler
            |$obj1 = new class1();
            |$obj2 = new class2();
            |$obj3 = new class3();
            |$obj4 = new class4();
            |$obj5 = new class5();
            |
            |echo "\n-- The following compare should return TRUE --\n";
            |var_dump($obj1 == $obj1);
            |
            |echo "\n-- All the following compares should return FALSE --\n";
            |var_dump($obj1 == $obj2);
            |var_dump($obj1 == $obj3);
            |var_dump($obj1 == $obj4);
            |var_dump($obj1 == $obj5);
            |var_dump($obj4 == $obj3);
            |var_dump($obj5 == $obj3);
            |
            |?>
            |===DONE===""".stripMargin)
  }
}
