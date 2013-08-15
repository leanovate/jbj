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
            |error_reporting(1023);
            |
            |function print_stuff($stuff)
            |{
            |	print $stuff;
            |}
            |
            |
            |function still_working()
            |{
            |	return "I'm still alive";
            |}
            |
            |function dafna()
            |{
            |	static $foo = 0;
            |
            |	print "Dafna!\n";
            |	print call_user_func("still_working")."\n";
            |	$foo++;
            |	return (string) $foo;
            |}
            |
            |
            |class dafna_class {
            |	function dafna_class() {
            |		$this->myname = "Dafna";
            |	}
            |	function GetMyName() {
            |		return $this->myname;
            |	}
            |	function SetMyName($name) {
            |		$this->myname = $name;
            |	}
            |};
            |
            |for ($i=0; $i<200; $i++):
            |	print "$i\n";
            |	call_user_func("dafna");
            |	call_user_func("print_stuff","Hey there!!\n");
            |	print "$i\n";
            |endfor;
            |
            |
            |$dafna = new dafna_class();
            |
            |print $name=call_user_func(array(&$dafna,"GetMyName"));
            |print "\n";
            |
            |?>""".stripMargin)
  }
}
