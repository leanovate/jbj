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

    "extending the same interface twice" in {
      // Zend/tests/objects_014.phpt
      script(
        """<?php
          |
          |interface foo {
          |}
          |
          |interface bar extends foo, foo {
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

    "comparing objects with strings/NULL" in {
      // Zend/tests/objects_015.phpt
      script(
        """<?php
          |
          |$o=new stdClass;
          |
          |var_dump($o == "");
          |var_dump($o != "");
          |var_dump($o <  "");
          |var_dump("" <  $o);
          |var_dump("" >  $o);
          |var_dump($o != null);
          |var_dump(is_null($o));
          |
          |?>
          |===DONE===
          |""".stripMargin
      ).result must haveOutput(
        """bool(false)
          |bool(true)
          |bool(false)
          |bool(true)
          |bool(false)
          |bool(true)
          |bool(false)
          |===DONE===
          |""".stripMargin
      )
    }

    "Testing visibility of object returned by function" in {
      // Zend/tests/objects_017.phpt
      script(
        """<?php
          |
          |class foo {
          |	private $test = 1;
          |}
          |
          |function test() {
          |	return new foo;
          |}
          |
          |test()->test = 2;
          |
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """
          |Fatal error: Cannot access private property foo::$test in /zend/Objects2Spec.inlinePhp on line 11
          |""".stripMargin
      )
    }

    "Using the same function name on interface with inheritance" in {
      // Zend/tests/objects_018.phpt
      script(
        """<?php
          |
          |interface Itest {
          |	function a();
          |}
          |
          |interface Itest2 {
          |	function a();
          |}
          |
          |interface Itest3 extends Itest, Itest2 {
          |}
          |
          |echo "done!\n";
          |
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """done!
          |""".stripMargin
      )
    }

    "Testing references of dynamic properties" in {
      // Zend/tests/objects_019.phpt
      script(
        """<?php
          |
          |error_reporting(E_ALL);
          |
          |$foo = array(new stdclass, new stdclass);
          |
          |$foo[1]->a = &$foo[0]->a;
          |$foo[0]->a = 2;
          |
          |$x = $foo[1]->a;
          |$x = 'foo';
          |
          |var_dump($foo, $x);
          |
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """array(2) {
          |  [0]=>
          |  object(stdClass)#1 (1) {
          |    ["a"]=>
          |    &int(2)
          |  }
          |  [1]=>
          |  object(stdClass)#2 (1) {
          |    ["a"]=>
          |    &int(2)
          |  }
          |}
          |string(3) "foo"
          |""".stripMargin
      )
    }
  }
}
