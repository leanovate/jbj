/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                 (Tests based on PHP)   */

package de.leanovate.jbj.core.tests.lang.operators

import de.leanovate.jbj.core.tests.TestJbjExecutor
import org.specs2.mutable.SpecificationWithJUnit

class EqualsSpec extends SpecificationWithJUnit with TestJbjExecutor {
  "Equals operator" should {
    "Test == operator : different types" in {
      // lang/operators/operator_equals_basic
      script(
        """<?php
          |
          |$valid_true = array(1, "1", "true", 1.0, array(1));
          |$valid_false = array(0, "", 0.0, array(), NULL);
          |
          |$int1 = 679;
          |$int2 = -67835;
          |$valid_int1 = array("679", "679abc", " 679", "679  ", 679.0, 6.79E2, "+679", +679);
          |$valid_int2 = array("-67835", "-67835abc", " -67835", "-67835  ", -67835.000, -6.7835E4);
          |$invalid_int1 = array("6 7 9", "6y79", 678);
          |$invalid_int2 = array("- 67835", "-67,835", "-67 835", "-678y35", -76834);
          |
          |$float1 = 57385.45835;
          |$float2 = -67345.76567;
          |$valid_float1 = array("57385.45835",  "57385.45835aaa", "  57385.45835", 5.738545835e4);
          |$valid_float2 = array("-67345.76567", "-67345.76567aaa", "  -67345.76567", -6.734576567E4);
          |$invalid_float1 = array("57385. 45835",  "57,385.45835", 57385.45834, 5.738545834e4);
          |$invalid_float2 = array("- 67345.76567", "-67,345.76567", -67345.76566, -6.734576566E4);
          |
          |
          |$toCompare = array(
          |  true, $valid_true, $valid_false,
          |  false, $valid_false, $valid_true,
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
          |      if ($typeToTest == $compareVal) {
          |         // do nothing
          |      }
          |      else {
          |         echo "FAILED: '$typeToTest' != '$compareVal'\n";
          |         $failed = true;
          |      }
          |   }
          |
          |   foreach($invalid_compares as $compareVal) {
          |      if ($typeToTest == $compareVal) {
          |         echo "FAILED: '$typeToTest' == '$compareVal'\n";
          |         $failed = true;
          |      }
          |   }
          |
          |}
          |if ($failed == false) {
          |   echo "Test Passed\n";
          |}
          |
          |?>""".stripMargin
      ).result must haveOutput(
        """Test Passed
          |""".stripMargin
      )
    }

    "Test == operator : max int 64bit range" in {
      script(
        // lang/operators/operator_equals_variation_64bit
        """<?php
          |
          |define("MAX_64Bit", 9223372036854775807);
          |define("MAX_32Bit", 2147483647);
          |define("MIN_64Bit", -9223372036854775807 - 1);
          |define("MIN_32Bit", -2147483647 - 1);
          |
          |$validEqual = array (
          |MAX_32Bit, array(MAX_32Bit, "2147483647", "2147483647.0000000", 2.147483647e9),
          |MIN_32Bit, array(MIN_32Bit, "-2147483648", "-2147483648.000", -2.147483648e9),
          |MAX_64Bit, array(MAX_64Bit, MAX_64Bit + 1),
          |MIN_64Bit, array(MIN_64Bit, MIN_64Bit - 1),
          |);
          |
          |$invalidEqual = array (
          |MAX_32Bit, array("2147483648", 2.1474836470001e9, MAX_32Bit - 1, MAX_32Bit + 1),
          |MIN_32Bit, array("-2147483649", -2.1474836480001e9, MIN_32Bit -1, MIN_32Bit + 1),
          |MAX_64Bit, array(MAX_64Bit - 1),
          |MIN_64Bit, array(MIN_64Bit + 1),
          |);
          |
          |
          |$failed = false;
          |// test valid values
          |for ($i = 0; $i < count($validEqual); $i +=2) {
          |   $typeToTestVal = $validEqual[$i];
          |   $compares = $validEqual[$i + 1];
          |   foreach($compares as $compareVal) {
          |      if ($typeToTestVal == $compareVal) {
          |         // do nothing
          |      }
          |      else {
          |         echo "FAILED: '$typeToTestVal' != '$compareVal'\n";
          |         $failed = true;
          |      }
          |   }
          |}
          |// test invalid values
          |for ($i = 0; $i < count($invalidEqual); $i +=2) {
          |   $typeToTestVal = $invalidEqual[$i];
          |   $compares = $invalidEqual[$i + 1];
          |   foreach($compares as $compareVal) {
          |      if ($typeToTestVal == $compareVal) {
          |         echo "FAILED: '$typeToTestVal' == '$compareVal'\n";
          |         $failed = true;
          |      }
          |   }
          |}
          |
          |if ($failed == false) {
          |   echo "Test Passed\n";
          |}
          |
          |?>""".stripMargin
      ).result must haveOutput(
        """Test Passed
          |""".stripMargin
      )
    }
  }

}
