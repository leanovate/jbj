/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                 (Tests based on PHP)   */

package de.leanovate.jbj.core.tests.zend

import org.specs2.mutable.SpecificationWithJUnit
import de.leanovate.jbj.core.tests.TestJbjExecutor

class Objects1Spec extends SpecificationWithJUnit with TestJbjExecutor {
  "Objects tests 001-009" should {
    "comparing objects to other types" in {
      // Zend/tests/objects_001.phpt
      script(
        """<?php
          |
          |class Bar {
          |}
          |
          |$b = new Bar;
          |
          |var_dump($b == NULL);
          |var_dump($b != NULL);
          |var_dump($b == true);
          |var_dump($b != true);
          |var_dump($b == false);
          |var_dump($b != false);
          |var_dump($b == "");
          |var_dump($b != "");
          |var_dump($b == 0);
          |var_dump($b != 0);
          |var_dump($b == 1);
          |var_dump($b != 1);
          |var_dump($b == 1.0);
          |var_dump($b != 1.0);
          |var_dump($b == 1);
          |
          |
          |echo "Done\n";
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """bool(false)
          |bool(true)
          |bool(true)
          |bool(false)
          |bool(false)
          |bool(true)
          |bool(false)
          |bool(true)
          |
          |Notice: Object of class Bar could not be converted to int in /zend/Objects1Spec.inlinePhp on line 16
          |bool(false)
          |
          |Notice: Object of class Bar could not be converted to int in /zend/Objects1Spec.inlinePhp on line 17
          |bool(true)
          |
          |Notice: Object of class Bar could not be converted to int in /zend/Objects1Spec.inlinePhp on line 18
          |bool(true)
          |
          |Notice: Object of class Bar could not be converted to int in /zend/Objects1Spec.inlinePhp on line 19
          |bool(false)
          |
          |Notice: Object of class Bar could not be converted to double in /zend/Objects1Spec.inlinePhp on line 20
          |bool(true)
          |
          |Notice: Object of class Bar could not be converted to double in /zend/Objects1Spec.inlinePhp on line 21
          |bool(false)
          |
          |Notice: Object of class Bar could not be converted to int in /zend/Objects1Spec.inlinePhp on line 22
          |bool(true)
          |Done
          |""".stripMargin
      )
    }

    "method overloading with different method signature" in {
      // Zend/tests/objects_002.phpt
      script(
        """<?php
          |
          |class test {
          |	function foo() {}
          |}
          |
          |class test2 extends test {
          |	function foo() {}
          |}
          |
          |class test3 extends test {
          |	function foo($arg) {}
          |}
          |
          |echo "Done\n";
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """
          |Strict Standards: Declaration of test3::foo() should be compatible with test::foo() in /zend/Objects1Spec.inlinePhp on line 12
          |Done
          |""".stripMargin
      )
    }

    "method overloading with different method signature" in {
      // Zend/tests/objects_003.phpt
      script(
        """<?php
          |
          |class test {
          |	function foo($arg) {}
          |}
          |
          |class test2 extends test {
          |	function foo($arg) {}
          |}
          |
          |class test3 extends test {
          |	function foo($arg, $arg2) {}
          |}
          |
          |echo "Done\n";
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """
          |Strict Standards: Declaration of test3::foo() should be compatible with test::foo($arg) in /zend/Objects1Spec.inlinePhp on line 12
          |Done
          |""".stripMargin
      )
    }

    "method overloading with different method signature" in {
      // Zend/tests/objects_004.phpt
      script(
        """<?php
          |
          |class test {
          |	function foo($arg) {}
          |}
          |
          |class test2 extends test {
          |	function foo($arg) {}
          |}
          |
          |class test3 extends test {
          |	function foo(&$arg) {}
          |}
          |
          |echo "Done\n";
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """
          |Strict Standards: Declaration of test3::foo() should be compatible with test::foo($arg) in /zend/Objects1Spec.inlinePhp on line 12
          |Done
          |""".stripMargin
      )
    }

    "method overloading with different method signature" in {
      // Zend/tests/objects_005.phpt
      script(
        """<?php
          |
          |class test {
          |	function &foo() {}
          |}
          |
          |class test2 extends test {
          |	function &foo() {}
          |}
          |
          |class test3 extends test {
          |	function foo() {}
          |}
          |
          |echo "Done\n";
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """
          |Strict Standards: Declaration of test3::foo() should be compatible with & test::foo() in /zend/Objects1Spec.inlinePhp on line 12
          |Done
          |""".stripMargin
      )
    }
  }
}
