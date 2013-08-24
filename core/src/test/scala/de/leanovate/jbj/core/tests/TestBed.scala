package de.leanovate.jbj.core.tests

import scala.util.parsing.input.Reader
import de.leanovate.jbj.core.parser.JbjTokens.Token
import de.leanovate.jbj.core.parser.{JbjParser, ParseContext, InitialLexer, TokenReader}
import de.leanovate.jbj.core.ast.Prog
import de.leanovate.jbj.core.runtime.Settings
import de.leanovate.jbj.core.runtime.env.CgiEnvironment
import de.leanovate.jbj.core.JbjEnv

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
        |    public    static $b = 'foo';
        |}
        |
        |$classname       =  'A';
        |$wrongClassname  =  'B';
        |
        |echo $classname::$b."\n";
        |echo $wrongClassname::$b."\n";
        |
        |?>
        |===DONE===""".stripMargin)
  }
}
