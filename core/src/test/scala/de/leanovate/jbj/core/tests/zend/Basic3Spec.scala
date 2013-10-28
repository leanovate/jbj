/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                 (Tests based on PHP)   */

package de.leanovate.jbj.core.tests.zend

import org.specs2.mutable.SpecificationWithJUnit
import de.leanovate.jbj.core.tests.TestJbjExecutor

class Basic3Spec extends SpecificationWithJUnit with TestJbjExecutor {
  "Basic tests 020-029" should {
    "func_get_arg() invalid usage" in {
      // Zend/tests/020.phpt
      script(
        """<?php
          |
          |var_dump(func_get_arg(1,2,3));
          |var_dump(func_get_arg(1));
          |var_dump(func_get_arg());
          |
          |function bar() {
          |	var_dump(func_get_arg(1));
          |}
          |
          |function foo() {
          |	bar(func_get_arg(1));
          |}
          |
          |foo(1,2);
          |
          |echo "Done\n";
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """
          |Warning: func_get_arg() expects exactly 1 parameter, 3 given in /zend/Basic3Spec.inlinePhp on line 3
          |NULL
          |
          |Warning: func_get_arg():  Called from the global scope - no function context in /zend/Basic3Spec.inlinePhp on line 4
          |bool(false)
          |
          |Warning: func_get_arg() expects exactly 1 parameter, 0 given in /zend/Basic3Spec.inlinePhp on line 5
          |NULL
          |
          |Warning: func_get_arg():  Argument 1 not passed to function in /zend/Basic3Spec.inlinePhp on line 8
          |bool(false)
          |Done
          |""".stripMargin
      )
    }

    "?: operator" in {
      // Zend/tests/021.phpt
      script(
        """<?php
          |var_dump(true ?: false);
          |var_dump(false ?: true);
          |var_dump(23 ?: 42);
          |var_dump(0 ?: "bar");
          |
          |$a = 23;
          |$b = 0;
          |$c = "";
          |$d = 23.5;
          |
          |var_dump($a ?: $b);
          |var_dump($c ?: $d);
          |
          |var_dump(1 ?: print(2));
          |
          |$e = array();
          |
          |$e['e'] = 'e';
          |$e['e'] = $e['e'] ?: 'e';
          |print_r($e);
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """bool(true)
          |bool(true)
          |int(23)
          |string(3) "bar"
          |int(23)
          |float(23.5)
          |int(1)
          |Array
          |(
          |    [e] => e
          |)
          |""".stripMargin
      )
    }
  }
}
