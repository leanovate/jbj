/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                 (Tests based on PHP)   */

package de.leanovate.jbj.core.tests.lang

import de.leanovate.jbj.core.tests.TestJbjExecutor
import org.specs2.mutable.SpecificationWithJUnit

class StringDecimalsSpec extends SpecificationWithJUnit with TestJbjExecutor {
  "String decimals" should {
    "String conversion with multiple decimal points" in {
      // lang/string_decimals_001
      script(
        """<?php
          |function test($str) {
          |  echo "\n--> Testing $str:\n";
          |  var_dump((int)$str);
          |  var_dump((float)$str);
          |  var_dump($str > 0);
          |}
          |
          |test("..9");
          |test(".9.");
          |test("9..");
          |test("9.9.");
          |test("9.9.9");
          |?>
          |===DONE===""".stripMargin
      ).result must haveOutput(
        """
          |--> Testing ..9:
          |int(0)
          |float(0)
          |bool(false)
          |
          |--> Testing .9.:
          |int(0)
          |float(0.9)
          |bool(true)
          |
          |--> Testing 9..:
          |int(9)
          |float(9)
          |bool(true)
          |
          |--> Testing 9.9.:
          |int(9)
          |float(9.9)
          |bool(true)
          |
          |--> Testing 9.9.9:
          |int(9)
          |float(9.9)
          |bool(true)
          |===DONE===""".stripMargin
      )
    }
  }
}
