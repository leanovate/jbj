/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                 (Tests based on PHP)   */

package de.leanovate.jbj.core.tests.zend

import org.specs2.mutable.SpecificationWithJUnit
import de.leanovate.jbj.core.tests.TestJbjExecutor

class Objects3Spec extends SpecificationWithJUnit with TestJbjExecutor {
  "Objects tests 020-029" should {
    "Accessing members of standard object through of variable variable" in {
      // Zend/tests/objects_020.phpt
      script(
        """<?php
          |
          |error_reporting(E_ALL);
          |
          |$test = 'stdclass';
          |
          |$$test->a =& $$test;
          |$$test->a->b[] = 2;
          |
          |var_dump($$test);
          |
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """object(stdClass)#1 (2) {
          |  ["a"]=>
          |  *RECURSION*
          |  ["b"]=>
          |  array(1) {
          |    [0]=>
          |    int(2)
          |  }
          |}
          |""".stripMargin
      )
    }
  }
}
