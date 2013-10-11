/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                 (Tests based on PHP)   */

package de.leanovate.jbj.core.tests.zend

import org.specs2.mutable.SpecificationWithJUnit
import de.leanovate.jbj.core.tests.TestJbjExecutor

class Closure3Spec extends SpecificationWithJUnit with TestJbjExecutor {
  "Closure tests 020-029" should {
    "Closure 020: Trying to access private property outside class" in {
      // Zend/tests/closure_020.phpt
      script(
        """<?php
          |
          |class foo {
          |	private $test = 3;
          |
          |	public function x() {
          |		$a = &$this;
          |		$this->a = function() use (&$a) { return $a; };
          |		var_dump($this->a->__invoke());
          |		var_dump(is_a($this->a, 'closure'));
          |		var_dump(is_callable($this->a));
          |
          |		return $this->a;
          |	}
          |}
          |
          |$foo = new foo;
          |$y = $foo->x();
          |var_dump($y()->test);
          |
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """object(foo)#1 (2) {
          |  ["test":"foo":private]=>
          |  int(3)
          |  ["a"]=>
          |  object(Closure)#2 (2) {
          |    ["static"]=>
          |    array(1) {
          |      ["a"]=>
          |      *RECURSION*
          |    }
          |    ["this"]=>
          |    *RECURSION*
          |  }
          |}
          |bool(true)
          |bool(true)
          |
          |Fatal error: Cannot access private property foo::$test in /zend/Closure3Spec.inlinePhp on line 19
          |""".stripMargin
      )
    }

    "Closure 021: Throwing exception inside lambda" in {
      // Zend/tests/closure_021.phpt
      script(
        """<?php
          |
          |$foo = function() {
          |	try {
          |		throw new Exception('test!');
          |	} catch(Exception $e) {
          |		throw $e;
          |	}
          |};
          |
          |try {
          |	$foo();
          |} catch (Exception $e) {
          |	var_dump($e->getMessage());
          |}
          |
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """string(5) "test!"
          |""".stripMargin
      )
    }

    "Closure 022: Closure properties" in {
      // Zend/tests/closure_022.phpt
      script(
        """<?php
          |$a = 0;
          |$foo = function() use ($a) {
          |};
          |$foo->a = 1;
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """
          |Catchable fatal error: Closure object cannot have properties in /zend/Closure3Spec.inlinePhp on line 5
          |""".stripMargin
      )
    }

    "Closure 023: Closure declared in statically called method" in {
      // Zend/tests/closure_023.phpt
      script(
        """<?php
          |class foo {
          |    public static function bar() {
          |        $func = function() { echo "Done"; };
          |        $func();
          |    }
          |}
          |foo::bar();
          |""".stripMargin
      ).result must haveOutput(
        """Done""".stripMargin
      )
    }
  }
}
