package de.leanovate.jbj.tests.lang

import de.leanovate.jbj.tests.TestJbjExecutor
import org.specs2.mutable.SpecificationWithJUnit

class PassByReferenceSpec extends SpecificationWithJUnit with TestJbjExecutor{
  "Pass by reference" should {
    "passing of function parameters by reference" in {
      // lang/passByReference_001
      script(
        """<?php
          |function f($arg1, &$arg2)
          |{
          |	var_dump($arg1++);
          |	var_dump($arg2++);
          |}
          |
          |function g (&$arg1, &$arg2)
          |{
          |	var_dump($arg1);
          |	var_dump($arg2);
          |}
          |$a = 7;
          |$b = 15;
          |
          |f($a, $b);
          |
          |var_dump($a);
          |var_dump($b);
          |
          |$c=array(1);
          |g($c,$c[0]);
          |
          |?>""".stripMargin
      ).result must haveOutput(
        """int(7)
          |int(15)
          |int(7)
          |int(16)
          |array(1) {
          |  [0]=>
          |  &int(1)
          |}
          |int(1)
          |""".stripMargin
      )
    }

    "Attempt to pass a constant by reference" in {
      // lang/passByReference_002
      script(
        """<?php
          |
          |function f(&$arg1)
          |{
          |	var_dump($arg1++);
          |}
          |
          |f(2);
          |
          |?>""".stripMargin
      ).result must haveOutput(
        """
          |Fatal error: Only variables can be passed by reference in /lang/PassByReferenceSpec.inlinePhp on line 8
          |""".stripMargin
      )
    }

    "Implicit initialisation when passing by reference" in {
      // lang/passByReference_003
      script(
        """<?php
          |function passbyVal($val) {
          |	echo "\nInside passbyVal call:\n";
          |	var_dump($val);
          |}
          |
          |function passbyRef(&$ref) {
          |	echo "\nInside passbyRef call:\n";
          |	var_dump($ref);
          |}
          |
          |echo "\nPassing undefined by value\n";
          |passbyVal($undef1[0]);
          |echo "\nAfter call\n";
          |var_dump($undef1);
          |
          |echo "\nPassing undefined by reference\n";
          |passbyRef($undef2[0]);
          |echo "\nAfter call\n";
          |var_dump($undef2)
          |?>""".stripMargin
      ).result must haveOutput(
        """
          |Passing undefined by value
          |
          |Notice: Undefined variable: undef1 in /lang/PassByReferenceSpec.inlinePhp on line 13
          |
          |Inside passbyVal call:
          |NULL
          |
          |After call
          |
          |Notice: Undefined variable: undef1 in /lang/PassByReferenceSpec.inlinePhp on line 15
          |NULL
          |
          |Passing undefined by reference
          |
          |Inside passbyRef call:
          |NULL
          |
          |After call
          |array(1) {
          |  [0]=>
          |  NULL
          |}
          |""".stripMargin
      )
    }

    "Attempt to pass a constant by reference" in {
      // lang/passByReference_004
      script(
        """<?php
          |
          |function foo(&$ref)
          |{
          |	var_dump($ref);
          |}
          |
          |function bar($value)
          |{
          |	return $value;
          |}
          |
          |foo(bar(5));
          |
          |?>""".stripMargin
      ).result must haveOutput(
        """
          |Strict Standards: Only variables should be passed by reference in /lang/PassByReferenceSpec.inlinePhp on line 13
          |int(5)
          |""".stripMargin
      )
    }
  }
}
