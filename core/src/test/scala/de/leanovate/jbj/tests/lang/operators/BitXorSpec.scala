package de.leanovate.jbj.tests.lang.operators

import org.specs2.mutable.SpecificationWithJUnit
import de.leanovate.jbj.tests.TestJbjExecutor

class BitXorSpec extends SpecificationWithJUnit with TestJbjExecutor {
  "Bit xor operator" should {
    "Test ^ operator : 64bit long tests" in {
      // lang/operators/bitwiseXor_basicLong_64bit
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
          |	   echo "--- testing: $longVal ^ $otherVal ---\n";
          |      var_dump($longVal^$otherVal);
          |   }
          |}
          |
          |foreach ($otherVals as $otherVal) {
          |   foreach($longVals as $longVal) {
          |	   echo "--- testing: $otherVal ^ $longVal ---\n";
          |      var_dump($otherVal^$longVal);
          |   }
          |}
          |
          |?>
          |===DONE===""".stripMargin
      ).result must haveOutput(
        """--- testing: 9223372036854775807 ^ 0 ---
          |int(9223372036854775807)
          |--- testing: 9223372036854775807 ^ 1 ---
          |int(9223372036854775806)
          |--- testing: 9223372036854775807 ^ -1 ---
          |int(-9223372036854775808)
          |--- testing: 9223372036854775807 ^ 7 ---
          |int(9223372036854775800)
          |--- testing: 9223372036854775807 ^ 9 ---
          |int(9223372036854775798)
          |--- testing: 9223372036854775807 ^ 65 ---
          |int(9223372036854775742)
          |--- testing: 9223372036854775807 ^ -44 ---
          |int(-9223372036854775765)
          |--- testing: 9223372036854775807 ^ 2147483647 ---
          |int(9223372034707292160)
          |--- testing: 9223372036854775807 ^ 9223372036854775807 ---
          |int(0)
          |--- testing: -9223372036854775808 ^ 0 ---
          |int(-9223372036854775808)
          |--- testing: -9223372036854775808 ^ 1 ---
          |int(-9223372036854775807)
          |--- testing: -9223372036854775808 ^ -1 ---
          |int(9223372036854775807)
          |--- testing: -9223372036854775808 ^ 7 ---
          |int(-9223372036854775801)
          |--- testing: -9223372036854775808 ^ 9 ---
          |int(-9223372036854775799)
          |--- testing: -9223372036854775808 ^ 65 ---
          |int(-9223372036854775743)
          |--- testing: -9223372036854775808 ^ -44 ---
          |int(9223372036854775764)
          |--- testing: -9223372036854775808 ^ 2147483647 ---
          |int(-9223372034707292161)
          |--- testing: -9223372036854775808 ^ 9223372036854775807 ---
          |int(-1)
          |--- testing: 2147483647 ^ 0 ---
          |int(2147483647)
          |--- testing: 2147483647 ^ 1 ---
          |int(2147483646)
          |--- testing: 2147483647 ^ -1 ---
          |int(-2147483648)
          |--- testing: 2147483647 ^ 7 ---
          |int(2147483640)
          |--- testing: 2147483647 ^ 9 ---
          |int(2147483638)
          |--- testing: 2147483647 ^ 65 ---
          |int(2147483582)
          |--- testing: 2147483647 ^ -44 ---
          |int(-2147483605)
          |--- testing: 2147483647 ^ 2147483647 ---
          |int(0)
          |--- testing: 2147483647 ^ 9223372036854775807 ---
          |int(9223372034707292160)
          |--- testing: -2147483648 ^ 0 ---
          |int(-2147483648)
          |--- testing: -2147483648 ^ 1 ---
          |int(-2147483647)
          |--- testing: -2147483648 ^ -1 ---
          |int(2147483647)
          |--- testing: -2147483648 ^ 7 ---
          |int(-2147483641)
          |--- testing: -2147483648 ^ 9 ---
          |int(-2147483639)
          |--- testing: -2147483648 ^ 65 ---
          |int(-2147483583)
          |--- testing: -2147483648 ^ -44 ---
          |int(2147483604)
          |--- testing: -2147483648 ^ 2147483647 ---
          |int(-1)
          |--- testing: -2147483648 ^ 9223372036854775807 ---
          |int(-9223372034707292161)
          |--- testing: 9223372034707292160 ^ 0 ---
          |int(9223372034707292160)
          |--- testing: 9223372034707292160 ^ 1 ---
          |int(9223372034707292161)
          |--- testing: 9223372034707292160 ^ -1 ---
          |int(-9223372034707292161)
          |--- testing: 9223372034707292160 ^ 7 ---
          |int(9223372034707292167)
          |--- testing: 9223372034707292160 ^ 9 ---
          |int(9223372034707292169)
          |--- testing: 9223372034707292160 ^ 65 ---
          |int(9223372034707292225)
          |--- testing: 9223372034707292160 ^ -44 ---
          |int(-9223372034707292204)
          |--- testing: 9223372034707292160 ^ 2147483647 ---
          |int(9223372036854775807)
          |--- testing: 9223372034707292160 ^ 9223372036854775807 ---
          |int(2147483647)
          |--- testing: -9223372034707292160 ^ 0 ---
          |int(-9223372034707292160)
          |--- testing: -9223372034707292160 ^ 1 ---
          |int(-9223372034707292159)
          |--- testing: -9223372034707292160 ^ -1 ---
          |int(9223372034707292159)
          |--- testing: -9223372034707292160 ^ 7 ---
          |int(-9223372034707292153)
          |--- testing: -9223372034707292160 ^ 9 ---
          |int(-9223372034707292151)
          |--- testing: -9223372034707292160 ^ 65 ---
          |int(-9223372034707292095)
          |--- testing: -9223372034707292160 ^ -44 ---
          |int(9223372034707292116)
          |--- testing: -9223372034707292160 ^ 2147483647 ---
          |int(-9223372032559808513)
          |--- testing: -9223372034707292160 ^ 9223372036854775807 ---
          |int(-2147483649)
          |--- testing: 2147483648 ^ 0 ---
          |int(2147483648)
          |--- testing: 2147483648 ^ 1 ---
          |int(2147483649)
          |--- testing: 2147483648 ^ -1 ---
          |int(-2147483649)
          |--- testing: 2147483648 ^ 7 ---
          |int(2147483655)
          |--- testing: 2147483648 ^ 9 ---
          |int(2147483657)
          |--- testing: 2147483648 ^ 65 ---
          |int(2147483713)
          |--- testing: 2147483648 ^ -44 ---
          |int(-2147483692)
          |--- testing: 2147483648 ^ 2147483647 ---
          |int(4294967295)
          |--- testing: 2147483648 ^ 9223372036854775807 ---
          |int(9223372034707292159)
          |--- testing: -2147483649 ^ 0 ---
          |int(-2147483649)
          |--- testing: -2147483649 ^ 1 ---
          |int(-2147483650)
          |--- testing: -2147483649 ^ -1 ---
          |int(2147483648)
          |--- testing: -2147483649 ^ 7 ---
          |int(-2147483656)
          |--- testing: -2147483649 ^ 9 ---
          |int(-2147483658)
          |--- testing: -2147483649 ^ 65 ---
          |int(-2147483714)
          |--- testing: -2147483649 ^ -44 ---
          |int(2147483691)
          |--- testing: -2147483649 ^ 2147483647 ---
          |int(-4294967296)
          |--- testing: -2147483649 ^ 9223372036854775807 ---
          |int(-9223372034707292160)
          |--- testing: 4294967294 ^ 0 ---
          |int(4294967294)
          |--- testing: 4294967294 ^ 1 ---
          |int(4294967295)
          |--- testing: 4294967294 ^ -1 ---
          |int(-4294967295)
          |--- testing: 4294967294 ^ 7 ---
          |int(4294967289)
          |--- testing: 4294967294 ^ 9 ---
          |int(4294967287)
          |--- testing: 4294967294 ^ 65 ---
          |int(4294967231)
          |--- testing: 4294967294 ^ -44 ---
          |int(-4294967254)
          |--- testing: 4294967294 ^ 2147483647 ---
          |int(2147483649)
          |--- testing: 4294967294 ^ 9223372036854775807 ---
          |int(9223372032559808513)
          |--- testing: 4294967295 ^ 0 ---
          |int(4294967295)
          |--- testing: 4294967295 ^ 1 ---
          |int(4294967294)
          |--- testing: 4294967295 ^ -1 ---
          |int(-4294967296)
          |--- testing: 4294967295 ^ 7 ---
          |int(4294967288)
          |--- testing: 4294967295 ^ 9 ---
          |int(4294967286)
          |--- testing: 4294967295 ^ 65 ---
          |int(4294967230)
          |--- testing: 4294967295 ^ -44 ---
          |int(-4294967253)
          |--- testing: 4294967295 ^ 2147483647 ---
          |int(2147483648)
          |--- testing: 4294967295 ^ 9223372036854775807 ---
          |int(9223372032559808512)
          |--- testing: 4294967293 ^ 0 ---
          |int(4294967293)
          |--- testing: 4294967293 ^ 1 ---
          |int(4294967292)
          |--- testing: 4294967293 ^ -1 ---
          |int(-4294967294)
          |--- testing: 4294967293 ^ 7 ---
          |int(4294967290)
          |--- testing: 4294967293 ^ 9 ---
          |int(4294967284)
          |--- testing: 4294967293 ^ 65 ---
          |int(4294967228)
          |--- testing: 4294967293 ^ -44 ---
          |int(-4294967255)
          |--- testing: 4294967293 ^ 2147483647 ---
          |int(2147483650)
          |--- testing: 4294967293 ^ 9223372036854775807 ---
          |int(9223372032559808514)
          |--- testing: 9223372036854775806 ^ 0 ---
          |int(9223372036854775806)
          |--- testing: 9223372036854775806 ^ 1 ---
          |int(9223372036854775807)
          |--- testing: 9223372036854775806 ^ -1 ---
          |int(-9223372036854775807)
          |--- testing: 9223372036854775806 ^ 7 ---
          |int(9223372036854775801)
          |--- testing: 9223372036854775806 ^ 9 ---
          |int(9223372036854775799)
          |--- testing: 9223372036854775806 ^ 65 ---
          |int(9223372036854775743)
          |--- testing: 9223372036854775806 ^ -44 ---
          |int(-9223372036854775766)
          |--- testing: 9223372036854775806 ^ 2147483647 ---
          |int(9223372034707292161)
          |--- testing: 9223372036854775806 ^ 9223372036854775807 ---
          |int(1)
          |--- testing: 9.2233720368548E+18 ^ 0 ---
          |int(-9223372036854775808)
          |--- testing: 9.2233720368548E+18 ^ 1 ---
          |int(-9223372036854775807)
          |--- testing: 9.2233720368548E+18 ^ -1 ---
          |int(9223372036854775807)
          |--- testing: 9.2233720368548E+18 ^ 7 ---
          |int(-9223372036854775801)
          |--- testing: 9.2233720368548E+18 ^ 9 ---
          |int(-9223372036854775799)
          |--- testing: 9.2233720368548E+18 ^ 65 ---
          |int(-9223372036854775743)
          |--- testing: 9.2233720368548E+18 ^ -44 ---
          |int(9223372036854775764)
          |--- testing: 9.2233720368548E+18 ^ 2147483647 ---
          |int(-9223372034707292161)
          |--- testing: 9.2233720368548E+18 ^ 9223372036854775807 ---
          |int(-1)
          |--- testing: -9223372036854775807 ^ 0 ---
          |int(-9223372036854775807)
          |--- testing: -9223372036854775807 ^ 1 ---
          |int(-9223372036854775808)
          |--- testing: -9223372036854775807 ^ -1 ---
          |int(9223372036854775806)
          |--- testing: -9223372036854775807 ^ 7 ---
          |int(-9223372036854775802)
          |--- testing: -9223372036854775807 ^ 9 ---
          |int(-9223372036854775800)
          |--- testing: -9223372036854775807 ^ 65 ---
          |int(-9223372036854775744)
          |--- testing: -9223372036854775807 ^ -44 ---
          |int(9223372036854775765)
          |--- testing: -9223372036854775807 ^ 2147483647 ---
          |int(-9223372034707292162)
          |--- testing: -9223372036854775807 ^ 9223372036854775807 ---
          |int(-2)
          |--- testing: -9.2233720368548E+18 ^ 0 ---
          |int(-9223372036854775808)
          |--- testing: -9.2233720368548E+18 ^ 1 ---
          |int(-9223372036854775807)
          |--- testing: -9.2233720368548E+18 ^ -1 ---
          |int(9223372036854775807)
          |--- testing: -9.2233720368548E+18 ^ 7 ---
          |int(-9223372036854775801)
          |--- testing: -9.2233720368548E+18 ^ 9 ---
          |int(-9223372036854775799)
          |--- testing: -9.2233720368548E+18 ^ 65 ---
          |int(-9223372036854775743)
          |--- testing: -9.2233720368548E+18 ^ -44 ---
          |int(9223372036854775764)
          |--- testing: -9.2233720368548E+18 ^ 2147483647 ---
          |int(-9223372034707292161)
          |--- testing: -9.2233720368548E+18 ^ 9223372036854775807 ---
          |int(-1)
          |--- testing: 0 ^ 9223372036854775807 ---
          |int(9223372036854775807)
          |--- testing: 0 ^ -9223372036854775808 ---
          |int(-9223372036854775808)
          |--- testing: 0 ^ 2147483647 ---
          |int(2147483647)
          |--- testing: 0 ^ -2147483648 ---
          |int(-2147483648)
          |--- testing: 0 ^ 9223372034707292160 ---
          |int(9223372034707292160)
          |--- testing: 0 ^ -9223372034707292160 ---
          |int(-9223372034707292160)
          |--- testing: 0 ^ 2147483648 ---
          |int(2147483648)
          |--- testing: 0 ^ -2147483649 ---
          |int(-2147483649)
          |--- testing: 0 ^ 4294967294 ---
          |int(4294967294)
          |--- testing: 0 ^ 4294967295 ---
          |int(4294967295)
          |--- testing: 0 ^ 4294967293 ---
          |int(4294967293)
          |--- testing: 0 ^ 9223372036854775806 ---
          |int(9223372036854775806)
          |--- testing: 0 ^ 9.2233720368548E+18 ---
          |int(-9223372036854775808)
          |--- testing: 0 ^ -9223372036854775807 ---
          |int(-9223372036854775807)
          |--- testing: 0 ^ -9.2233720368548E+18 ---
          |int(-9223372036854775808)
          |--- testing: 1 ^ 9223372036854775807 ---
          |int(9223372036854775806)
          |--- testing: 1 ^ -9223372036854775808 ---
          |int(-9223372036854775807)
          |--- testing: 1 ^ 2147483647 ---
          |int(2147483646)
          |--- testing: 1 ^ -2147483648 ---
          |int(-2147483647)
          |--- testing: 1 ^ 9223372034707292160 ---
          |int(9223372034707292161)
          |--- testing: 1 ^ -9223372034707292160 ---
          |int(-9223372034707292159)
          |--- testing: 1 ^ 2147483648 ---
          |int(2147483649)
          |--- testing: 1 ^ -2147483649 ---
          |int(-2147483650)
          |--- testing: 1 ^ 4294967294 ---
          |int(4294967295)
          |--- testing: 1 ^ 4294967295 ---
          |int(4294967294)
          |--- testing: 1 ^ 4294967293 ---
          |int(4294967292)
          |--- testing: 1 ^ 9223372036854775806 ---
          |int(9223372036854775807)
          |--- testing: 1 ^ 9.2233720368548E+18 ---
          |int(-9223372036854775807)
          |--- testing: 1 ^ -9223372036854775807 ---
          |int(-9223372036854775808)
          |--- testing: 1 ^ -9.2233720368548E+18 ---
          |int(-9223372036854775807)
          |--- testing: -1 ^ 9223372036854775807 ---
          |int(-9223372036854775808)
          |--- testing: -1 ^ -9223372036854775808 ---
          |int(9223372036854775807)
          |--- testing: -1 ^ 2147483647 ---
          |int(-2147483648)
          |--- testing: -1 ^ -2147483648 ---
          |int(2147483647)
          |--- testing: -1 ^ 9223372034707292160 ---
          |int(-9223372034707292161)
          |--- testing: -1 ^ -9223372034707292160 ---
          |int(9223372034707292159)
          |--- testing: -1 ^ 2147483648 ---
          |int(-2147483649)
          |--- testing: -1 ^ -2147483649 ---
          |int(2147483648)
          |--- testing: -1 ^ 4294967294 ---
          |int(-4294967295)
          |--- testing: -1 ^ 4294967295 ---
          |int(-4294967296)
          |--- testing: -1 ^ 4294967293 ---
          |int(-4294967294)
          |--- testing: -1 ^ 9223372036854775806 ---
          |int(-9223372036854775807)
          |--- testing: -1 ^ 9.2233720368548E+18 ---
          |int(9223372036854775807)
          |--- testing: -1 ^ -9223372036854775807 ---
          |int(9223372036854775806)
          |--- testing: -1 ^ -9.2233720368548E+18 ---
          |int(9223372036854775807)
          |--- testing: 7 ^ 9223372036854775807 ---
          |int(9223372036854775800)
          |--- testing: 7 ^ -9223372036854775808 ---
          |int(-9223372036854775801)
          |--- testing: 7 ^ 2147483647 ---
          |int(2147483640)
          |--- testing: 7 ^ -2147483648 ---
          |int(-2147483641)
          |--- testing: 7 ^ 9223372034707292160 ---
          |int(9223372034707292167)
          |--- testing: 7 ^ -9223372034707292160 ---
          |int(-9223372034707292153)
          |--- testing: 7 ^ 2147483648 ---
          |int(2147483655)
          |--- testing: 7 ^ -2147483649 ---
          |int(-2147483656)
          |--- testing: 7 ^ 4294967294 ---
          |int(4294967289)
          |--- testing: 7 ^ 4294967295 ---
          |int(4294967288)
          |--- testing: 7 ^ 4294967293 ---
          |int(4294967290)
          |--- testing: 7 ^ 9223372036854775806 ---
          |int(9223372036854775801)
          |--- testing: 7 ^ 9.2233720368548E+18 ---
          |int(-9223372036854775801)
          |--- testing: 7 ^ -9223372036854775807 ---
          |int(-9223372036854775802)
          |--- testing: 7 ^ -9.2233720368548E+18 ---
          |int(-9223372036854775801)
          |--- testing: 9 ^ 9223372036854775807 ---
          |int(9223372036854775798)
          |--- testing: 9 ^ -9223372036854775808 ---
          |int(-9223372036854775799)
          |--- testing: 9 ^ 2147483647 ---
          |int(2147483638)
          |--- testing: 9 ^ -2147483648 ---
          |int(-2147483639)
          |--- testing: 9 ^ 9223372034707292160 ---
          |int(9223372034707292169)
          |--- testing: 9 ^ -9223372034707292160 ---
          |int(-9223372034707292151)
          |--- testing: 9 ^ 2147483648 ---
          |int(2147483657)
          |--- testing: 9 ^ -2147483649 ---
          |int(-2147483658)
          |--- testing: 9 ^ 4294967294 ---
          |int(4294967287)
          |--- testing: 9 ^ 4294967295 ---
          |int(4294967286)
          |--- testing: 9 ^ 4294967293 ---
          |int(4294967284)
          |--- testing: 9 ^ 9223372036854775806 ---
          |int(9223372036854775799)
          |--- testing: 9 ^ 9.2233720368548E+18 ---
          |int(-9223372036854775799)
          |--- testing: 9 ^ -9223372036854775807 ---
          |int(-9223372036854775800)
          |--- testing: 9 ^ -9.2233720368548E+18 ---
          |int(-9223372036854775799)
          |--- testing: 65 ^ 9223372036854775807 ---
          |int(9223372036854775742)
          |--- testing: 65 ^ -9223372036854775808 ---
          |int(-9223372036854775743)
          |--- testing: 65 ^ 2147483647 ---
          |int(2147483582)
          |--- testing: 65 ^ -2147483648 ---
          |int(-2147483583)
          |--- testing: 65 ^ 9223372034707292160 ---
          |int(9223372034707292225)
          |--- testing: 65 ^ -9223372034707292160 ---
          |int(-9223372034707292095)
          |--- testing: 65 ^ 2147483648 ---
          |int(2147483713)
          |--- testing: 65 ^ -2147483649 ---
          |int(-2147483714)
          |--- testing: 65 ^ 4294967294 ---
          |int(4294967231)
          |--- testing: 65 ^ 4294967295 ---
          |int(4294967230)
          |--- testing: 65 ^ 4294967293 ---
          |int(4294967228)
          |--- testing: 65 ^ 9223372036854775806 ---
          |int(9223372036854775743)
          |--- testing: 65 ^ 9.2233720368548E+18 ---
          |int(-9223372036854775743)
          |--- testing: 65 ^ -9223372036854775807 ---
          |int(-9223372036854775744)
          |--- testing: 65 ^ -9.2233720368548E+18 ---
          |int(-9223372036854775743)
          |--- testing: -44 ^ 9223372036854775807 ---
          |int(-9223372036854775765)
          |--- testing: -44 ^ -9223372036854775808 ---
          |int(9223372036854775764)
          |--- testing: -44 ^ 2147483647 ---
          |int(-2147483605)
          |--- testing: -44 ^ -2147483648 ---
          |int(2147483604)
          |--- testing: -44 ^ 9223372034707292160 ---
          |int(-9223372034707292204)
          |--- testing: -44 ^ -9223372034707292160 ---
          |int(9223372034707292116)
          |--- testing: -44 ^ 2147483648 ---
          |int(-2147483692)
          |--- testing: -44 ^ -2147483649 ---
          |int(2147483691)
          |--- testing: -44 ^ 4294967294 ---
          |int(-4294967254)
          |--- testing: -44 ^ 4294967295 ---
          |int(-4294967253)
          |--- testing: -44 ^ 4294967293 ---
          |int(-4294967255)
          |--- testing: -44 ^ 9223372036854775806 ---
          |int(-9223372036854775766)
          |--- testing: -44 ^ 9.2233720368548E+18 ---
          |int(9223372036854775764)
          |--- testing: -44 ^ -9223372036854775807 ---
          |int(9223372036854775765)
          |--- testing: -44 ^ -9.2233720368548E+18 ---
          |int(9223372036854775764)
          |--- testing: 2147483647 ^ 9223372036854775807 ---
          |int(9223372034707292160)
          |--- testing: 2147483647 ^ -9223372036854775808 ---
          |int(-9223372034707292161)
          |--- testing: 2147483647 ^ 2147483647 ---
          |int(0)
          |--- testing: 2147483647 ^ -2147483648 ---
          |int(-1)
          |--- testing: 2147483647 ^ 9223372034707292160 ---
          |int(9223372036854775807)
          |--- testing: 2147483647 ^ -9223372034707292160 ---
          |int(-9223372032559808513)
          |--- testing: 2147483647 ^ 2147483648 ---
          |int(4294967295)
          |--- testing: 2147483647 ^ -2147483649 ---
          |int(-4294967296)
          |--- testing: 2147483647 ^ 4294967294 ---
          |int(2147483649)
          |--- testing: 2147483647 ^ 4294967295 ---
          |int(2147483648)
          |--- testing: 2147483647 ^ 4294967293 ---
          |int(2147483650)
          |--- testing: 2147483647 ^ 9223372036854775806 ---
          |int(9223372034707292161)
          |--- testing: 2147483647 ^ 9.2233720368548E+18 ---
          |int(-9223372034707292161)
          |--- testing: 2147483647 ^ -9223372036854775807 ---
          |int(-9223372034707292162)
          |--- testing: 2147483647 ^ -9.2233720368548E+18 ---
          |int(-9223372034707292161)
          |--- testing: 9223372036854775807 ^ 9223372036854775807 ---
          |int(0)
          |--- testing: 9223372036854775807 ^ -9223372036854775808 ---
          |int(-1)
          |--- testing: 9223372036854775807 ^ 2147483647 ---
          |int(9223372034707292160)
          |--- testing: 9223372036854775807 ^ -2147483648 ---
          |int(-9223372034707292161)
          |--- testing: 9223372036854775807 ^ 9223372034707292160 ---
          |int(2147483647)
          |--- testing: 9223372036854775807 ^ -9223372034707292160 ---
          |int(-2147483649)
          |--- testing: 9223372036854775807 ^ 2147483648 ---
          |int(9223372034707292159)
          |--- testing: 9223372036854775807 ^ -2147483649 ---
          |int(-9223372034707292160)
          |--- testing: 9223372036854775807 ^ 4294967294 ---
          |int(9223372032559808513)
          |--- testing: 9223372036854775807 ^ 4294967295 ---
          |int(9223372032559808512)
          |--- testing: 9223372036854775807 ^ 4294967293 ---
          |int(9223372032559808514)
          |--- testing: 9223372036854775807 ^ 9223372036854775806 ---
          |int(1)
          |--- testing: 9223372036854775807 ^ 9.2233720368548E+18 ---
          |int(-1)
          |--- testing: 9223372036854775807 ^ -9223372036854775807 ---
          |int(-2)
          |--- testing: 9223372036854775807 ^ -9.2233720368548E+18 ---
          |int(-1)
          |===DONE===""".stripMargin
      )
    }

    "Test ^ operator : various numbers as strings" in {
      // lang/operators/bitwiseXor_variationStr
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
          |	   echo "--- testing: '$strVal' ^ '$otherVal' ---\n";
          |      var_dump(bin2hex($strVal^$otherVal));
          |   }
          |}
          |
          |
          |?>
          |===DONE===""".stripMargin
      ).result must haveOutput(
        """--- testing: '0' ^ '0' ---
          |string(2) "00"
          |--- testing: '0' ^ '65' ---
          |string(2) "06"
          |--- testing: '0' ^ '-44' ---
          |string(2) "1d"
          |--- testing: '0' ^ '1.2' ---
          |string(2) "01"
          |--- testing: '0' ^ '-7.7' ---
          |string(2) "1d"
          |--- testing: '0' ^ 'abc' ---
          |string(2) "51"
          |--- testing: '0' ^ '123abc' ---
          |string(2) "01"
          |--- testing: '0' ^ '123e5' ---
          |string(2) "01"
          |--- testing: '0' ^ '123e5xyz' ---
          |string(2) "01"
          |--- testing: '0' ^ ' 123abc' ---
          |string(2) "10"
          |--- testing: '0' ^ '123 abc' ---
          |string(2) "01"
          |--- testing: '0' ^ '123abc ' ---
          |string(2) "01"
          |--- testing: '0' ^ '3.4a' ---
          |string(2) "03"
          |--- testing: '0' ^ 'a5.9' ---
          |string(2) "51"
          |--- testing: '65' ^ '0' ---
          |string(2) "06"
          |--- testing: '65' ^ '65' ---
          |string(4) "0000"
          |--- testing: '65' ^ '-44' ---
          |string(4) "1b01"
          |--- testing: '65' ^ '1.2' ---
          |string(4) "071b"
          |--- testing: '65' ^ '-7.7' ---
          |string(4) "1b02"
          |--- testing: '65' ^ 'abc' ---
          |string(4) "5757"
          |--- testing: '65' ^ '123abc' ---
          |string(4) "0707"
          |--- testing: '65' ^ '123e5' ---
          |string(4) "0707"
          |--- testing: '65' ^ '123e5xyz' ---
          |string(4) "0707"
          |--- testing: '65' ^ ' 123abc' ---
          |string(4) "1604"
          |--- testing: '65' ^ '123 abc' ---
          |string(4) "0707"
          |--- testing: '65' ^ '123abc ' ---
          |string(4) "0707"
          |--- testing: '65' ^ '3.4a' ---
          |string(4) "051b"
          |--- testing: '65' ^ 'a5.9' ---
          |string(4) "5700"
          |--- testing: '-44' ^ '0' ---
          |string(2) "1d"
          |--- testing: '-44' ^ '65' ---
          |string(4) "1b01"
          |--- testing: '-44' ^ '-44' ---
          |string(6) "000000"
          |--- testing: '-44' ^ '1.2' ---
          |string(6) "1c1a06"
          |--- testing: '-44' ^ '-7.7' ---
          |string(6) "00031a"
          |--- testing: '-44' ^ 'abc' ---
          |string(6) "4c5657"
          |--- testing: '-44' ^ '123abc' ---
          |string(6) "1c0607"
          |--- testing: '-44' ^ '123e5' ---
          |string(6) "1c0607"
          |--- testing: '-44' ^ '123e5xyz' ---
          |string(6) "1c0607"
          |--- testing: '-44' ^ ' 123abc' ---
          |string(6) "0d0506"
          |--- testing: '-44' ^ '123 abc' ---
          |string(6) "1c0607"
          |--- testing: '-44' ^ '123abc ' ---
          |string(6) "1c0607"
          |--- testing: '-44' ^ '3.4a' ---
          |string(6) "1e1a00"
          |--- testing: '-44' ^ 'a5.9' ---
          |string(6) "4c011a"
          |--- testing: '1.2' ^ '0' ---
          |string(2) "01"
          |--- testing: '1.2' ^ '65' ---
          |string(4) "071b"
          |--- testing: '1.2' ^ '-44' ---
          |string(6) "1c1a06"
          |--- testing: '1.2' ^ '1.2' ---
          |string(6) "000000"
          |--- testing: '1.2' ^ '-7.7' ---
          |string(6) "1c191c"
          |--- testing: '1.2' ^ 'abc' ---
          |string(6) "504c51"
          |--- testing: '1.2' ^ '123abc' ---
          |string(6) "001c01"
          |--- testing: '1.2' ^ '123e5' ---
          |string(6) "001c01"
          |--- testing: '1.2' ^ '123e5xyz' ---
          |string(6) "001c01"
          |--- testing: '1.2' ^ ' 123abc' ---
          |string(6) "111f00"
          |--- testing: '1.2' ^ '123 abc' ---
          |string(6) "001c01"
          |--- testing: '1.2' ^ '123abc ' ---
          |string(6) "001c01"
          |--- testing: '1.2' ^ '3.4a' ---
          |string(6) "020006"
          |--- testing: '1.2' ^ 'a5.9' ---
          |string(6) "501b1c"
          |--- testing: '-7.7' ^ '0' ---
          |string(2) "1d"
          |--- testing: '-7.7' ^ '65' ---
          |string(4) "1b02"
          |--- testing: '-7.7' ^ '-44' ---
          |string(6) "00031a"
          |--- testing: '-7.7' ^ '1.2' ---
          |string(6) "1c191c"
          |--- testing: '-7.7' ^ '-7.7' ---
          |string(8) "00000000"
          |--- testing: '-7.7' ^ 'abc' ---
          |string(6) "4c554d"
          |--- testing: '-7.7' ^ '123abc' ---
          |string(8) "1c051d56"
          |--- testing: '-7.7' ^ '123e5' ---
          |string(8) "1c051d52"
          |--- testing: '-7.7' ^ '123e5xyz' ---
          |string(8) "1c051d52"
          |--- testing: '-7.7' ^ ' 123abc' ---
          |string(8) "0d061c04"
          |--- testing: '-7.7' ^ '123 abc' ---
          |string(8) "1c051d17"
          |--- testing: '-7.7' ^ '123abc ' ---
          |string(8) "1c051d56"
          |--- testing: '-7.7' ^ '3.4a' ---
          |string(8) "1e191a56"
          |--- testing: '-7.7' ^ 'a5.9' ---
          |string(8) "4c02000e"
          |--- testing: 'abc' ^ '0' ---
          |string(2) "51"
          |--- testing: 'abc' ^ '65' ---
          |string(4) "5757"
          |--- testing: 'abc' ^ '-44' ---
          |string(6) "4c5657"
          |--- testing: 'abc' ^ '1.2' ---
          |string(6) "504c51"
          |--- testing: 'abc' ^ '-7.7' ---
          |string(6) "4c554d"
          |--- testing: 'abc' ^ 'abc' ---
          |string(6) "000000"
          |--- testing: 'abc' ^ '123abc' ---
          |string(6) "505050"
          |--- testing: 'abc' ^ '123e5' ---
          |string(6) "505050"
          |--- testing: 'abc' ^ '123e5xyz' ---
          |string(6) "505050"
          |--- testing: 'abc' ^ ' 123abc' ---
          |string(6) "415351"
          |--- testing: 'abc' ^ '123 abc' ---
          |string(6) "505050"
          |--- testing: 'abc' ^ '123abc ' ---
          |string(6) "505050"
          |--- testing: 'abc' ^ '3.4a' ---
          |string(6) "524c57"
          |--- testing: 'abc' ^ 'a5.9' ---
          |string(6) "00574d"
          |--- testing: '123abc' ^ '0' ---
          |string(2) "01"
          |--- testing: '123abc' ^ '65' ---
          |string(4) "0707"
          |--- testing: '123abc' ^ '-44' ---
          |string(6) "1c0607"
          |--- testing: '123abc' ^ '1.2' ---
          |string(6) "001c01"
          |--- testing: '123abc' ^ '-7.7' ---
          |string(8) "1c051d56"
          |--- testing: '123abc' ^ 'abc' ---
          |string(6) "505050"
          |--- testing: '123abc' ^ '123abc' ---
          |string(12) "000000000000"
          |--- testing: '123abc' ^ '123e5' ---
          |string(10) "0000000457"
          |--- testing: '123abc' ^ '123e5xyz' ---
          |string(12) "00000004571b"
          |--- testing: '123abc' ^ ' 123abc' ---
          |string(12) "110301520301"
          |--- testing: '123abc' ^ '123 abc' ---
          |string(12) "000000410301"
          |--- testing: '123abc' ^ '123abc ' ---
          |string(12) "000000000000"
          |--- testing: '123abc' ^ '3.4a' ---
          |string(8) "021c0700"
          |--- testing: '123abc' ^ 'a5.9' ---
          |string(8) "50071d58"
          |--- testing: '123e5' ^ '0' ---
          |string(2) "01"
          |--- testing: '123e5' ^ '65' ---
          |string(4) "0707"
          |--- testing: '123e5' ^ '-44' ---
          |string(6) "1c0607"
          |--- testing: '123e5' ^ '1.2' ---
          |string(6) "001c01"
          |--- testing: '123e5' ^ '-7.7' ---
          |string(8) "1c051d52"
          |--- testing: '123e5' ^ 'abc' ---
          |string(6) "505050"
          |--- testing: '123e5' ^ '123abc' ---
          |string(10) "0000000457"
          |--- testing: '123e5' ^ '123e5' ---
          |string(10) "0000000000"
          |--- testing: '123e5' ^ '123e5xyz' ---
          |string(10) "0000000000"
          |--- testing: '123e5' ^ ' 123abc' ---
          |string(10) "1103015654"
          |--- testing: '123e5' ^ '123 abc' ---
          |string(10) "0000004554"
          |--- testing: '123e5' ^ '123abc ' ---
          |string(10) "0000000457"
          |--- testing: '123e5' ^ '3.4a' ---
          |string(8) "021c0704"
          |--- testing: '123e5' ^ 'a5.9' ---
          |string(8) "50071d5c"
          |--- testing: '123e5xyz' ^ '0' ---
          |string(2) "01"
          |--- testing: '123e5xyz' ^ '65' ---
          |string(4) "0707"
          |--- testing: '123e5xyz' ^ '-44' ---
          |string(6) "1c0607"
          |--- testing: '123e5xyz' ^ '1.2' ---
          |string(6) "001c01"
          |--- testing: '123e5xyz' ^ '-7.7' ---
          |string(8) "1c051d52"
          |--- testing: '123e5xyz' ^ 'abc' ---
          |string(6) "505050"
          |--- testing: '123e5xyz' ^ '123abc' ---
          |string(12) "00000004571b"
          |--- testing: '123e5xyz' ^ '123e5' ---
          |string(10) "0000000000"
          |--- testing: '123e5xyz' ^ '123e5xyz' ---
          |string(16) "0000000000000000"
          |--- testing: '123e5xyz' ^ ' 123abc' ---
          |string(14) "11030156541a1a"
          |--- testing: '123e5xyz' ^ '123 abc' ---
          |string(14) "00000045541a1a"
          |--- testing: '123e5xyz' ^ '123abc ' ---
          |string(14) "00000004571b59"
          |--- testing: '123e5xyz' ^ '3.4a' ---
          |string(8) "021c0704"
          |--- testing: '123e5xyz' ^ 'a5.9' ---
          |string(8) "50071d5c"
          |--- testing: ' 123abc' ^ '0' ---
          |string(2) "10"
          |--- testing: ' 123abc' ^ '65' ---
          |string(4) "1604"
          |--- testing: ' 123abc' ^ '-44' ---
          |string(6) "0d0506"
          |--- testing: ' 123abc' ^ '1.2' ---
          |string(6) "111f00"
          |--- testing: ' 123abc' ^ '-7.7' ---
          |string(8) "0d061c04"
          |--- testing: ' 123abc' ^ 'abc' ---
          |string(6) "415351"
          |--- testing: ' 123abc' ^ '123abc' ---
          |string(12) "110301520301"
          |--- testing: ' 123abc' ^ '123e5' ---
          |string(10) "1103015654"
          |--- testing: ' 123abc' ^ '123e5xyz' ---
          |string(14) "11030156541a1a"
          |--- testing: ' 123abc' ^ ' 123abc' ---
          |string(14) "00000000000000"
          |--- testing: ' 123abc' ^ '123 abc' ---
          |string(14) "11030113000000"
          |--- testing: ' 123abc' ^ '123abc ' ---
          |string(14) "11030152030143"
          |--- testing: ' 123abc' ^ '3.4a' ---
          |string(8) "131f0652"
          |--- testing: ' 123abc' ^ 'a5.9' ---
          |string(8) "41041c0a"
          |--- testing: '123 abc' ^ '0' ---
          |string(2) "01"
          |--- testing: '123 abc' ^ '65' ---
          |string(4) "0707"
          |--- testing: '123 abc' ^ '-44' ---
          |string(6) "1c0607"
          |--- testing: '123 abc' ^ '1.2' ---
          |string(6) "001c01"
          |--- testing: '123 abc' ^ '-7.7' ---
          |string(8) "1c051d17"
          |--- testing: '123 abc' ^ 'abc' ---
          |string(6) "505050"
          |--- testing: '123 abc' ^ '123abc' ---
          |string(12) "000000410301"
          |--- testing: '123 abc' ^ '123e5' ---
          |string(10) "0000004554"
          |--- testing: '123 abc' ^ '123e5xyz' ---
          |string(14) "00000045541a1a"
          |--- testing: '123 abc' ^ ' 123abc' ---
          |string(14) "11030113000000"
          |--- testing: '123 abc' ^ '123 abc' ---
          |string(14) "00000000000000"
          |--- testing: '123 abc' ^ '123abc ' ---
          |string(14) "00000041030143"
          |--- testing: '123 abc' ^ '3.4a' ---
          |string(8) "021c0741"
          |--- testing: '123 abc' ^ 'a5.9' ---
          |string(8) "50071d19"
          |--- testing: '123abc ' ^ '0' ---
          |string(2) "01"
          |--- testing: '123abc ' ^ '65' ---
          |string(4) "0707"
          |--- testing: '123abc ' ^ '-44' ---
          |string(6) "1c0607"
          |--- testing: '123abc ' ^ '1.2' ---
          |string(6) "001c01"
          |--- testing: '123abc ' ^ '-7.7' ---
          |string(8) "1c051d56"
          |--- testing: '123abc ' ^ 'abc' ---
          |string(6) "505050"
          |--- testing: '123abc ' ^ '123abc' ---
          |string(12) "000000000000"
          |--- testing: '123abc ' ^ '123e5' ---
          |string(10) "0000000457"
          |--- testing: '123abc ' ^ '123e5xyz' ---
          |string(14) "00000004571b59"
          |--- testing: '123abc ' ^ ' 123abc' ---
          |string(14) "11030152030143"
          |--- testing: '123abc ' ^ '123 abc' ---
          |string(14) "00000041030143"
          |--- testing: '123abc ' ^ '123abc ' ---
          |string(14) "00000000000000"
          |--- testing: '123abc ' ^ '3.4a' ---
          |string(8) "021c0700"
          |--- testing: '123abc ' ^ 'a5.9' ---
          |string(8) "50071d58"
          |--- testing: '3.4a' ^ '0' ---
          |string(2) "03"
          |--- testing: '3.4a' ^ '65' ---
          |string(4) "051b"
          |--- testing: '3.4a' ^ '-44' ---
          |string(6) "1e1a00"
          |--- testing: '3.4a' ^ '1.2' ---
          |string(6) "020006"
          |--- testing: '3.4a' ^ '-7.7' ---
          |string(8) "1e191a56"
          |--- testing: '3.4a' ^ 'abc' ---
          |string(6) "524c57"
          |--- testing: '3.4a' ^ '123abc' ---
          |string(8) "021c0700"
          |--- testing: '3.4a' ^ '123e5' ---
          |string(8) "021c0704"
          |--- testing: '3.4a' ^ '123e5xyz' ---
          |string(8) "021c0704"
          |--- testing: '3.4a' ^ ' 123abc' ---
          |string(8) "131f0652"
          |--- testing: '3.4a' ^ '123 abc' ---
          |string(8) "021c0741"
          |--- testing: '3.4a' ^ '123abc ' ---
          |string(8) "021c0700"
          |--- testing: '3.4a' ^ '3.4a' ---
          |string(8) "00000000"
          |--- testing: '3.4a' ^ 'a5.9' ---
          |string(8) "521b1a58"
          |--- testing: 'a5.9' ^ '0' ---
          |string(2) "51"
          |--- testing: 'a5.9' ^ '65' ---
          |string(4) "5700"
          |--- testing: 'a5.9' ^ '-44' ---
          |string(6) "4c011a"
          |--- testing: 'a5.9' ^ '1.2' ---
          |string(6) "501b1c"
          |--- testing: 'a5.9' ^ '-7.7' ---
          |string(8) "4c02000e"
          |--- testing: 'a5.9' ^ 'abc' ---
          |string(6) "00574d"
          |--- testing: 'a5.9' ^ '123abc' ---
          |string(8) "50071d58"
          |--- testing: 'a5.9' ^ '123e5' ---
          |string(8) "50071d5c"
          |--- testing: 'a5.9' ^ '123e5xyz' ---
          |string(8) "50071d5c"
          |--- testing: 'a5.9' ^ ' 123abc' ---
          |string(8) "41041c0a"
          |--- testing: 'a5.9' ^ '123 abc' ---
          |string(8) "50071d19"
          |--- testing: 'a5.9' ^ '123abc ' ---
          |string(8) "50071d58"
          |--- testing: 'a5.9' ^ '3.4a' ---
          |string(8) "521b1a58"
          |--- testing: 'a5.9' ^ 'a5.9' ---
          |string(8) "00000000"
          |===DONE===""".stripMargin
      )
    }
  }
}
