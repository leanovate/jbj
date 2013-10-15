/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                 (Tests based on PHP)   */

package de.leanovate.jbj.core.tests.zend

import org.specs2.mutable.SpecificationWithJUnit
import de.leanovate.jbj.core.tests.TestJbjExecutor

class Closure4Spec extends SpecificationWithJUnit with TestJbjExecutor {
  "Closure tests 030-039" should {
    "Closure 030: Using lambda with variable variables" in {
      // Zend/tests/closure_030.phpt
      script(
        """<?php
          |
          |$b = function() { return func_get_args(); };
          |$a = 'b';
          |var_dump($$a(1));
          |var_dump($$a->__invoke(2));
          |
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """array(1) {
          |  [0]=>
          |  int(1)
          |}
          |array(1) {
          |  [0]=>
          |  int(2)
          |}
          |""".stripMargin
      )
    }

    "Closure 031: Closure properties with custom error handlers" in {
      // Zend/tests/closure_031.phpt
      script(
        """<?php
          |function foo($errno, $errstr, $errfile, $errline) {
          |	echo "Error: $errstr\n";
          |}
          |set_error_handler('foo');
          |$foo = function() {
          |};
          |var_dump($foo->a);
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """Error: Closure object cannot have properties
          |NULL
          |""".stripMargin
      )
    }

    "Closure 033: Dynamic closure property and private function" in {
      // Zend/tests/closure_033.phpt
      script(
        """<?php
          |
          |class Test {
          |	public $func;
          |	function __construct() {
          |		$this->func = function() {
          |			echo __METHOD__ . "()\n";
          |		};
          |	}
          |	private function func() {
          |		echo __METHOD__ . "()\n";
          |	}
          |}
          |
          |$o = new Test;
          |$f = $o->func;
          |$f();
          |$o->func();
          |
          |?>
          |===DONE===
          |""".stripMargin
      ).result must haveOutput(
        """Test::{closure}()
          |
          |Fatal error: Call to private method Test::func() from context '' in /zend/Closure4Spec.inlinePhp on line 18
          |""".stripMargin
      )
    }

    "Closure 033: Recursive var_dump on closures" in {
      // Zend/tests/closure_034.phpt
      script(
        """<?php
          |
          |$a = function () use(&$a) {};
          |var_dump($a);
          |
          |?>
          |===DONE===
          |""".stripMargin
      ).result must haveOutput(
        """object(Closure)#1 (1) {
          |  ["static"]=>
          |  array(1) {
          |    ["a"]=>
          |    *RECURSION*
          |  }
          |}
          |===DONE===
          |""".stripMargin
      )
    }

    "Testing recursion detection with Closures" in {
      // Zend/tests/closure_035.phpt
      script(
        """<?php
          |
          |$x = function () use (&$x) {
          |	$h = function () use ($x) {
          |		var_dump($x);
          |		return 1;
          |	};
          |	return $h();
          |};
          |
          |var_dump($x());
          |
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """object(Closure)#1 (1) {
          |  ["static"]=>
          |  array(1) {
          |    ["x"]=>
          |    *RECURSION*
          |  }
          |}
          |int(1)
          |""".stripMargin
      )
    }

    "Closure 036: Rebinding closures, keep calling scope" in {
      // Zend/tests/closure_036.phpt
      script(
        """<?php
          |
          |class A {
          |	private $x;
          |
          |	public function __construct($v) {
          |		$this->x = $v;
          |	}
          |
          |	public function getIncrementor() {
          |		return function() { return ++$this->x; };
          |	}
          |}
          |
          |$a = new A(0);
          |$b = new A(10);
          |
          |$ca = $a->getIncrementor();
          |$cb = $ca->bindTo($b);
          |$cb2 = Closure::bind($ca, $b);
          |
          |var_dump($ca());
          |var_dump($cb());
          |var_dump($cb2());
          |
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """int(1)
          |int(11)
          |int(12)
          |""".stripMargin
      )
    }

    "Closure 037: self:: and static:: within closures" in {
      // Zend/tests/closure_037.phpt
      script(
        """<?php
          |class A {
          |	private $x = 0;
          |
          |	function getClosure () {
          |			return function () {
          |				$this->x++;
          |				self::printX();
          |				self::print42();
          |				static::print42();
          |			};
          |	}
          |
          |	function printX () {
          |		echo $this->x."\n";
          |	}
          |
          |	function print42() {
          |		echo "42\n";
          |	}
          |}
          |
          |class B extends A {
          |	function print42() {
          |		echo "forty two\n";
          |	}
          |}
          |
          |$a = new A;
          |$closure = $a->getClosure();
          |$closure();
          |$b = new B;
          |$closure = $b->getClosure();
          |$closure();
          |?>
          |Done.
          |""".stripMargin
      ).result must haveOutput(
        """1
          |42
          |42
          |1
          |42
          |forty two
          |Done.
          |""".stripMargin
      )
    }
  }
}
