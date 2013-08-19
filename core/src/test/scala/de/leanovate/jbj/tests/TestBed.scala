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
            |function f() {
            |	echo "in f()\n";
            |	return "name";
            |}
            |
            |function g() {
            |	echo "in g()\n";
            |	return "assigned value";
            |}
            |
            |
            |echo "\n\nOrder with local assignment:\n";
            |${f()} = g();
            |var_dump($name);
            |
            |echo "\n\nOrder with array assignment:\n";
            |$a[f()] = g();
            |var_dump($a);
            |
            |echo "\n\nOrder with object property assignment:\n";
            |$oa = new stdClass;
            |$oa->${f()} = g();
            |var_dump($oa);
            |
            |echo "\n\nOrder with nested object property assignment:\n";
            |$ob = new stdClass;
            |$ob->o1 = new stdClass;
            |$ob->o1->o2 = new stdClass;
            |$ob->o1->o2->${f()} = g();
            |var_dump($ob);
            |
            |echo "\n\nOrder with dim_list property assignment:\n";
            |$oc = new stdClass;
            |$oc->a[${f()}] = g();
            |var_dump($oc);
            |
            |
            |class C {
            |	public static $name = "original";
            |	public static $a = array();
            |	public static $string = "hello";
            |}
            |echo "\n\nOrder with static property assignment:\n";
            |C::${f()} = g();
            |var_dump(C::$name);
            |
            |echo "\n\nOrder with static array property assignment:\n";
            |C::$a[f()] = g();
            |var_dump(C::$a);
            |
            |echo "\n\nOrder with indexed string assignment:\n";
            |$string = "hello";
            |function getOffset() {
            |	echo "in getOffset()\n";
            |	return 0;
            |}
            |function newChar() {
            |	echo "in newChar()\n";
            |	return 'j';
            |}
            |$string[getOffset()] = newChar();
            |var_dump($string);
            |
            |echo "\n\nOrder with static string property assignment:\n";
            |C::$string[getOffset()] = newChar();
            |var_dump(C::$string);
            |
            |?>""".stripMargin)
  }
}
