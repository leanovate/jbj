/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                 (Tests based on PHP)   */

package de.leanovate.jbj.core.tests.lang

import org.specs2.mutable.SpecificationWithJUnit
import de.leanovate.jbj.core.tests.TestJbjExecutor

class FuncArgSpec extends SpecificationWithJUnit with TestJbjExecutor {
  "func get arg" should {
    "func_get_arg test" in {
      // lang/func_get_arg.001.phpt
      script(
        """<?php
          |
          |function foo($a)
          |{
          |   $a=5;
          |   echo func_get_arg(0);
          |}
          |foo(2);
          |echo "\n";
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """2
          |""".stripMargin
      )
    }

    "func_get_arg with variable number of args" in {
      // lang/func_get_arg.002.phpt
      script(
        """<?php
          |
          |function foo($a)
          |{
          |	$b = func_get_arg(1);
          |	var_dump($b);
          |	$b++;
          |	var_dump(func_get_arg(1));
          |
          |}
          |foo(2, 3);
          |echo "\n";
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """int(3)
          |int(3)
          |
          |""".stripMargin
      )
    }

    "func_get_arg outside of a function declaration" in {
      // lang/func_get_arg.003.phpt
      script(
        """<?php
          |
          |var_dump (func_get_arg(0));
          |
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """
          |Warning: func_get_arg():  Called from the global scope - no function context in /lang/FuncArgSpec.inlinePhp on line 3
          |bool(false)
          |""".stripMargin
      )
    }

    "func_get_arg on non-existent arg" in {
      // lang/func_get_arg.004.phpt
      script(
        """<?php
          |
          |function foo($a)
          |{
          |	var_dump(func_get_arg(2));
          |}
          |foo(2, 3);
          |echo "\n";
          |
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """
          |Warning: func_get_arg():  Argument 2 not passed to function in /lang/FuncArgSpec.inlinePhp on line 5
          |bool(false)
          |
          |""".stripMargin
      )
    }

    "A variable, which is referenced by another variable, is passed by value." in {
      // lang/func_get_arg.005.phpt
      script(
        """<?php
          |function refVal($x) {
          |	global $a;
          |	$a = 'changed.a';
          |	var_dump($x);
          |	var_dump(func_get_arg(0));
          |}
          |
          |$a = "original.a";
          |$ref =& $a;
          |refVal($a);
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """string(10) "original.a"
          |string(10) "original.a"
          |""".stripMargin
      )
    }
  }
}
