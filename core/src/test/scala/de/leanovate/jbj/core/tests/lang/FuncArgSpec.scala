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
  }
}
