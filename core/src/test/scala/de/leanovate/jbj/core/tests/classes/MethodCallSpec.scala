/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                 (Tests based on PHP)   */

package de.leanovate.jbj.core.tests.classes

import de.leanovate.jbj.core.tests.TestJbjExecutor
import org.specs2.mutable.SpecificationWithJUnit

class MethodCallSpec extends SpecificationWithJUnit with TestJbjExecutor {
  "Method call" should {
    "In $a->$b[Y](), $b[Y] represents a method name on $a. But in $a->X[Y](), $a->X[Y] represents a global function name." in {
      // classes/method_call_variation_001
      script(
        """<?php
          |  class C
          |  {
          |      function foo($a, $b)
          |      {
          |          echo "Called C::foo($a, $b)\n";
          |      }
          |  }
          |
          |  $c = new C;
          |
          |  $functions[0] = 'foo';
          |  $functions[1][2][3][4] = 'foo';
          |
          |  $c->$functions[0](1, 2);
          |  $c->$functions[1][2][3][4](3, 4);
          |
          |
          |  function foo($a, $b)
          |  {
          |      echo "Called global foo($a, $b)\n";
          |  }
          |
          |  $c->functions[0] = 'foo';
          |  $c->functions[1][2][3][4] = 'foo';
          |
          |  $c->functions[0](5, 6);
          |  $c->functions[1][2][3][4](7, 8);
          |?>""".stripMargin
      ).result must haveOutput(
        """Called C::foo(1, 2)
          |Called C::foo(3, 4)
          |Called global foo(5, 6)
          |Called global foo(7, 8)
          |""".stripMargin
      )
    }
  }

}
