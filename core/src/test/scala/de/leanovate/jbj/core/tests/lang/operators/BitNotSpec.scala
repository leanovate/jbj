package de.leanovate.jbj.core.tests.lang.operators

import org.specs2.mutable.SpecificationWithJUnit
import de.leanovate.jbj.core.tests.TestJbjExecutor

class BitNotSpec extends SpecificationWithJUnit with TestJbjExecutor {
  "Bit not operator" should {
    "est ~N operator : 64bit long tests" in {
      // lang/operators/bitwiseNot_basicLong_64bit
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
          |   var_dump(~$longVal);
          |}
          |
          |?>
          |===DONE===""".stripMargin
      ).result must haveOutput(
        """--- testing: 9223372036854775807 ---
          |int(-9223372036854775808)
          |--- testing: -9223372036854775808 ---
          |int(9223372036854775807)
          |--- testing: 2147483647 ---
          |int(-2147483648)
          |--- testing: -2147483648 ---
          |int(2147483647)
          |--- testing: 9223372034707292160 ---
          |int(-9223372034707292161)
          |--- testing: -9223372034707292160 ---
          |int(9223372034707292159)
          |--- testing: 2147483648 ---
          |int(-2147483649)
          |--- testing: -2147483649 ---
          |int(2147483648)
          |--- testing: 4294967294 ---
          |int(-4294967295)
          |--- testing: 4294967295 ---
          |int(-4294967296)
          |--- testing: 4294967293 ---
          |int(-4294967294)
          |--- testing: 9223372036854775806 ---
          |int(-9223372036854775807)
          |--- testing: 9.2233720368548E+18 ---
          |int(9223372036854775807)
          |--- testing: -9223372036854775807 ---
          |int(9223372036854775806)
          |--- testing: -9.2233720368548E+18 ---
          |int(9223372036854775807)
          |===DONE===""".stripMargin
      )
    }

    "Test ~N operator : various numbers as strings" in {
      // lang/operators/bitwiseNot_variationStr
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
          |   var_dump(bin2hex(~$strVal));
          |}
          |
          |?>
          |===DONE===""".stripMargin
      ).result must haveOutput(
        """--- testing: '0' ---
          |string(2) "cf"
          |--- testing: '65' ---
          |string(4) "c9ca"
          |--- testing: '-44' ---
          |string(6) "d2cbcb"
          |--- testing: '1.2' ---
          |string(6) "ced1cd"
          |--- testing: '-7.7' ---
          |string(8) "d2c8d1c8"
          |--- testing: 'abc' ---
          |string(6) "9e9d9c"
          |--- testing: '123abc' ---
          |string(12) "cecdcc9e9d9c"
          |--- testing: '123e5' ---
          |string(10) "cecdcc9aca"
          |--- testing: '123e5xyz' ---
          |string(16) "cecdcc9aca878685"
          |--- testing: ' 123abc' ---
          |string(14) "dfcecdcc9e9d9c"
          |--- testing: '123 abc' ---
          |string(14) "cecdccdf9e9d9c"
          |--- testing: '123abc ' ---
          |string(14) "cecdcc9e9d9cdf"
          |--- testing: '3.4a' ---
          |string(8) "ccd1cb9e"
          |--- testing: 'a5.9' ---
          |string(8) "9ecad1c6"
          |===DONE===""".stripMargin
      )
    }
  }
}
