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
  }
}
