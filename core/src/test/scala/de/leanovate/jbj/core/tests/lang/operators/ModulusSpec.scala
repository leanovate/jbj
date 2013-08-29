/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                 (Tests based on PHP)   */

package de.leanovate.jbj.core.tests.lang.operators

import de.leanovate.jbj.core.tests.TestJbjExecutor
import org.specs2.mutable.SpecificationWithJUnit

class ModulusSpec extends SpecificationWithJUnit with TestJbjExecutor {
  "Modulus operator" should {
    "Test % operator : 64bit long tests" in {
      // lang/operators/modulus_basicLong_64bit
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
          |$otherVals = array(0, 1, -1, 7, 9, 65, -44, MAX_32Bit, MAX_64Bit);
          |
          |error_reporting(E_ERROR);
          |
          |foreach ($longVals as $longVal) {
          |   foreach($otherVals as $otherVal) {
          |	   echo "--- testing: $longVal % $otherVal ---\n";
          |      var_dump($longVal%$otherVal);
          |   }
          |}
          |
          |foreach ($otherVals as $otherVal) {
          |   foreach($longVals as $longVal) {
          |	   echo "--- testing: $otherVal % $longVal ---\n";
          |      var_dump($otherVal%$longVal);
          |   }
          |}
          |
          |?>
          |===DONE===""".stripMargin
      ).result must haveOutput(
        """--- testing: 9223372036854775807 % 0 ---
          |bool(false)
          |--- testing: 9223372036854775807 % 1 ---
          |int(0)
          |--- testing: 9223372036854775807 % -1 ---
          |int(0)
          |--- testing: 9223372036854775807 % 7 ---
          |int(0)
          |--- testing: 9223372036854775807 % 9 ---
          |int(7)
          |--- testing: 9223372036854775807 % 65 ---
          |int(7)
          |--- testing: 9223372036854775807 % -44 ---
          |int(7)
          |--- testing: 9223372036854775807 % 2147483647 ---
          |int(1)
          |--- testing: 9223372036854775807 % 9223372036854775807 ---
          |int(0)
          |--- testing: -9223372036854775808 % 0 ---
          |bool(false)
          |--- testing: -9223372036854775808 % 1 ---
          |int(0)
          |--- testing: -9223372036854775808 % -1 ---
          |int(0)
          |--- testing: -9223372036854775808 % 7 ---
          |int(-1)
          |--- testing: -9223372036854775808 % 9 ---
          |int(-8)
          |--- testing: -9223372036854775808 % 65 ---
          |int(-8)
          |--- testing: -9223372036854775808 % -44 ---
          |int(-8)
          |--- testing: -9223372036854775808 % 2147483647 ---
          |int(-2)
          |--- testing: -9223372036854775808 % 9223372036854775807 ---
          |int(-1)
          |--- testing: 2147483647 % 0 ---
          |bool(false)
          |--- testing: 2147483647 % 1 ---
          |int(0)
          |--- testing: 2147483647 % -1 ---
          |int(0)
          |--- testing: 2147483647 % 7 ---
          |int(1)
          |--- testing: 2147483647 % 9 ---
          |int(1)
          |--- testing: 2147483647 % 65 ---
          |int(62)
          |--- testing: 2147483647 % -44 ---
          |int(23)
          |--- testing: 2147483647 % 2147483647 ---
          |int(0)
          |--- testing: 2147483647 % 9223372036854775807 ---
          |int(2147483647)
          |--- testing: -2147483648 % 0 ---
          |bool(false)
          |--- testing: -2147483648 % 1 ---
          |int(0)
          |--- testing: -2147483648 % -1 ---
          |int(0)
          |--- testing: -2147483648 % 7 ---
          |int(-2)
          |--- testing: -2147483648 % 9 ---
          |int(-2)
          |--- testing: -2147483648 % 65 ---
          |int(-63)
          |--- testing: -2147483648 % -44 ---
          |int(-24)
          |--- testing: -2147483648 % 2147483647 ---
          |int(-1)
          |--- testing: -2147483648 % 9223372036854775807 ---
          |int(-2147483648)
          |--- testing: 9223372034707292160 % 0 ---
          |bool(false)
          |--- testing: 9223372034707292160 % 1 ---
          |int(0)
          |--- testing: 9223372034707292160 % -1 ---
          |int(0)
          |--- testing: 9223372034707292160 % 7 ---
          |int(6)
          |--- testing: 9223372034707292160 % 9 ---
          |int(6)
          |--- testing: 9223372034707292160 % 65 ---
          |int(10)
          |--- testing: 9223372034707292160 % -44 ---
          |int(28)
          |--- testing: 9223372034707292160 % 2147483647 ---
          |int(1)
          |--- testing: 9223372034707292160 % 9223372036854775807 ---
          |int(9223372034707292160)
          |--- testing: -9223372034707292160 % 0 ---
          |bool(false)
          |--- testing: -9223372034707292160 % 1 ---
          |int(0)
          |--- testing: -9223372034707292160 % -1 ---
          |int(0)
          |--- testing: -9223372034707292160 % 7 ---
          |int(-6)
          |--- testing: -9223372034707292160 % 9 ---
          |int(-6)
          |--- testing: -9223372034707292160 % 65 ---
          |int(-10)
          |--- testing: -9223372034707292160 % -44 ---
          |int(-28)
          |--- testing: -9223372034707292160 % 2147483647 ---
          |int(-1)
          |--- testing: -9223372034707292160 % 9223372036854775807 ---
          |int(-9223372034707292160)
          |--- testing: 2147483648 % 0 ---
          |bool(false)
          |--- testing: 2147483648 % 1 ---
          |int(0)
          |--- testing: 2147483648 % -1 ---
          |int(0)
          |--- testing: 2147483648 % 7 ---
          |int(2)
          |--- testing: 2147483648 % 9 ---
          |int(2)
          |--- testing: 2147483648 % 65 ---
          |int(63)
          |--- testing: 2147483648 % -44 ---
          |int(24)
          |--- testing: 2147483648 % 2147483647 ---
          |int(1)
          |--- testing: 2147483648 % 9223372036854775807 ---
          |int(2147483648)
          |--- testing: -2147483649 % 0 ---
          |bool(false)
          |--- testing: -2147483649 % 1 ---
          |int(0)
          |--- testing: -2147483649 % -1 ---
          |int(0)
          |--- testing: -2147483649 % 7 ---
          |int(-3)
          |--- testing: -2147483649 % 9 ---
          |int(-3)
          |--- testing: -2147483649 % 65 ---
          |int(-64)
          |--- testing: -2147483649 % -44 ---
          |int(-25)
          |--- testing: -2147483649 % 2147483647 ---
          |int(-2)
          |--- testing: -2147483649 % 9223372036854775807 ---
          |int(-2147483649)
          |--- testing: 4294967294 % 0 ---
          |bool(false)
          |--- testing: 4294967294 % 1 ---
          |int(0)
          |--- testing: 4294967294 % -1 ---
          |int(0)
          |--- testing: 4294967294 % 7 ---
          |int(2)
          |--- testing: 4294967294 % 9 ---
          |int(2)
          |--- testing: 4294967294 % 65 ---
          |int(59)
          |--- testing: 4294967294 % -44 ---
          |int(2)
          |--- testing: 4294967294 % 2147483647 ---
          |int(0)
          |--- testing: 4294967294 % 9223372036854775807 ---
          |int(4294967294)
          |--- testing: 4294967295 % 0 ---
          |bool(false)
          |--- testing: 4294967295 % 1 ---
          |int(0)
          |--- testing: 4294967295 % -1 ---
          |int(0)
          |--- testing: 4294967295 % 7 ---
          |int(3)
          |--- testing: 4294967295 % 9 ---
          |int(3)
          |--- testing: 4294967295 % 65 ---
          |int(60)
          |--- testing: 4294967295 % -44 ---
          |int(3)
          |--- testing: 4294967295 % 2147483647 ---
          |int(1)
          |--- testing: 4294967295 % 9223372036854775807 ---
          |int(4294967295)
          |--- testing: 4294967293 % 0 ---
          |bool(false)
          |--- testing: 4294967293 % 1 ---
          |int(0)
          |--- testing: 4294967293 % -1 ---
          |int(0)
          |--- testing: 4294967293 % 7 ---
          |int(1)
          |--- testing: 4294967293 % 9 ---
          |int(1)
          |--- testing: 4294967293 % 65 ---
          |int(58)
          |--- testing: 4294967293 % -44 ---
          |int(1)
          |--- testing: 4294967293 % 2147483647 ---
          |int(2147483646)
          |--- testing: 4294967293 % 9223372036854775807 ---
          |int(4294967293)
          |--- testing: 9223372036854775806 % 0 ---
          |bool(false)
          |--- testing: 9223372036854775806 % 1 ---
          |int(0)
          |--- testing: 9223372036854775806 % -1 ---
          |int(0)
          |--- testing: 9223372036854775806 % 7 ---
          |int(6)
          |--- testing: 9223372036854775806 % 9 ---
          |int(6)
          |--- testing: 9223372036854775806 % 65 ---
          |int(6)
          |--- testing: 9223372036854775806 % -44 ---
          |int(6)
          |--- testing: 9223372036854775806 % 2147483647 ---
          |int(0)
          |--- testing: 9223372036854775806 % 9223372036854775807 ---
          |int(9223372036854775806)
          |--- testing: 9.2233720368548E+18 % 0 ---
          |bool(false)
          |--- testing: 9.2233720368548E+18 % 1 ---
          |int(0)
          |--- testing: 9.2233720368548E+18 % -1 ---
          |int(0)
          |--- testing: 9.2233720368548E+18 % 7 ---
          |int(-1)
          |--- testing: 9.2233720368548E+18 % 9 ---
          |int(-8)
          |--- testing: 9.2233720368548E+18 % 65 ---
          |int(-8)
          |--- testing: 9.2233720368548E+18 % -44 ---
          |int(-8)
          |--- testing: 9.2233720368548E+18 % 2147483647 ---
          |int(-2)
          |--- testing: 9.2233720368548E+18 % 9223372036854775807 ---
          |int(-1)
          |--- testing: -9223372036854775807 % 0 ---
          |bool(false)
          |--- testing: -9223372036854775807 % 1 ---
          |int(0)
          |--- testing: -9223372036854775807 % -1 ---
          |int(0)
          |--- testing: -9223372036854775807 % 7 ---
          |int(0)
          |--- testing: -9223372036854775807 % 9 ---
          |int(-7)
          |--- testing: -9223372036854775807 % 65 ---
          |int(-7)
          |--- testing: -9223372036854775807 % -44 ---
          |int(-7)
          |--- testing: -9223372036854775807 % 2147483647 ---
          |int(-1)
          |--- testing: -9223372036854775807 % 9223372036854775807 ---
          |int(0)
          |--- testing: -9.2233720368548E+18 % 0 ---
          |bool(false)
          |--- testing: -9.2233720368548E+18 % 1 ---
          |int(0)
          |--- testing: -9.2233720368548E+18 % -1 ---
          |int(0)
          |--- testing: -9.2233720368548E+18 % 7 ---
          |int(-1)
          |--- testing: -9.2233720368548E+18 % 9 ---
          |int(-8)
          |--- testing: -9.2233720368548E+18 % 65 ---
          |int(-8)
          |--- testing: -9.2233720368548E+18 % -44 ---
          |int(-8)
          |--- testing: -9.2233720368548E+18 % 2147483647 ---
          |int(-2)
          |--- testing: -9.2233720368548E+18 % 9223372036854775807 ---
          |int(-1)
          |--- testing: 0 % 9223372036854775807 ---
          |int(0)
          |--- testing: 0 % -9223372036854775808 ---
          |int(0)
          |--- testing: 0 % 2147483647 ---
          |int(0)
          |--- testing: 0 % -2147483648 ---
          |int(0)
          |--- testing: 0 % 9223372034707292160 ---
          |int(0)
          |--- testing: 0 % -9223372034707292160 ---
          |int(0)
          |--- testing: 0 % 2147483648 ---
          |int(0)
          |--- testing: 0 % -2147483649 ---
          |int(0)
          |--- testing: 0 % 4294967294 ---
          |int(0)
          |--- testing: 0 % 4294967295 ---
          |int(0)
          |--- testing: 0 % 4294967293 ---
          |int(0)
          |--- testing: 0 % 9223372036854775806 ---
          |int(0)
          |--- testing: 0 % 9.2233720368548E+18 ---
          |int(0)
          |--- testing: 0 % -9223372036854775807 ---
          |int(0)
          |--- testing: 0 % -9.2233720368548E+18 ---
          |int(0)
          |--- testing: 1 % 9223372036854775807 ---
          |int(1)
          |--- testing: 1 % -9223372036854775808 ---
          |int(1)
          |--- testing: 1 % 2147483647 ---
          |int(1)
          |--- testing: 1 % -2147483648 ---
          |int(1)
          |--- testing: 1 % 9223372034707292160 ---
          |int(1)
          |--- testing: 1 % -9223372034707292160 ---
          |int(1)
          |--- testing: 1 % 2147483648 ---
          |int(1)
          |--- testing: 1 % -2147483649 ---
          |int(1)
          |--- testing: 1 % 4294967294 ---
          |int(1)
          |--- testing: 1 % 4294967295 ---
          |int(1)
          |--- testing: 1 % 4294967293 ---
          |int(1)
          |--- testing: 1 % 9223372036854775806 ---
          |int(1)
          |--- testing: 1 % 9.2233720368548E+18 ---
          |int(1)
          |--- testing: 1 % -9223372036854775807 ---
          |int(1)
          |--- testing: 1 % -9.2233720368548E+18 ---
          |int(1)
          |--- testing: -1 % 9223372036854775807 ---
          |int(-1)
          |--- testing: -1 % -9223372036854775808 ---
          |int(-1)
          |--- testing: -1 % 2147483647 ---
          |int(-1)
          |--- testing: -1 % -2147483648 ---
          |int(-1)
          |--- testing: -1 % 9223372034707292160 ---
          |int(-1)
          |--- testing: -1 % -9223372034707292160 ---
          |int(-1)
          |--- testing: -1 % 2147483648 ---
          |int(-1)
          |--- testing: -1 % -2147483649 ---
          |int(-1)
          |--- testing: -1 % 4294967294 ---
          |int(-1)
          |--- testing: -1 % 4294967295 ---
          |int(-1)
          |--- testing: -1 % 4294967293 ---
          |int(-1)
          |--- testing: -1 % 9223372036854775806 ---
          |int(-1)
          |--- testing: -1 % 9.2233720368548E+18 ---
          |int(-1)
          |--- testing: -1 % -9223372036854775807 ---
          |int(-1)
          |--- testing: -1 % -9.2233720368548E+18 ---
          |int(-1)
          |--- testing: 7 % 9223372036854775807 ---
          |int(7)
          |--- testing: 7 % -9223372036854775808 ---
          |int(7)
          |--- testing: 7 % 2147483647 ---
          |int(7)
          |--- testing: 7 % -2147483648 ---
          |int(7)
          |--- testing: 7 % 9223372034707292160 ---
          |int(7)
          |--- testing: 7 % -9223372034707292160 ---
          |int(7)
          |--- testing: 7 % 2147483648 ---
          |int(7)
          |--- testing: 7 % -2147483649 ---
          |int(7)
          |--- testing: 7 % 4294967294 ---
          |int(7)
          |--- testing: 7 % 4294967295 ---
          |int(7)
          |--- testing: 7 % 4294967293 ---
          |int(7)
          |--- testing: 7 % 9223372036854775806 ---
          |int(7)
          |--- testing: 7 % 9.2233720368548E+18 ---
          |int(7)
          |--- testing: 7 % -9223372036854775807 ---
          |int(7)
          |--- testing: 7 % -9.2233720368548E+18 ---
          |int(7)
          |--- testing: 9 % 9223372036854775807 ---
          |int(9)
          |--- testing: 9 % -9223372036854775808 ---
          |int(9)
          |--- testing: 9 % 2147483647 ---
          |int(9)
          |--- testing: 9 % -2147483648 ---
          |int(9)
          |--- testing: 9 % 9223372034707292160 ---
          |int(9)
          |--- testing: 9 % -9223372034707292160 ---
          |int(9)
          |--- testing: 9 % 2147483648 ---
          |int(9)
          |--- testing: 9 % -2147483649 ---
          |int(9)
          |--- testing: 9 % 4294967294 ---
          |int(9)
          |--- testing: 9 % 4294967295 ---
          |int(9)
          |--- testing: 9 % 4294967293 ---
          |int(9)
          |--- testing: 9 % 9223372036854775806 ---
          |int(9)
          |--- testing: 9 % 9.2233720368548E+18 ---
          |int(9)
          |--- testing: 9 % -9223372036854775807 ---
          |int(9)
          |--- testing: 9 % -9.2233720368548E+18 ---
          |int(9)
          |--- testing: 65 % 9223372036854775807 ---
          |int(65)
          |--- testing: 65 % -9223372036854775808 ---
          |int(65)
          |--- testing: 65 % 2147483647 ---
          |int(65)
          |--- testing: 65 % -2147483648 ---
          |int(65)
          |--- testing: 65 % 9223372034707292160 ---
          |int(65)
          |--- testing: 65 % -9223372034707292160 ---
          |int(65)
          |--- testing: 65 % 2147483648 ---
          |int(65)
          |--- testing: 65 % -2147483649 ---
          |int(65)
          |--- testing: 65 % 4294967294 ---
          |int(65)
          |--- testing: 65 % 4294967295 ---
          |int(65)
          |--- testing: 65 % 4294967293 ---
          |int(65)
          |--- testing: 65 % 9223372036854775806 ---
          |int(65)
          |--- testing: 65 % 9.2233720368548E+18 ---
          |int(65)
          |--- testing: 65 % -9223372036854775807 ---
          |int(65)
          |--- testing: 65 % -9.2233720368548E+18 ---
          |int(65)
          |--- testing: -44 % 9223372036854775807 ---
          |int(-44)
          |--- testing: -44 % -9223372036854775808 ---
          |int(-44)
          |--- testing: -44 % 2147483647 ---
          |int(-44)
          |--- testing: -44 % -2147483648 ---
          |int(-44)
          |--- testing: -44 % 9223372034707292160 ---
          |int(-44)
          |--- testing: -44 % -9223372034707292160 ---
          |int(-44)
          |--- testing: -44 % 2147483648 ---
          |int(-44)
          |--- testing: -44 % -2147483649 ---
          |int(-44)
          |--- testing: -44 % 4294967294 ---
          |int(-44)
          |--- testing: -44 % 4294967295 ---
          |int(-44)
          |--- testing: -44 % 4294967293 ---
          |int(-44)
          |--- testing: -44 % 9223372036854775806 ---
          |int(-44)
          |--- testing: -44 % 9.2233720368548E+18 ---
          |int(-44)
          |--- testing: -44 % -9223372036854775807 ---
          |int(-44)
          |--- testing: -44 % -9.2233720368548E+18 ---
          |int(-44)
          |--- testing: 2147483647 % 9223372036854775807 ---
          |int(2147483647)
          |--- testing: 2147483647 % -9223372036854775808 ---
          |int(2147483647)
          |--- testing: 2147483647 % 2147483647 ---
          |int(0)
          |--- testing: 2147483647 % -2147483648 ---
          |int(2147483647)
          |--- testing: 2147483647 % 9223372034707292160 ---
          |int(2147483647)
          |--- testing: 2147483647 % -9223372034707292160 ---
          |int(2147483647)
          |--- testing: 2147483647 % 2147483648 ---
          |int(2147483647)
          |--- testing: 2147483647 % -2147483649 ---
          |int(2147483647)
          |--- testing: 2147483647 % 4294967294 ---
          |int(2147483647)
          |--- testing: 2147483647 % 4294967295 ---
          |int(2147483647)
          |--- testing: 2147483647 % 4294967293 ---
          |int(2147483647)
          |--- testing: 2147483647 % 9223372036854775806 ---
          |int(2147483647)
          |--- testing: 2147483647 % 9.2233720368548E+18 ---
          |int(2147483647)
          |--- testing: 2147483647 % -9223372036854775807 ---
          |int(2147483647)
          |--- testing: 2147483647 % -9.2233720368548E+18 ---
          |int(2147483647)
          |--- testing: 9223372036854775807 % 9223372036854775807 ---
          |int(0)
          |--- testing: 9223372036854775807 % -9223372036854775808 ---
          |int(9223372036854775807)
          |--- testing: 9223372036854775807 % 2147483647 ---
          |int(1)
          |--- testing: 9223372036854775807 % -2147483648 ---
          |int(2147483647)
          |--- testing: 9223372036854775807 % 9223372034707292160 ---
          |int(2147483647)
          |--- testing: 9223372036854775807 % -9223372034707292160 ---
          |int(2147483647)
          |--- testing: 9223372036854775807 % 2147483648 ---
          |int(2147483647)
          |--- testing: 9223372036854775807 % -2147483649 ---
          |int(1)
          |--- testing: 9223372036854775807 % 4294967294 ---
          |int(1)
          |--- testing: 9223372036854775807 % 4294967295 ---
          |int(2147483647)
          |--- testing: 9223372036854775807 % 4294967293 ---
          |int(2147483650)
          |--- testing: 9223372036854775807 % 9223372036854775806 ---
          |int(1)
          |--- testing: 9223372036854775807 % 9.2233720368548E+18 ---
          |int(9223372036854775807)
          |--- testing: 9223372036854775807 % -9223372036854775807 ---
          |int(0)
          |--- testing: 9223372036854775807 % -9.2233720368548E+18 ---
          |int(9223372036854775807)
          |===DONE===""".stripMargin
      )
    }

    "Test % operator : various numbers as strings" in {
      // lang/operators/modulus_verationStr
      script(
        """<?php
          |
          |$strVals = array(
          |   "0","65","-44", "1.2", "-7.7", "abc", "123abc", "123e5", "123e5xyz", " 123abc", "123 abc", "123abc ", "3.4a",
          |   "a5.9"
          |);
          |
          |error_reporting(E_ERROR);
          |
          |foreach ($strVals as $strVal) {
          |   foreach($strVals as $otherVal) {
          |	   echo "--- testing: '$strVal' % '$otherVal' ---\n";
          |      var_dump($strVal%$otherVal);
          |   }
          |}
          |
          |
          |?>""".stripMargin
      ).result must haveOutput (
        """--- testing: '0' % '0' ---
          |bool(false)
          |--- testing: '0' % '65' ---
          |int(0)
          |--- testing: '0' % '-44' ---
          |int(0)
          |--- testing: '0' % '1.2' ---
          |int(0)
          |--- testing: '0' % '-7.7' ---
          |int(0)
          |--- testing: '0' % 'abc' ---
          |bool(false)
          |--- testing: '0' % '123abc' ---
          |int(0)
          |--- testing: '0' % '123e5' ---
          |int(0)
          |--- testing: '0' % '123e5xyz' ---
          |int(0)
          |--- testing: '0' % ' 123abc' ---
          |int(0)
          |--- testing: '0' % '123 abc' ---
          |int(0)
          |--- testing: '0' % '123abc ' ---
          |int(0)
          |--- testing: '0' % '3.4a' ---
          |int(0)
          |--- testing: '0' % 'a5.9' ---
          |bool(false)
          |--- testing: '65' % '0' ---
          |bool(false)
          |--- testing: '65' % '65' ---
          |int(0)
          |--- testing: '65' % '-44' ---
          |int(21)
          |--- testing: '65' % '1.2' ---
          |int(0)
          |--- testing: '65' % '-7.7' ---
          |int(2)
          |--- testing: '65' % 'abc' ---
          |bool(false)
          |--- testing: '65' % '123abc' ---
          |int(65)
          |--- testing: '65' % '123e5' ---
          |int(65)
          |--- testing: '65' % '123e5xyz' ---
          |int(65)
          |--- testing: '65' % ' 123abc' ---
          |int(65)
          |--- testing: '65' % '123 abc' ---
          |int(65)
          |--- testing: '65' % '123abc ' ---
          |int(65)
          |--- testing: '65' % '3.4a' ---
          |int(2)
          |--- testing: '65' % 'a5.9' ---
          |bool(false)
          |--- testing: '-44' % '0' ---
          |bool(false)
          |--- testing: '-44' % '65' ---
          |int(-44)
          |--- testing: '-44' % '-44' ---
          |int(0)
          |--- testing: '-44' % '1.2' ---
          |int(0)
          |--- testing: '-44' % '-7.7' ---
          |int(-2)
          |--- testing: '-44' % 'abc' ---
          |bool(false)
          |--- testing: '-44' % '123abc' ---
          |int(-44)
          |--- testing: '-44' % '123e5' ---
          |int(-44)
          |--- testing: '-44' % '123e5xyz' ---
          |int(-44)
          |--- testing: '-44' % ' 123abc' ---
          |int(-44)
          |--- testing: '-44' % '123 abc' ---
          |int(-44)
          |--- testing: '-44' % '123abc ' ---
          |int(-44)
          |--- testing: '-44' % '3.4a' ---
          |int(-2)
          |--- testing: '-44' % 'a5.9' ---
          |bool(false)
          |--- testing: '1.2' % '0' ---
          |bool(false)
          |--- testing: '1.2' % '65' ---
          |int(1)
          |--- testing: '1.2' % '-44' ---
          |int(1)
          |--- testing: '1.2' % '1.2' ---
          |int(0)
          |--- testing: '1.2' % '-7.7' ---
          |int(1)
          |--- testing: '1.2' % 'abc' ---
          |bool(false)
          |--- testing: '1.2' % '123abc' ---
          |int(1)
          |--- testing: '1.2' % '123e5' ---
          |int(1)
          |--- testing: '1.2' % '123e5xyz' ---
          |int(1)
          |--- testing: '1.2' % ' 123abc' ---
          |int(1)
          |--- testing: '1.2' % '123 abc' ---
          |int(1)
          |--- testing: '1.2' % '123abc ' ---
          |int(1)
          |--- testing: '1.2' % '3.4a' ---
          |int(1)
          |--- testing: '1.2' % 'a5.9' ---
          |bool(false)
          |--- testing: '-7.7' % '0' ---
          |bool(false)
          |--- testing: '-7.7' % '65' ---
          |int(-7)
          |--- testing: '-7.7' % '-44' ---
          |int(-7)
          |--- testing: '-7.7' % '1.2' ---
          |int(0)
          |--- testing: '-7.7' % '-7.7' ---
          |int(0)
          |--- testing: '-7.7' % 'abc' ---
          |bool(false)
          |--- testing: '-7.7' % '123abc' ---
          |int(-7)
          |--- testing: '-7.7' % '123e5' ---
          |int(-7)
          |--- testing: '-7.7' % '123e5xyz' ---
          |int(-7)
          |--- testing: '-7.7' % ' 123abc' ---
          |int(-7)
          |--- testing: '-7.7' % '123 abc' ---
          |int(-7)
          |--- testing: '-7.7' % '123abc ' ---
          |int(-7)
          |--- testing: '-7.7' % '3.4a' ---
          |int(-1)
          |--- testing: '-7.7' % 'a5.9' ---
          |bool(false)
          |--- testing: 'abc' % '0' ---
          |bool(false)
          |--- testing: 'abc' % '65' ---
          |int(0)
          |--- testing: 'abc' % '-44' ---
          |int(0)
          |--- testing: 'abc' % '1.2' ---
          |int(0)
          |--- testing: 'abc' % '-7.7' ---
          |int(0)
          |--- testing: 'abc' % 'abc' ---
          |bool(false)
          |--- testing: 'abc' % '123abc' ---
          |int(0)
          |--- testing: 'abc' % '123e5' ---
          |int(0)
          |--- testing: 'abc' % '123e5xyz' ---
          |int(0)
          |--- testing: 'abc' % ' 123abc' ---
          |int(0)
          |--- testing: 'abc' % '123 abc' ---
          |int(0)
          |--- testing: 'abc' % '123abc ' ---
          |int(0)
          |--- testing: 'abc' % '3.4a' ---
          |int(0)
          |--- testing: 'abc' % 'a5.9' ---
          |bool(false)
          |--- testing: '123abc' % '0' ---
          |bool(false)
          |--- testing: '123abc' % '65' ---
          |int(58)
          |--- testing: '123abc' % '-44' ---
          |int(35)
          |--- testing: '123abc' % '1.2' ---
          |int(0)
          |--- testing: '123abc' % '-7.7' ---
          |int(4)
          |--- testing: '123abc' % 'abc' ---
          |bool(false)
          |--- testing: '123abc' % '123abc' ---
          |int(0)
          |--- testing: '123abc' % '123e5' ---
          |int(0)
          |--- testing: '123abc' % '123e5xyz' ---
          |int(0)
          |--- testing: '123abc' % ' 123abc' ---
          |int(0)
          |--- testing: '123abc' % '123 abc' ---
          |int(0)
          |--- testing: '123abc' % '123abc ' ---
          |int(0)
          |--- testing: '123abc' % '3.4a' ---
          |int(0)
          |--- testing: '123abc' % 'a5.9' ---
          |bool(false)
          |--- testing: '123e5' % '0' ---
          |bool(false)
          |--- testing: '123e5' % '65' ---
          |int(58)
          |--- testing: '123e5' % '-44' ---
          |int(35)
          |--- testing: '123e5' % '1.2' ---
          |int(0)
          |--- testing: '123e5' % '-7.7' ---
          |int(4)
          |--- testing: '123e5' % 'abc' ---
          |bool(false)
          |--- testing: '123e5' % '123abc' ---
          |int(0)
          |--- testing: '123e5' % '123e5' ---
          |int(0)
          |--- testing: '123e5' % '123e5xyz' ---
          |int(0)
          |--- testing: '123e5' % ' 123abc' ---
          |int(0)
          |--- testing: '123e5' % '123 abc' ---
          |int(0)
          |--- testing: '123e5' % '123abc ' ---
          |int(0)
          |--- testing: '123e5' % '3.4a' ---
          |int(0)
          |--- testing: '123e5' % 'a5.9' ---
          |bool(false)
          |--- testing: '123e5xyz' % '0' ---
          |bool(false)
          |--- testing: '123e5xyz' % '65' ---
          |int(58)
          |--- testing: '123e5xyz' % '-44' ---
          |int(35)
          |--- testing: '123e5xyz' % '1.2' ---
          |int(0)
          |--- testing: '123e5xyz' % '-7.7' ---
          |int(4)
          |--- testing: '123e5xyz' % 'abc' ---
          |bool(false)
          |--- testing: '123e5xyz' % '123abc' ---
          |int(0)
          |--- testing: '123e5xyz' % '123e5' ---
          |int(0)
          |--- testing: '123e5xyz' % '123e5xyz' ---
          |int(0)
          |--- testing: '123e5xyz' % ' 123abc' ---
          |int(0)
          |--- testing: '123e5xyz' % '123 abc' ---
          |int(0)
          |--- testing: '123e5xyz' % '123abc ' ---
          |int(0)
          |--- testing: '123e5xyz' % '3.4a' ---
          |int(0)
          |--- testing: '123e5xyz' % 'a5.9' ---
          |bool(false)
          |--- testing: ' 123abc' % '0' ---
          |bool(false)
          |--- testing: ' 123abc' % '65' ---
          |int(58)
          |--- testing: ' 123abc' % '-44' ---
          |int(35)
          |--- testing: ' 123abc' % '1.2' ---
          |int(0)
          |--- testing: ' 123abc' % '-7.7' ---
          |int(4)
          |--- testing: ' 123abc' % 'abc' ---
          |bool(false)
          |--- testing: ' 123abc' % '123abc' ---
          |int(0)
          |--- testing: ' 123abc' % '123e5' ---
          |int(0)
          |--- testing: ' 123abc' % '123e5xyz' ---
          |int(0)
          |--- testing: ' 123abc' % ' 123abc' ---
          |int(0)
          |--- testing: ' 123abc' % '123 abc' ---
          |int(0)
          |--- testing: ' 123abc' % '123abc ' ---
          |int(0)
          |--- testing: ' 123abc' % '3.4a' ---
          |int(0)
          |--- testing: ' 123abc' % 'a5.9' ---
          |bool(false)
          |--- testing: '123 abc' % '0' ---
          |bool(false)
          |--- testing: '123 abc' % '65' ---
          |int(58)
          |--- testing: '123 abc' % '-44' ---
          |int(35)
          |--- testing: '123 abc' % '1.2' ---
          |int(0)
          |--- testing: '123 abc' % '-7.7' ---
          |int(4)
          |--- testing: '123 abc' % 'abc' ---
          |bool(false)
          |--- testing: '123 abc' % '123abc' ---
          |int(0)
          |--- testing: '123 abc' % '123e5' ---
          |int(0)
          |--- testing: '123 abc' % '123e5xyz' ---
          |int(0)
          |--- testing: '123 abc' % ' 123abc' ---
          |int(0)
          |--- testing: '123 abc' % '123 abc' ---
          |int(0)
          |--- testing: '123 abc' % '123abc ' ---
          |int(0)
          |--- testing: '123 abc' % '3.4a' ---
          |int(0)
          |--- testing: '123 abc' % 'a5.9' ---
          |bool(false)
          |--- testing: '123abc ' % '0' ---
          |bool(false)
          |--- testing: '123abc ' % '65' ---
          |int(58)
          |--- testing: '123abc ' % '-44' ---
          |int(35)
          |--- testing: '123abc ' % '1.2' ---
          |int(0)
          |--- testing: '123abc ' % '-7.7' ---
          |int(4)
          |--- testing: '123abc ' % 'abc' ---
          |bool(false)
          |--- testing: '123abc ' % '123abc' ---
          |int(0)
          |--- testing: '123abc ' % '123e5' ---
          |int(0)
          |--- testing: '123abc ' % '123e5xyz' ---
          |int(0)
          |--- testing: '123abc ' % ' 123abc' ---
          |int(0)
          |--- testing: '123abc ' % '123 abc' ---
          |int(0)
          |--- testing: '123abc ' % '123abc ' ---
          |int(0)
          |--- testing: '123abc ' % '3.4a' ---
          |int(0)
          |--- testing: '123abc ' % 'a5.9' ---
          |bool(false)
          |--- testing: '3.4a' % '0' ---
          |bool(false)
          |--- testing: '3.4a' % '65' ---
          |int(3)
          |--- testing: '3.4a' % '-44' ---
          |int(3)
          |--- testing: '3.4a' % '1.2' ---
          |int(0)
          |--- testing: '3.4a' % '-7.7' ---
          |int(3)
          |--- testing: '3.4a' % 'abc' ---
          |bool(false)
          |--- testing: '3.4a' % '123abc' ---
          |int(3)
          |--- testing: '3.4a' % '123e5' ---
          |int(3)
          |--- testing: '3.4a' % '123e5xyz' ---
          |int(3)
          |--- testing: '3.4a' % ' 123abc' ---
          |int(3)
          |--- testing: '3.4a' % '123 abc' ---
          |int(3)
          |--- testing: '3.4a' % '123abc ' ---
          |int(3)
          |--- testing: '3.4a' % '3.4a' ---
          |int(0)
          |--- testing: '3.4a' % 'a5.9' ---
          |bool(false)
          |--- testing: 'a5.9' % '0' ---
          |bool(false)
          |--- testing: 'a5.9' % '65' ---
          |int(0)
          |--- testing: 'a5.9' % '-44' ---
          |int(0)
          |--- testing: 'a5.9' % '1.2' ---
          |int(0)
          |--- testing: 'a5.9' % '-7.7' ---
          |int(0)
          |--- testing: 'a5.9' % 'abc' ---
          |bool(false)
          |--- testing: 'a5.9' % '123abc' ---
          |int(0)
          |--- testing: 'a5.9' % '123e5' ---
          |int(0)
          |--- testing: 'a5.9' % '123e5xyz' ---
          |int(0)
          |--- testing: 'a5.9' % ' 123abc' ---
          |int(0)
          |--- testing: 'a5.9' % '123 abc' ---
          |int(0)
          |--- testing: 'a5.9' % '123abc ' ---
          |int(0)
          |--- testing: 'a5.9' % '3.4a' ---
          |int(0)
          |--- testing: 'a5.9' % 'a5.9' ---
          |bool(false)
          |""".stripMargin
      )
    }
  }
}
