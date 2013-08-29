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
        |  class X
        |  {
        |      // Static and instance array using class constants
        |      public static $sa_x = array(B::KEY => B::VALUE);
        |      public $a_x = array(B::KEY => B::VALUE);
        |  }
        |
        |  class B
        |  {
        |      const KEY = "key";
        |      const VALUE = "value";
        |
        |      // Static and instance array using class constants with self
        |      public static $sa_b = array(self::KEY => self::VALUE);
        |      public $a_b = array(self::KEY => self::VALUE);
        |  }
        |
        |  class C extends B
        |  {
        |      // Static and instance array using class constants with parent
        |      public static $sa_c_parent = array(parent::KEY => parent::VALUE);
        |      public $a_c_parent = array(parent::KEY => parent::VALUE);
        |
        |      // Static and instance array using class constants with self (constants should be inherited)
        |      public static $sa_c_self = array(self::KEY => self::VALUE);
        |      public $a_c_self = array(self::KEY => self::VALUE);
        |
        |      // Should also include inherited properties from B.
        |  }
        |
        |  echo "\nStatic properties:\n";
        |  var_dump(X::$sa_x, B::$sa_b, C::$sa_b, C::$sa_c_parent, C::$sa_c_self);
        |
        |  echo "\nInstance properties:\n";
        |  $x = new x;
        |  $b = new B;
        |  $c = new C;
        |  var_dump($x, $b, $c);
        |?>""".stripMargin)
  }
}
