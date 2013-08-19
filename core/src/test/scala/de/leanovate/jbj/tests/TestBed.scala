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
            |$b = "bb";
            |$a = "aa";
            |
            |function foo()
            |{
            |echo "Bad call\n";
            |}
            |
            |function baa()
            |{
            |echo "Good call\n";
            |}
            |
            |$bb = "baa";
            |
            |$aa = "foo";
            |
            |$c = ${$a=$b};
            |
            |$c();
            |
            |$a1 = array("dead","dead","dead");
            |$a2 = array("dead","dead","live");
            |$a3 = array("dead","dead","dead");
            |
            |$a = array($a1,$a2,$a3);
            |
            |function live()
            |{
            |echo "Good call\n";
            |}
            |
            |function dead()
            |{
            |echo "Bad call\n";
            |}
            |
            |$i = 0;
            |
            |$a[$i=1][++$i]();
            |
            |$a = -1;
            |
            |function foo1()
            |{
            |  global $a;
            |  return ++$a;
            |}
            |
            |$arr = array(array(0,0),0);
            |
            |$brr = array(0,0,array(0,0,0,5),0);
            |$crr = array(0,0,0,0,array(0,0,0,0,0,10),0,0);
            |
            |$arr[foo1()][foo1()] = $brr[foo1()][foo1()] +
            |                     $crr[foo1()][foo1()];
            |
            |$val = $arr[0][1];
            |echo "Expect 15 and get...$val\n";
            |
            |$x = array(array(0),0);
            |function mod($b)
            |{
            |global $x;
            |$x = $b;
            |return 0;
            |}
            |
            |$x1 = array(array(1),1);
            |$x2 = array(array(2),2);
            |$x3 = array(array(3),3);
            |$bx = array(10);
            |
            |$x[mod($x1)][mod($x2)] = $bx[mod($x3)];
            |
            |// expecting 10,3
            |
            |var_dump($x);
            |?>""".stripMargin)
  }
}
