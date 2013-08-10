package de.leanovate.jbj.tests.classes

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.FreeSpec
import de.leanovate.jbj.tests.TestJbjExecutor
import org.scalatest.matchers.MustMatchers

@RunWith(classOf[JUnitRunner])
class PassByReferenceSpec extends FreeSpec with TestJbjExecutor with MustMatchers {
  "Pass by reference" - {
    "passing of function parameters by reference" in {
      // classes/passByReference_001
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
      // classes/passByReference_002
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
          |Fatal error: Only variables can be passed by reference in /classes/PassByReferenceSpec.inlinePhp on line 8
          |""".stripMargin
      )
    }
  }
}
