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
  }
}
