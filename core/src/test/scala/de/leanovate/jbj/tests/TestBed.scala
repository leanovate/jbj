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
        val pp = new scala.xml.PrettyPrinter(80, 2)
        println(pp.format(tree.toXml))

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
            |function i1() {
            |        echo "i1\n";
            |        return 1;
            |}
            |
            |function i2() {
            |        echo "i2\n";
            |        return 1;
            |}
            |
            |function i3() {
            |        echo "i3\n";
            |        return 3;
            |}
            |
            |function i4() {
            |        global $a;
            |        $a = array(10, 11, 12, 13, 14);
            |        echo "i4\n";
            |        return 4;
            |}
            |
            |$a = 0; // $a should not be indexable till the i4 has been executed
            |list($a[i1()+i2()], , list($a[i3()], $a[i4()]), $a[]) = array (0, 1, array(30, 40), 3, 4);
            |
            |var_dump($a);
            |
            |?>""".stripMargin)
  }
}
