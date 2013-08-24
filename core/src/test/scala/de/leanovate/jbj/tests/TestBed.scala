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
        context.cleanup()
      case e: parser.NoSuccess =>
        println(e)
    }

  }

  //A main method for testing
  def main(args: Array[String]) {
    test(
      """<?php
        |
        |function refs(&$ref1, &$ref2, &$ref3, &$ref4, &$ref5) {
        |  $ref1 = "Ref1 changed";
        |  $ref2 = "Ref2 changed";
        |  $ref3 = "Ref3 changed";
        |  $ref4 = "Ref4 changed";
        |  $ref5 = "Ref5 changed";
        |}
        |
        |
        |class C {
        |
        |	function __construct(&$ref1, &$ref2, &$ref3, &$ref4, &$ref5) {
        |	  $ref1 = "Ref1 changed";
        |	  $ref2 = "Ref2 changed";
        |	  $ref3 = "Ref3 changed";
        |	  $ref4 = "Ref4 changed";
        |	  $ref5 = "Ref5 changed";
        |	}
        |
        |	function refs(&$ref1, &$ref2, &$ref3, &$ref4, &$ref5) {
        |	  $ref1 = "Ref1 changed";
        |	  $ref2 = "Ref2 changed";
        |	  $ref3 = "Ref3 changed";
        |	  $ref4 = "Ref4 changed";
        |	  $ref5 = "Ref5 changed";
        |	}
        |
        |}
        |
        |echo "\n ---- Pass uninitialised array & object by ref: function call ---\n";
        |unset($u1, $u2, $u3, $u4, $u5);
        |refs($u1[0], $u2[0][1], $u3->a, $u4->a->b, $u5->a->b->c);
        |var_dump($u1, $u2, $u3, $u4, $u5);
        |
        |echo "\n ---- Pass uninitialised arrays & objects by ref: static method call ---\n";
        |unset($u1, $u2, $u3, $u4, $u5);
        |C::refs($u1[0], $u2[0][1], $u3->a, $u4->a->b, $u5->a->b->c);
        |var_dump($u1, $u2, $u3, $u4, $u5);
        |
        |echo "\n\n---- Pass uninitialised arrays & objects by ref: constructor ---\n";
        |unset($u1, $u2, $u3, $u4, $u5);
        |$c = new C($u1[0], $u2[0][1], $u3->a, $u4->a->b, $u5->a->b->c);
        |var_dump($u1, $u2, $u3, $u4, $u5);
        |
        |echo "\n ---- Pass uninitialised arrays & objects by ref: instance method call ---\n";
        |unset($u1, $u2, $u3, $u4, $u5);
        |$c->refs($u1[0], $u2[0][1], $u3->a, $u4->a->b, $u5->a->b->c);
        |var_dump($u1, $u2, $u3, $u4, $u5);
        |
        |?>""".stripMargin)
  }
}
