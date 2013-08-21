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
            |        return 0;
            |}
            |
            |function i2() {
            |        echo "i2\n";
            |        return 0;
            |}
            |
            |function i3() {
            |        echo "i3\n";
            |        return 0;
            |}
            |
            |function i4() {
            |        echo "i4\n";
            |        return 0;
            |}
            |
            |function i5() {
            |        echo "i5\n";
            |        return 0;
            |}
            |
            |function i6() {
            |        echo "i6\n";
            |        return 0;
            |}
            |
            |$a = array(array(0));
            |$b = array(array(1));
            |$c = array(array(2));
            |
            |$a[i1()][i2()] = ($b[i3()][i4()] = $c[i5()][i6()]);
            |var_dump($a);
            |var_dump($b);
            |var_dump($c);
            |
            |$a[i1()][i2()] = $b[i3()][i4()] = -$c[i5()][i6()];
            |var_dump($a);
            |var_dump($b);
            |var_dump($c);
            |
            |$a[i1()][i2()] = -($b[i3()][i4()] = +($c[i5()][i6()]));
            |var_dump($a);
            |var_dump($b);
            |var_dump($c);
            |
            |
            |?>""".stripMargin)
  }
}
