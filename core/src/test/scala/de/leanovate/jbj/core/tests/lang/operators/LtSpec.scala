/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                 (Tests based on PHP)   */

package de.leanovate.jbj.core.tests.lang.operators

import org.specs2.mutable.SpecificationWithJUnit
import de.leanovate.jbj.core.tests.TestJbjExecutor

class LtSpec extends SpecificationWithJUnit with TestJbjExecutor{
  "< operator" should {
    "Test < operator : different types" in {
      // lang/operators/operator_lt_basic
      script(
        """<?php
          |$valid_true = array(1, "1", "true", 1.0, array(1));
          |$valid_false = array(0, "", 0.0, array(), NULL);
          |
          |$int1 = 677;
          |$int2 = -67837;
          |$valid_int1 = array("678", "678abc", " 678", "678  ", 678.0, 6.789E2, "+678", +678);
          |$valid_int2 = array("-67836", "-67836abc", " -67836", "-67836  ", -67835.0001, -6.78351E4);
          |$invalid_int1 = array(676, "676");
          |$invalid_int2 = array(-67837, "-67837");
          |
          |$float1 = 57385.45835;
          |$float2 = -67345.76567;
          |$valid_float1 = array("57385.45836",  "57385.45836aaa", "  57385.45836", 5.738545836e4);
          |$valid_float2 = array("-67345.76566", "-67345.76566aaa", "  -67345.76566", -6.734576566E4);
          |$invalid_float1 = array(57385.45835, 5.738545835e4);
          |$invalid_float2 = array(-67345.76567, -6.734576567E4);
          |
          |
          |$toCompare = array(
          |  false, $valid_true, $valid_false,
          |  $int1, $valid_int1, $invalid_int1,
          |  $int2, $valid_int2, $invalid_int2,
          |  $float1, $valid_float1, $invalid_float1,
          |  $float2, $valid_float2, $invalid_float2
          |);
          |
          |$failed = false;
          |for ($i = 0; $i < count($toCompare); $i +=3) {
          |   $typeToTest = $toCompare[$i];
          |   $valid_compares = $toCompare[$i + 1];
          |   $invalid_compares = $toCompare[$i + 2];
          |
          |   foreach($valid_compares as $compareVal) {
          |      if ($typeToTest < $compareVal) {
          |         // do nothing
          |      }
          |      else {
          |         echo "FAILED: '$typeToTest' >= '$compareVal'\n";
          |         $failed = true;
          |      }
          |   }
          |
          |   foreach($invalid_compares as $compareVal) {
          |      if ($typeToTest < $compareVal) {
          |         echo "FAILED: '$typeToTest' < '$compareVal'\n";
          |         $failed = true;
          |      }
          |   }
          |
          |}
          |if ($failed == false) {
          |   echo "Test Passed\n";
          |}
          |?>
          |===DONE===""".stripMargin
      ).result must haveOutput(
        """Test Passed
          |===DONE===""".stripMargin
      )
    }

    "Test < operator : max int 64bit range" in {
      // lang/operators/operator_lt_variation_64bit
      script(
        """<?php
          |
          |define("MAX_64Bit", 9223372036854775807);
          |define("MAX_32Bit", 2147483647);
          |define("MIN_64Bit", -9223372036854775807 - 1);
          |define("MIN_32Bit", -2147483647 - 1);
          |
          |$validLessThan = array (
          |2147483646, array(MAX_32Bit, "2147483647", "2147483647.001", 2.147483647e9, 2147483647.9),
          |MIN_32Bit, array(MIN_32Bit + 1, "-2147483647", "-2147483646.001", -2.1474836461e9, -2147483646.9),
          |);
          |
          |$invalidLessThan = array (
          |MAX_32Bit, array("2147483646", 2.1474836460001e9, MAX_32Bit - 1),
          |MIN_32Bit, array(MIN_32Bit - 1, "-2147483649", -2.1474836480001e9)
          |);
          |
          |$failed = false;
          |// test for equality
          |for ($i = 0; $i < count($validLessThan); $i +=2) {
          |   $typeToTestVal = $validLessThan[$i];
          |   $compares = $validLessThan[$i + 1];
          |   foreach($compares as $compareVal) {
          |      if ($typeToTestVal < $compareVal) {
          |         // do nothing
          |      }
          |      else {
          |         echo "FAILED: '$typeToTestVal' >= '$compareVal'\n";
          |         $failed = true;
          |      }
          |   }
          |}
          |// test for invalid values
          |for ($i = 0; $i < count($invalidLessThan); $i +=2) {
          |   $typeToTestVal = $invalidLessThan[$i];
          |   $compares = $invalidLessThan[$i + 1];
          |   foreach($compares as $compareVal) {
          |      if ($typeToTestVal < $compareVal) {
          |         echo "FAILED: '$typeToTestVal' < '$compareVal'\n";
          |         $failed = true;
          |      }
          |   }
          |}
          |
          |if ($failed == false) {
          |   echo "Test Passed\n";
          |}
          |
          |?>
          |===DONE===""".stripMargin
      ).result must haveOutput(
        """Test Passed
          |===DONE===""".stripMargin
      )
    }
  }
}
