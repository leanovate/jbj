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

    "redefining constructor (__construct first)" in {
      // Zend/tests/objects_011.phpt
      script(
        """<?php
          |
          |class test {
          |	function __construct() {
          |	}
          |	function test() {
          |	}
          |}
          |
          |echo "Done\n";
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """Done
          |""".stripMargin
      )
    }

    "implementing a class" in {
      // Zend/tests/objects_012.phpt
      script(
        """<?php
          |
          |class foo {
          |}
          |
          |interface bar extends foo {
          |}
          |
          |echo "Done\n";
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """
          |Fatal error: bar cannot implement foo - it is not an interface in /zend/Objects2Spec.inlinePhp on line 6
          |""".stripMargin
      )
    }

    "implementing the same interface twice" in {
      // Zend/tests/objects_013.phpt
      script(
        """<?php
          |
          |interface foo {
          |}
          |
          |class bar implements foo, foo {
          |}
          |
          |echo "Done\n";
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """
          |Fatal error: Class bar cannot implement previously implemented interface foo in /zend/Objects2Spec.inlinePhp on line 6
          |""".stripMargin
      )
    }
  }
}
