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

    "Testing invalid method names with __call and __callstatic" in {
      // Zend/tests/objects_025.phpt
      script(
        """<?php
          |
          |class foo {
          |	public function __call($a, $b) {
          |		print "non-static - ok\n";
          |	}
          |
          |	public static function __callstatic($a, $b) {
          |		print "static - ok\n";
          |	}
          |}
          |
          |$a = new foo;
          |$a->foooo();
          |$a::foooo();
          |
          |$b = 'aaaaa1';
          |$a->$b();
          |$a::$b();
          |
          |$b = '  ';
          |$a->$b();
          |$a::$b();
          |
          |$b = str_repeat('a', 10000);
          |$a->$b();
          |$a::$b();
          |
          |$b = NULL;
          |$a->$b();
          |
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """non-static - ok
          |static - ok
          |non-static - ok
          |static - ok
          |non-static - ok
          |static - ok
          |non-static - ok
          |static - ok
          |
          |Fatal error: Method name must be a string in /zend/Objects3Spec.inlinePhp on line 30
          |""".stripMargin
      )
    }

    "Using $this when out of context" in {
      // Zend/tests/objects_026.phpt
      script(
        """<?php
          |
          |try {
          |	$this->a = 1;
          |} catch (Exception $e) {
          |}
          |
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """
          |Fatal error: Using $this when not in object context in /zend/Objects3Spec.inlinePhp on line 4
          |""".stripMargin
      )
    }

    "Testing 'new static;' calling parent method" in {
      // Zend/tests/objects_027.phpt
      script(
        """<?php
          |
          |class bar {
          |	public function show() {
          |		var_dump(new static);
          |	}
          |}
          |
          |class foo extends bar {
          |	public function test() {
          |		parent::show();
          |	}
          |}
          |
          |$foo = new foo;
          |$foo->test();
          |$foo::test();
          |
          |call_user_func(array($foo, 'test'));
          |call_user_func(array('foo', 'test'));
          |
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """object(foo)#2 (0) {
          |}
          |
          |Strict Standards: Non-static method foo::test() should not be called statically in /zend/Objects3Spec.inlinePhp on line 17
          |
          |Strict Standards: Non-static method bar::show() should not be called statically in /zend/Objects3Spec.inlinePhp on line 11
          |object(foo)#3 (0) {
          |}
          |object(foo)#4 (0) {
          |}
          |
          |Strict Standards: call_user_func() expects parameter 1 to be a valid callback, non-static method foo::test() should not be called statically in /zend/Objects3Spec.inlinePhp on line 20
          |
          |Strict Standards: Non-static method bar::show() should not be called statically in /zend/Objects3Spec.inlinePhp on line 11
          |object(foo)#5 (0) {
          |}
          |""".stripMargin
      )
    }

    "Testing 'static::' and 'parent::' in calls" in {
      // Zend/tests/objects_028.phpt
      script(
        """<?php
          |
          |class bar {
          |	public function __call($a, $b) {
          |		print "hello\n";
          |	}
          |}
          |
          |class foo extends bar {
          |	public function __construct() {
          |		static::bar();
          |		parent::bar();
          |	}
          |}
          |
          |
          |new foo;
          |
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """hello
          |hello
          |""".stripMargin
      )
    }

    "Trying to access undeclared static property" in {
      // Zend/tests/objects_029.phpt
      script(
        """<?php
          |
          |class bar {
          |	public function __set($a, $b) {
          |		print "hello\n";
          |	}
          |}
          |
          |class foo extends bar {
          |	public function __construct() {
          |		static::$f = 1;
          |	}
          |	public function __set($a, $b) {
          |		print "foo\n";
          |	}
          |}
          |
          |
          |new foo;
          |
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """
          |Fatal error: Access to undeclared static property: foo::$f in /zend/Objects3Spec.inlinePhp on line 11
          |""".stripMargin
      )
    }

    "Trying to access undeclared parent property" in {
      // Zend/tests/objects_030.phpt
      script(
        """<?php
          |
          |class bar {
          |	public function __set($a, $b) {
          |		print "hello\n";
          |	}
          |}
          |
          |class foo extends bar {
          |	public function __construct() {
          |		parent::$f = 1;
          |	}
          |	public function __set($a, $b) {
          |		print "foo\n";
          |	}
          |}
          |
          |
          |new foo;
          |
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """
          |Fatal error: Access to undeclared static property: bar::$f in /zend/Objects3Spec.inlinePhp on line 11
          |""".stripMargin
      )
    }

    "Cloning stdClass" in {
      // Zend/tests/objects_031.phpt
      script(
        """<?php
          |
          |$x[] = clone new stdclass;
          |$x[] = clone new stdclass;
          |$x[] = clone new stdclass;
          |
          |$x[0]->a = 1;
          |
          |var_dump($x);
          |
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """array(3) {
          |  [0]=>
          |  object(stdClass)#2 (1) {
          |    ["a"]=>
          |    int(1)
          |  }
          |  [1]=>
          |  object(stdClass)#4 (0) {
          |  }
          |  [2]=>
          |  object(stdClass)#6 (0) {
          |  }
          |}
          |""".stripMargin
      )
    }
  }
}
