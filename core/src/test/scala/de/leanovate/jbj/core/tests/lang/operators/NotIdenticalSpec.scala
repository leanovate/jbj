package de.leanovate.jbj.core.tests.lang.operators

import org.specs2.mutable.SpecificationWithJUnit
import de.leanovate.jbj.core.tests.TestJbjExecutor

class NotIdenticalSpec extends SpecificationWithJUnit with TestJbjExecutor {
  "!== operator" should {
    "Test !== operator : different types" in {
      // lang/operators/operator_notidentical_basic.phpt
      script(
        """<?php
          |
          |$valid_true = array(1, "1", "true", 1.0, array(1));
          |$valid_false = array(0, "", 0.0, array(), NULL);
          |
          |$int1 = 679;
          |$int2 = -67835;
          |$valid_int1 = array("679", "679abc", " 679", "679  ", 679.0, 6.79E2, "+679", "6 7 9", "6y79", 678);
          |$valid_int2 = array("-67835", "-67835abc", " -67835", "-67835  ", -67835.000, -6.7835E4, "- 67835", "-67,835", "-67 835", "-678y35", -76834);
          |$invalid_int1 = array(679, +679);
          |$invalid_int2 = array(-67835);
          |
          |$float1 = 57385.45835;
          |$float2 = -67345.76567;
          |$valid_float1 = array("57385.45835",  "57385.45835aaa", "  57385.45835", "57385. 45835",  "57,385.45835", 57385.45834, 5.738545834e4);
          |$valid_float2 = array("-67345.76567", "-67345.76567aaa", "  -67345.76567", "- 67345.76567", "-67,345.76567", -67345.76566, -6.734576566E4);
          |$invalid_float1 = array(57385.45835, 5.738545835e4);
          |$invalid_float2 = array(-67345.76567, -6.734576567E4);
          |
          |
          |$toCompare = array(
          |  true, array_merge($valid_true, $valid_false), array(true),
          |  false, array_merge($valid_true, $valid_false), array(false),
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
          |      if ($typeToTest !== $compareVal) {
          |         // do nothing
          |      }
          |      else {
          |         echo "FAILED: '$typeToTest' === '$compareVal'\n";
          |         $failed = true;
          |      }
          |   }
          |
          |   foreach($invalid_compares as $compareVal) {
          |      if ($typeToTest !== $compareVal) {
          |         echo "FAILED: '$typeToTest' !== '$compareVal'\n";
          |         $failed = true;
          |      }
          |   }
          |
          |}
          |if ($failed == false) {
          |   echo "Test Passed\n";
          |}
          |
          |?>
          |===DONE===
          |""".stripMargin
      ).result must haveOutput(
        """Test Passed
          |===DONE===
          |""".stripMargin
      )
    }

    "Test !== operator : max int 64bit range" in {
      // lang/operators/operator_notidentical_variation_64bit.phpt
      script(
        """<?php
          |
          |define("MAX_64Bit", 9223372036854775807);
          |define("MAX_32Bit", 2147483647);
          |define("MIN_64Bit", -9223372036854775807 - 1);
          |define("MIN_32Bit", -2147483647 - 1);
          |
          |$invalidNotIdentical = array (
          |MAX_32Bit, array(MAX_32Bit),
          |MIN_32Bit, array(MIN_32Bit),
          |MAX_64Bit, array(MAX_64Bit),
          |MIN_64Bit, array(MIN_64Bit),
          |);
          |
          |$validNotIdentical = array (
          |MAX_32Bit, array("2147483647", "2147483647.0000000", 2.147483647e9, 2147483647.0, "2147483648", 2.1474836470001e9, MAX_32Bit - 1, MAX_32Bit + 1),
          |MIN_32Bit, array("-2147483648", "-2147483648.000", -2.147483648e9, -2147483648.0, "-2147483649", -2.1474836480001e9, MIN_32Bit -1, MIN_32Bit + 1),
          |MAX_64Bit, array(MAX_64Bit - 1, MAX_64Bit + 1),
          |MIN_64Bit, array(MIN_64Bit + 1, MIN_64Bit - 1),
          |);
          |
          |
          |$failed = false;
          |// test for valid values
          |for ($i = 0; $i < count($validNotIdentical); $i +=2) {
          |   $typeToTestVal = $validNotIdentical[$i];
          |   $compares = $validNotIdentical[$i + 1];
          |   foreach($compares as $compareVal) {
          |      if ($typeToTestVal !== $compareVal) {
          |         //Do Nothing
          |      }
          |      else {
          |         echo "FAILED: '$typeToTestVal' === '$compareVal'\n";
          |         $failed = true;
          |      }
          |   }
          |}
          |// test for invalid values
          |for ($i = 0; $i < count($invalidNotIdentical); $i +=2) {
          |   $typeToTestVal = $invalidNotIdentical[$i];
          |   $compares = $invalidNotIdentical[$i + 1];
          |   foreach($compares as $compareVal) {
          |      if ($typeToTestVal !== $compareVal) {
          |         echo "FAILED: '$typeToTestVal' !== '$compareVal'\n";
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
          |===DONE===
          |""".stripMargin
      ).result must haveOutput(
        """Test Passed
          |===DONE===
          |""".stripMargin
      )
    }
  }
}
