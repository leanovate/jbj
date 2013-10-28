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

    "Implementating abstracting methods and optional parameters" in {
      // Zend/tests/022.phpt
      script(
        """<?php
          |
          |abstract class Base
          |{
          |	abstract function someMethod($param);
          |}
          |
          |class Ext extends Base
          |{
          |	function someMethod($param = "default")
          |	{
          |		echo $param, "\n";
          |	}
          |}
          |
          |$a = new Ext();
          |$a->someMethod("foo");
          |$a->someMethod();
          |""".stripMargin
      ).result must haveOutput(
        """foo
          |default
          |""".stripMargin
      )
    }

    "Testing variable variables as function name" in {
      // Zend/tests/023.phpt
      script(
        """<?php
          |
          |$a = 'ucfirst';
          |$b = 'a';
          |print $$b('test');
          |print "\n";
          |
          |
          |class bar {
          |	public function a() {
          |		return "bar!";
          |	}
          |}
          |
          |class foo {
          |	public function test() {
          |		print "foo!\n";
          |		return new bar;
          |	}
          |}
          |
          |function test() {
          |	return new foo;
          |}
          |
          |$a = 'test';
          |$b = 'a';
          |var_dump($$b()->$$b()->$b());
          |
          |
          |$a = 'strtoupper';
          |$b = 'a';
          |$c = 'b';
          |$d = 'c';
          |var_dump($$$$d('foo'));
          |
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """Test
          |foo!
          |string(4) "bar!"
          |string(3) "FOO"
          |""".stripMargin
      )
    }

    "Testing operations with undefined variable" in {
      // Zend/tests/024.phpt
      script(
        """<?php
          |
          |var_dump($a[1]);
          |var_dump($a[$c]);
          |var_dump($a + 1);
          |var_dump($a + $b);
          |var_dump($a++);
          |var_dump(++$b);
          |var_dump($a->$b);
          |var_dump($a->$b);
          |var_dump($a->$b->$c[1]);
          |
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """
          |Notice: Undefined variable: a in /zend/Basic3Spec.inlinePhp on line 3
          |NULL
          |
          |Notice: Undefined variable: c in /zend/Basic3Spec.inlinePhp on line 4
          |
          |Notice: Undefined variable: a in /zend/Basic3Spec.inlinePhp on line 4
          |NULL
          |
          |Notice: Undefined variable: a in /zend/Basic3Spec.inlinePhp on line 5
          |int(1)
          |
          |Notice: Undefined variable: a in /zend/Basic3Spec.inlinePhp on line 6
          |
          |Notice: Undefined variable: b in /zend/Basic3Spec.inlinePhp on line 6
          |int(0)
          |
          |Notice: Undefined variable: a in /zend/Basic3Spec.inlinePhp on line 7
          |NULL
          |
          |Notice: Undefined variable: b in /zend/Basic3Spec.inlinePhp on line 8
          |int(1)
          |
          |Notice: Trying to get property of non-object in /zend/Basic3Spec.inlinePhp on line 9
          |NULL
          |
          |Notice: Trying to get property of non-object in /zend/Basic3Spec.inlinePhp on line 10
          |NULL
          |
          |Notice: Undefined variable: c in /zend/Basic3Spec.inlinePhp on line 11
          |
          |Notice: Trying to get property of non-object in /zend/Basic3Spec.inlinePhp on line 11
          |
          |Notice: Trying to get property of non-object in /zend/Basic3Spec.inlinePhp on line 11
          |NULL
          |""".stripMargin
      )
    }

    "Testing dynamic calls" in {
      // Zend/tests/025.phpt
      script(
        """<?php
          |
          |class foo {
          |	static public function a() {
          |		print "ok\n";
          |	}
          |}
          |
          |$a = 'a';
          |$b = 'a';
          |
          |$class = 'foo';
          |
          |foo::a();
          |foo::$a();
          |foo::$$b();
          |
          |$class::a();
          |$class::$a();
          |$class::$$b();
          |
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """ok
          |ok
          |ok
          |ok
          |ok
          |ok
          |""".stripMargin
      )
    }

    "Trying assign value to property when an object is not returned in a function" in {
      // Zend/tests/026.phpt
      script(
        """<?php
          |
          |class foo {
          |	public function a() {
          |	}
          |}
          |
          |$test = new foo;
          |
          |$test->a()->a;
          |print "ok\n";
          |
          |$test->a()->a = 1;
          |print "ok\n";
          |
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """
          |Notice: Trying to get property of non-object in /zend/Basic3Spec.inlinePhp on line 10
          |ok
          |
          |Warning: Creating default object from empty value in /zend/Basic3Spec.inlinePhp on line 13
          |ok
          |""".stripMargin
      )
    }

    "Testing dynamic calls using variable variables with curly syntax" in {
      // Zend/tests/027.phpt
      script(
        """<?php
          |
          |$a = 'b';
          |$b = 'c';
          |$c = 'strtoupper';
          |
          |var_dump(${${$a}}('foo') == 'FOO');
          |
          |$a = 'b';
          |$b = 'c';
          |$c = 'strtoupper';
          |$strtoupper = 'strtolower';
          |
          |var_dump(${${++$a}}('FOO') == 'foo');
          |
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """bool(true)
          |bool(true)
          |""".stripMargin
      )
    }

    "Testing function call through of array item" in {
      // Zend/tests/028.phpt
      script(
        """<?php
          |
          |$arr = array('strtoupper', 'strtolower');
          |
          |$k = 0;
          |
          |var_dump($arr[0]('foo') == 'FOO');
          |var_dump($arr[$k]('foo') == 'FOO');
          |var_dump($arr[++$k]('FOO') == 'foo');
          |var_dump($arr[++$k]('FOO') == 'foo');
          |
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """bool(true)
          |bool(true)
          |bool(true)
          |
          |Notice: Undefined offset: 2 in /zend/Basic3Spec.inlinePhp on line 10
          |
          |Fatal error: Function name must be a string in /zend/Basic3Spec.inlinePhp on line 10
          |""".stripMargin
      )
    }

    "Testing assign to property of an object in an array" in {
      // Zend/tests/029.phpt
      script(
        """<?php
          |
          |$arr = array(new stdClass);
          |
          |$arr[0]->a = clone $arr[0];
          |var_dump($arr);
          |
          |$arr[0]->b = new $arr[0];
          |var_dump($arr);
          |
          |$arr[0]->c = $arr[0]->a;
          |var_dump($arr);
          |
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """array(1) {
          |  [0]=>
          |  object(stdClass)#1 (1) {
          |    ["a"]=>
          |    object(stdClass)#2 (0) {
          |    }
          |  }
          |}
          |array(1) {
          |  [0]=>
          |  object(stdClass)#1 (2) {
          |    ["a"]=>
          |    object(stdClass)#2 (0) {
          |    }
          |    ["b"]=>
          |    object(stdClass)#3 (0) {
          |    }
          |  }
          |}
          |array(1) {
          |  [0]=>
          |  object(stdClass)#1 (3) {
          |    ["a"]=>
          |    object(stdClass)#2 (0) {
          |    }
          |    ["b"]=>
          |    object(stdClass)#3 (0) {
          |    }
          |    ["c"]=>
          |    object(stdClass)#2 (0) {
          |    }
          |  }
          |}
          |""".stripMargin
      )
    }
  }
}
