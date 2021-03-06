/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                 (Tests based on PHP)   */

package de.leanovate.jbj.core.tests.lang.operators

import de.leanovate.jbj.core.tests.TestJbjExecutor
import org.specs2.mutable.SpecificationWithJUnit

class NegateSpec extends SpecificationWithJUnit with TestJbjExecutor {
  "Negate" should {
    "Test -N operator : 64bit long tests" in {
      // lang/operators/negate_basiclong_64bit
      script(
        """<?php
          |
          |define("MAX_64Bit", 9223372036854775807);
          |define("MAX_32Bit", 2147483647);
          |define("MIN_64Bit", -9223372036854775807 - 1);
          |define("MIN_32Bit", -2147483647 - 1);
          |
          |$longVals = array(
          |    MAX_64Bit, MIN_64Bit, MAX_32Bit, MIN_32Bit, MAX_64Bit - MAX_32Bit, MIN_64Bit - MIN_32Bit,
          |    MAX_32Bit + 1, MIN_32Bit - 1, MAX_32Bit * 2, (MAX_32Bit * 2) + 1, (MAX_32Bit * 2) - 1,
          |    MAX_64Bit -1, MAX_64Bit + 1, MIN_64Bit + 1, MIN_64Bit - 1
          |);
          |
          |
          |foreach ($longVals as $longVal) {
          |   echo "--- testing: $longVal ---\n";
          |   var_dump(-$longVal);
          |}
          |
          |?>
          |===DONE===""".stripMargin
      ).result must haveOutput(
        """--- testing: 9223372036854775807 ---
          |int(-9223372036854775807)
          |--- testing: -9223372036854775808 ---
          |float(9.2233720368548E+18)
          |--- testing: 2147483647 ---
          |int(-2147483647)
          |--- testing: -2147483648 ---
          |int(2147483648)
          |--- testing: 9223372034707292160 ---
          |int(-9223372034707292160)
          |--- testing: -9223372034707292160 ---
          |int(9223372034707292160)
          |--- testing: 2147483648 ---
          |int(-2147483648)
          |--- testing: -2147483649 ---
          |int(2147483649)
          |--- testing: 4294967294 ---
          |int(-4294967294)
          |--- testing: 4294967295 ---
          |int(-4294967295)
          |--- testing: 4294967293 ---
          |int(-4294967293)
          |--- testing: 9223372036854775806 ---
          |int(-9223372036854775806)
          |--- testing: 9.2233720368548E+18 ---
          |float(-9.2233720368548E+18)
          |--- testing: -9223372036854775807 ---
          |int(9223372036854775807)
          |--- testing: -9.2233720368548E+18 ---
          |float(9.2233720368548E+18)
          |===DONE===""".stripMargin
      )
    }

    "Test -N operator : various numbers as strings" in {
      // lang/operators/negate_variationStr
      script(
        """<?php
          |
          |$strVals = array(
          |   "0","65","-44", "1.2", "-7.7", "abc", "123abc", "123e5", "123e5xyz", " 123abc", "123 abc", "123abc ", "3.4a",
          |   "a5.9"
          |);
          |
          |
          |foreach ($strVals as $strVal) {
          |   echo "--- testing: '$strVal' ---\n";
          |   var_dump(-$strVal);
          |}
          |
          |?>
          |===DONE===""".stripMargin
      ).result must haveOutput(
        """--- testing: '0' ---
          |int(0)
          |--- testing: '65' ---
          |int(-65)
          |--- testing: '-44' ---
          |int(44)
          |--- testing: '1.2' ---
          |float(-1.2)
          |--- testing: '-7.7' ---
          |float(7.7)
          |--- testing: 'abc' ---
          |int(0)
          |--- testing: '123abc' ---
          |int(-123)
          |--- testing: '123e5' ---
          |float(-12300000)
          |--- testing: '123e5xyz' ---
          |float(-12300000)
          |--- testing: ' 123abc' ---
          |int(-123)
          |--- testing: '123 abc' ---
          |int(-123)
          |--- testing: '123abc ' ---
          |int(-123)
          |--- testing: '3.4a' ---
          |float(-3.4)
          |--- testing: 'a5.9' ---
          |int(0)
          |===DONE===""".stripMargin
      )
    }
  }
}
