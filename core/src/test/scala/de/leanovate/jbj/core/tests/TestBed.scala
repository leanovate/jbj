/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                 (Tests based on PHP)   */

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
        |  set_error_handler('myErrorHandler', E_RECOVERABLE_ERROR);
        |  function myErrorHandler($errno, $errstr, $errfile, $errline) {
        |      echo "$errno: $errstr - $errfile($errline)\n";
        |      return true;
        |  }
        |
        |  echo "---> Type hints with callback function:\n";
        |  class A  {  }
        |  function f1(A $a)  {
        |      echo "in f1;\n";
        |  }
        |  function f2(A $a = null)  {
        |      echo "in f2;\n";
        |  }
        |  call_user_func('f1', 1);
        |  call_user_func('f1', new A);
        |  call_user_func('f2', 1);
        |  call_user_func('f2');
        |  call_user_func('f2', new A);
        |  call_user_func('f2', null);
        |
        |
        |  echo "\n\n---> Type hints with callback static method:\n";
        |  class C {
        |      static function f1(A $a) {
        |          if (isset($this)) {
        |              echo "in C::f1 (instance);\n";
        |          } else {
        |              echo "in C::f1 (static);\n";
        |          }
        |      }
        |      static function f2(A $a = null) {
        |          if (isset($this)) {
        |              echo "in C::f2 (instance);\n";
        |          } else {
        |              echo "in C::f2 (static);\n";
        |          }
        |      }
        |  }
        |  call_user_func(array('C', 'f1'), 1);
        |  call_user_func(array('C', 'f1'), new A);
        |  call_user_func(array('C', 'f2'), 1);
        |  call_user_func(array('C', 'f2'));
        |  call_user_func(array('C', 'f2'), new A);
        |  call_user_func(array('C', 'f2'), null);
        |
        |
        |  echo "\n\n---> Type hints with callback instance method:\n";
        |  class D {
        |      function f1(A $a) {
        |          if (isset($this)) {
        |              echo "in C::f1 (instance);\n";
        |          } else {
        |              echo "in C::f1 (static);\n";
        |          }
        |      }
        |      function f2(A $a = null) {
        |          if (isset($this)) {
        |              echo "in C::f2 (instance);\n";
        |          } else {
        |              echo "in C::f2 (static);\n";
        |          }
        |      }
        |  }
        |  $d = new D;
        |  call_user_func(array($d, 'f1'), 1);
        |  call_user_func(array($d, 'f1'), new A);
        |  call_user_func(array($d, 'f2'), 1);
        |  call_user_func(array($d, 'f2'));
        |  call_user_func(array($d, 'f2'), new A);
        |  call_user_func(array($d, 'f2'), null);
        |
        |?>""".stripMargin)
  }
}
