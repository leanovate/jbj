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
        println(AstAsXmlNodeVisitor.dump(tree))

        implicit val context = jbj.newGlobalContext(System.out)

        context.settings.setErrorReporting(JbjSettings.E_ALL)
        try {
          tree.exec(context)
        } finally {
          context.cleanup()
        }
      case e: parser.NoSuccess =>
        println(e)
    }

  }

  //A main method for testing
  def main(args: Array[String]) {
    test(
      """<?php
        |  define('DEFINED', 1234);
        |  $def = 456;
        |  define('DEFINED_TO_VAR', $def);
        |  define('DEFINED_TO_UNDEF_VAR', $undef);
        |
        |  class C
        |  {
        |      const c0 = UNDEFINED;
        |
        |      const c1 = 1, c2 = 1.5;
        |      const c3 =  + 1, c4 =  + 1.5;
        |      const c5 = -1, c6 = -1.5;
        |
        |      const c7 = __LINE__;
        |      const c8 = __FILE__;
        |      const c9 = __CLASS__;
        |      const c10 = __METHOD__;
        |      const c11 = __FUNCTION__;
        |
        |      const c12 = DEFINED;
        |      const c13 = DEFINED_TO_VAR;
        |      const c14 = DEFINED_TO_UNDEF_VAR;
        |
        |      const c15 = "hello1";
        |      const c16 = 'hello2';
        |      const c17 = C::c16;
        |      const c18 = self::c17;
        |  }
        |
        |  echo "\nAttempt to access various kinds of class constants:\n";
        |  var_dump(C::c0);
        |  var_dump(C::c1);
        |  var_dump(C::c2);
        |  var_dump(C::c3);
        |  var_dump(C::c4);
        |  var_dump(C::c5);
        |  var_dump(C::c6);
        |  var_dump(C::c7);
        |  var_dump(C::c8);
        |  var_dump(C::c9);
        |  var_dump(C::c10);
        |  var_dump(C::c11);
        |  var_dump(C::c12);
        |  var_dump(C::c13);
        |  var_dump(C::c14);
        |  var_dump(C::c15);
        |  var_dump(C::c16);
        |  var_dump(C::c17);
        |  var_dump(C::c18);
        |
        |  echo "\nExpecting fatal error:\n";
        |  var_dump(C::c19);
        |
        |  echo "\nYou should not see this.";
        |?>""".stripMargin)
  }
}
