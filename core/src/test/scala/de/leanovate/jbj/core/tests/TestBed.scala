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
        |class A {
        |	function __call($strMethod, $arrArgs) {
        |		var_dump($this);
        |		throw new Exception;
        |		echo "You should not see this";
        |	}
        |	function test() {
        |		A::unknownCalledWithSRO(1,2,3);
        |	}
        |}
        |
        |class B extends A {
        |	function test() {
        |		B::unknownCalledWithSROFromChild(1,2,3);
        |	}
        |}
        |
        |$a = new A();
        |
        |echo "---> Invoke __call via simple method call.\n";
        |try {
        |	$a->unknown();
        |} catch (Exception $e) {
        |	echo "Exception caught OK; continuing.\n";
        |}
        |
        |echo "\n\n---> Invoke __call via scope resolution operator within instance.\n";
        |try {
        |	$a->test();
        |} catch (Exception $e) {
        |	echo "Exception caught OK; continuing.\n";
        |}
        |
        |echo "\n\n---> Invoke __call via scope resolution operator within child instance.\n";
        |$b = new B();
        |try {
        |	$b->test();
        |} catch (Exception $e) {
        |	echo "Exception caught OK; continuing.\n";
        |}
        |
        |echo "\n\n---> Invoke __call via callback.\n";
        |try {
        |	call_user_func(array($b, 'unknownCallback'), 1,2,3);
        |} catch (Exception $e) {
        |	echo "Exception caught OK; continuing.\n";
        |}
        |?>
        |==DONE==""".stripMargin)
  }
}
