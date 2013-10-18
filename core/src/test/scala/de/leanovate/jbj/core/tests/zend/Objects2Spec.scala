/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                 (Tests based on PHP)   */

package de.leanovate.jbj.core.tests.zend

import org.specs2.mutable.SpecificationWithJUnit
import de.leanovate.jbj.core.tests.TestJbjExecutor

class Objects2Spec extends SpecificationWithJUnit with TestJbjExecutor {
  "Objects tests 010-019" should {
    "redefining constructor (__construct second)" in {
      // Zend/tests/objects_010.phpt
      script(
        """<?php
          |
          |class test {
          |	function test() {
          |	}
          |	function __construct() {
          |	}
          |}
          |
          |echo "Done\n";
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """
          |Strict Standards: Redefining already defined constructor for class test in /zend/Objects2Spec.inlinePhp on line 6
          |Done
          |""".stripMargin
      )
    }
  }
}
