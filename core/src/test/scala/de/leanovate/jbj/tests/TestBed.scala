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
            |
            |function v($val) {
            |  $val = "Val changed";
            |}
            |
            |function r(&$ref) {
            |  $ref = "Ref changed";
            |}
            |
            |
            |function vv($val1, $val2) {
            |  $val1 = "Val1 changed";
            |  $val2 = "Val2 changed";
            |}
            |
            |function vr($val, &$ref) {
            |  $val = "Val changed";
            |  $ref = "Ref changed";
            |}
            |
            |function rv(&$ref, $val) {
            |  $val = "Val changed";
            |  $ref = "Ref changed";
            |}
            |
            |function rr(&$ref1, &$ref2) {
            |  $ref1 = "Ref1 changed";
            |  $ref2 = "Ref2 changed";
            |}
            |
            |
            |class C {
            |
            |	function __construct($val, &$ref) {
            |	  $val = "Val changed";
            |	  $ref = "Ref changed";
            |	}
            |
            |	function v($val) {
            |	  $val = "Val changed";
            |	}
            |
            |	function r(&$ref) {
            |	  $ref = "Ref changed";
            |	}
            |
            |	function vv($val1, $val2) {
            |	  $val1 = "Val1 changed";
            |	  $val2 = "Val2 changed";
            |	}
            |
            |	function vr($val, &$ref) {
            |	  $val = "Val changed";
            |	  $ref = "Ref changed";
            |	}
            |
            |	function rv(&$ref, $val) {
            |	  $val = "Val changed";
            |	  $ref = "Ref changed";
            |	}
            |
            |	function rr(&$ref1, &$ref2) {
            |	  $ref1 = "Ref1 changed";
            |	  $ref2 = "Ref2 changed";
            |	}
            |
            |}
            |
            |echo "\n ---- Pass by ref / pass by val: functions ----\n";
            |unset($u1, $u2);
            |v($u1);
            |r($u2);
            |var_dump($u1, $u2);
            |
            |unset($u1, $u2);
            |vv($u1, $u2);
            |var_dump($u1, $u2);
            |
            |unset($u1, $u2);
            |vr($u1, $u2);
            |var_dump($u1, $u2);
            |
            |unset($u1, $u2);
            |rv($u1, $u2);
            |var_dump($u1, $u2);
            |
            |unset($u1, $u2);
            |rr($u1, $u2);
            |var_dump($u1, $u2);
            |
            |
            |echo "\n\n ---- Pass by ref / pass by val: static method calls ----\n";
            |unset($u1, $u2);
            |C::v($u1);
            |C::r($u2);
            |var_dump($u1, $u2);
            |
            |unset($u1, $u2);
            |C::vv($u1, $u2);
            |var_dump($u1, $u2);
            |
            |unset($u1, $u2);
            |C::vr($u1, $u2);
            |var_dump($u1, $u2);
            |
            |unset($u1, $u2);
            |C::rv($u1, $u2);
            |var_dump($u1, $u2);
            |
            |unset($u1, $u2);
            |C::rr($u1, $u2);
            |var_dump($u1, $u2);
            |
            |echo "\n\n ---- Pass by ref / pass by val: instance method calls ----\n";
            |unset($u1, $u2);
            |$c = new C($u1, $u2);
            |var_dump($u1, $u2);
            |
            |unset($u1, $u2);
            |$c->v($u1);
            |$c->r($u2);
            |var_dump($u1, $u2);
            |
            |unset($u1, $u2);
            |$c->vv($u1, $u2);
            |var_dump($u1, $u2);
            |
            |unset($u1, $u2);
            |$c->vr($u1, $u2);
            |var_dump($u1, $u2);
            |
            |unset($u1, $u2);
            |$c->rv($u1, $u2);
            |var_dump($u1, $u2);
            |
            |unset($u1, $u2);
            |$c->rr($u1, $u2);
            |var_dump($u1, $u2);
            |
            |?>""".stripMargin)
  }
}
