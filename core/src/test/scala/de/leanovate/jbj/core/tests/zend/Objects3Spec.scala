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

    "Testing magic methods __set, __get and __call in cascade" in {
      // Zend/tests/objects_021.phpt
      script(
        """<?php
          |
          |class test {
          |	static public $i = 0;
          |
          |	public function __construct() {
          |		self::$i++;
          |	}
          |
          |	public function __set($a, $b) {
          |		return x();
          |	}
          |
          |	public function __get($a) {
          |		return x();
          |	}
          |
          |	public function __call($a, $b) {
          |		return x();
          |	}
          |}
          |
          |function x() {
          |	return new test;
          |}
          |
          |x()
          |	->a
          |		->b()
          |			->c	= 1;
          |
          |var_dump(test::$i);
          |
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """int(4)
          |""".stripMargin
      )
    }

    "Testing 'self', 'parent' as type-hint" in {
      // Zend/tests/objects_022.phpt
      script(
        """<?php
          |
          |interface iTest { }
          |
          |class baz implements iTest {}
          |
          |class bar { }
          |
          |class foo extends bar {
          |    public function testFoo(self $obj) {
          |        var_dump($obj);
          |    }
          |    public function testBar(parent $obj) {
          |        var_dump($obj);
          |    }
          |    public function testBaz(iTest $obj) {
          |        var_dump($obj);
          |    }
          |}
          |
          |$foo = new foo;
          |$foo->testFoo(new foo);
          |$foo->testBar(new bar);
          |$foo->testBaz(new baz);
          |$foo->testFoo(new stdClass); // Catchable fatal error
          |
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """object(foo)#2 (0) {
          |}
          |object(bar)#3 (0) {
          |}
          |object(baz)#4 (0) {
          |}
          |
          |Catchable fatal error: Argument 1 passed to foo::testFoo() must be an instance of foo, instance of stdClass given, called in /zend/Objects3Spec.inlinePhp on line 25 and defined in /zend/Objects3Spec.inlinePhp on line 10
          |""".stripMargin
      )
    }
  }
}
