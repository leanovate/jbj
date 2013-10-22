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

    "Creating instances dynamically" in {
      // Zend/tests/objects_023.phpt
      script(
        """<?php
          |
          |$arr = array(new stdClass, 'stdClass');
          |
          |new $arr[0]();
          |new $arr[1]();
          |
          |print "ok\n";
          |
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """ok
          |""".stripMargin
      )
    }

    "Testing direct assigning for property of object returned by function" in {
      // Zend/tests/objects_024.phpt
      script(
        """<?php
          |
          |class foo {
          |	static $bar = array();
          |
          |	public function __set($a, $b) {
          |		self::$bar[] = $b;
          |	}
          |
          |	public function __get($a) {
          |		/* last */
          |		return self::$bar[count(self::$bar)-1];
          |	}
          |}
          |
          |function test() {
          |	return new foo;
          |}
          |
          |$a = test()->bar = 1;
          |var_dump($a, count(foo::$bar), test()->whatever);
          |
          |print "\n";
          |
          |$a = test()->bar = NULL;
          |var_dump($a, count(foo::$bar), test()->whatever);
          |
          |print "\n";
          |
          |$a = test()->bar = test();
          |var_dump($a, count(foo::$bar), test()->whatever);
          |
          |print "\n";
          |
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """int(1)
          |int(1)
          |int(1)
          |
          |NULL
          |int(2)
          |NULL
          |
          |object(foo)#6 (0) {
          |}
          |int(3)
          |object(foo)#6 (0) {
          |}
          |
          |""".stripMargin
      )
    }
  }
}
