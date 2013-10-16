/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                 (Tests based on PHP)   */

package de.leanovate.jbj.core.tests.zend

import org.specs2.mutable.SpecificationWithJUnit
import de.leanovate.jbj.core.tests.TestJbjExecutor

class Closure5Spec extends SpecificationWithJUnit with TestJbjExecutor {
  "Closure tests 040-049" should {
    "Closure 040: Rebinding closures, bad arguments" in {
      // Zend/tests/closure_040.phpt
      script(
        """<?php
          |
          |class A {
          |	private $x;
          |	private static $xs = 10;
          |
          |	public function __construct($v) {
          |		$this->x = $v;
          |	}
          |
          |	public function getIncrementor() {
          |		return function() { return ++$this->x; };
          |	}
          |	public function getStaticIncrementor() {
          |		return static function() { return ++static::$xs; };
          |	}
          |}
          |
          |$a = new A(20);
          |
          |$ca = $a->getIncrementor();
          |$cas = $a->getStaticIncrementor();
          |
          |$ca->bindTo($a, array());
          |$ca->bindTo(array(), 'A');
          |$ca->bindTo($a, array(), "");
          |$ca->bindTo();
          |$cas->bindTo($a, 'A');
          |
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """
          |Notice: Array to string conversion in /zend/Closure5Spec.inlinePhp on line 24
          |
          |Warning: Class 'Array' not found in /zend/Closure5Spec.inlinePhp on line 24
          |
          |Warning: Closure::bindTo() expects parameter 1 to be object, array given in /zend/Closure5Spec.inlinePhp on line 25
          |
          |Warning: Closure::bindTo() expects at most 2 parameters, 3 given in /zend/Closure5Spec.inlinePhp on line 26
          |
          |Warning: Closure::bindTo() expects at least 1 parameter, 0 given in /zend/Closure5Spec.inlinePhp on line 27
          |
          |Warning: Cannot bind an instance to a static closure in /zend/Closure5Spec.inlinePhp on line 28
          |""".stripMargin
      )
    }

    "Closure 041: Rebinding: preservation of previous scope when not given as arg unless impossible" in {
      // Zend/tests/closure_041.phpt
      script(
        """<?php
          |
          |/* It's impossible to preserve the previous scope when doing so would break
          | * the invariants that, for non-static closures, having a scope is equivalent
          | * to having a bound instance. */
          |
          |$staticUnscoped = static function () {
          |	echo "scoped to A: "; var_dump(isset(A::$priv));
          |	echo "bound: ", isset($this)?get_class($this):"no";
          |};
          |
          |$nonstaticUnscoped = function () {
          |	echo "scoped to A: "; var_dump(isset(A::$priv));
          |	echo "bound: ", isset($this)?get_class($this):"no";
          |};
          |
          |class A {
          |	private static $priv = 7;
          |	function getClosure() {
          |		return function () {
          |			echo "scoped to A: "; var_dump(isset(A::$priv));
          |			echo "bound: ", isset($this)?get_class($this):"no";
          |		};
          |	}
          |	function getStaticClosure() {
          |		return static function () {
          |			echo "scoped to A: "; var_dump(isset(A::$priv));
          |			echo "bound: ", isset($this)?get_class($this):"no";
          |		};
          |	}
          |}
          |class B extends A {}
          |
          |$a = new A();
          |$staticScoped = $a->getStaticClosure();
          |$nonstaticScoped = $a->getClosure();
          |
          |echo "Before binding", "\n";
          |$staticUnscoped(); echo "\n";
          |$nonstaticUnscoped(); echo "\n";
          |$staticScoped(); echo "\n";
          |$nonstaticScoped(); echo "\n";
          |
          |echo "After binding, no instance", "\n";
          |$d = $staticUnscoped->bindTo(null); $d(); echo "\n";
          |$d = $nonstaticUnscoped->bindTo(null); $d(); echo "\n";
          |$d = $staticScoped->bindTo(null); $d(); echo "\n";
          |$d = $nonstaticScoped->bindTo(null); $d(); echo "\n";
          |//$d should have been turned to static
          |$d->bindTo($d);
          |
          |echo "After binding, with same-class instance for the bound ones", "\n";
          |$d = $staticUnscoped->bindTo(new A); $d(); echo "\n";
          |$d = $nonstaticUnscoped->bindTo(new A); $d(); echo " (should be scoped to dummy class)\n";
          |$d = $staticScoped->bindTo(new A); $d(); echo "\n";
          |$d = $nonstaticScoped->bindTo(new A); $d(); echo "\n";
          |
          |echo "After binding, with different instance for the bound ones", "\n";
          |$d = $nonstaticUnscoped->bindTo(new B); $d(); echo " (should be scoped to dummy class)\n";
          |$d = $nonstaticScoped->bindTo(new B); $d(); echo "\n";
          |
          |echo "Done.\n";
          |
          |""".stripMargin
      ).result must haveOutput(
        """Before binding
          |scoped to A: bool(false)
          |bound: no
          |scoped to A: bool(false)
          |bound: no
          |scoped to A: bool(true)
          |bound: no
          |scoped to A: bool(true)
          |bound: A
          |After binding, no instance
          |scoped to A: bool(false)
          |bound: no
          |scoped to A: bool(false)
          |bound: no
          |scoped to A: bool(true)
          |bound: no
          |scoped to A: bool(true)
          |bound: no
          |
          |Warning: Cannot bind an instance to a static closure in /zend/Closure5Spec.inlinePhp on line 50
          |After binding, with same-class instance for the bound ones
          |
          |Warning: Cannot bind an instance to a static closure in /zend/Closure5Spec.inlinePhp on line 53
          |scoped to A: bool(false)
          |bound: no
          |scoped to A: bool(false)
          |bound: A (should be scoped to dummy class)
          |
          |Warning: Cannot bind an instance to a static closure in /zend/Closure5Spec.inlinePhp on line 55
          |scoped to A: bool(true)
          |bound: no
          |scoped to A: bool(true)
          |bound: A
          |After binding, with different instance for the bound ones
          |scoped to A: bool(false)
          |bound: B (should be scoped to dummy class)
          |scoped to A: bool(true)
          |bound: B
          |Done.
          |""".stripMargin
      )
    }

    "Closure 043: Scope/bounding combination invariants; static closures" in {
      // Zend/tests/closure_043.phpt
      script(
        """<?php
          |/* Whether it's scoped or not, a static closure cannot have
          | * a bound instance. It should also not be automatically converted
          | * to a non-static instance when attempting to bind one */
          |
          |$staticUnscoped = static function () { var_dump(isset(A::$priv)); var_dump(isset($this)); };
          |
          |class A {
          |	private static $priv = 7;
          |	static function getStaticClosure() {
          |		return static function() { var_dump(isset(A::$priv)); var_dump(isset($this)); };
          |	}
          |}
          |
          |$staticScoped = A::getStaticClosure();
          |
          |echo "Before binding", "\n";
          |$staticUnscoped(); echo "\n";
          |$staticScoped(); echo "\n";
          |
          |echo "After binding, null scope, no instance", "\n";
          |$d = $staticUnscoped->bindTo(null, null); $d(); echo "\n";
          |$d = $staticScoped->bindTo(null, null); $d(); echo "\n";
          |
          |echo "After binding, null scope, with instance", "\n";
          |$d = $staticUnscoped->bindTo(new A, null); $d(); echo "\n";
          |$d = $staticScoped->bindTo(new A, null); $d(); echo "\n";
          |
          |echo "After binding, with scope, no instance", "\n";
          |$d = $staticUnscoped->bindTo(null, 'A'); $d(); echo "\n";
          |$d = $staticScoped->bindTo(null, 'A'); $d(); echo "\n";
          |
          |echo "After binding, with scope, with instance", "\n";
          |$d = $staticUnscoped->bindTo(new A, 'A'); $d(); echo "\n";
          |$d = $staticScoped->bindTo(new A, 'A'); $d(); echo "\n";
          |
          |echo "Done.\n";
          |
          |""".stripMargin
      ).result must haveOutput(
        """Before binding
          |bool(false)
          |bool(false)
          |
          |bool(true)
          |bool(false)
          |
          |After binding, null scope, no instance
          |bool(false)
          |bool(false)
          |
          |bool(false)
          |bool(false)
          |
          |After binding, null scope, with instance
          |
          |Warning: Cannot bind an instance to a static closure in /zend/Closure5Spec.inlinePhp on line 26
          |bool(false)
          |bool(false)
          |
          |
          |Warning: Cannot bind an instance to a static closure in /zend/Closure5Spec.inlinePhp on line 27
          |bool(false)
          |bool(false)
          |
          |After binding, with scope, no instance
          |bool(true)
          |bool(false)
          |
          |bool(true)
          |bool(false)
          |
          |After binding, with scope, with instance
          |
          |Warning: Cannot bind an instance to a static closure in /zend/Closure5Spec.inlinePhp on line 34
          |bool(true)
          |bool(false)
          |
          |
          |Warning: Cannot bind an instance to a static closure in /zend/Closure5Spec.inlinePhp on line 35
          |bool(true)
          |bool(false)
          |
          |Done.
          |""".stripMargin
      )
    }

    "Closure 044: Scope/bounding combination invariants; non static closures" in {
      // Zend/tests/closure_044.phpt
      script(
        """<?php
          |/* A non-static closure has a bound instance if it has a scope
          | * and does't have an instance if it has no scope */
          |
          |$nonstaticUnscoped = function () { var_dump(isset(A::$priv)); var_dump(isset($this)); };
          |
          |class A {
          |	private static $priv = 7;
          |	function getClosure() {
          |		return function() { var_dump(isset(A::$priv)); var_dump(isset($this)); };
          |	}
          |}
          |
          |$a = new A();
          |$nonstaticScoped = $a->getClosure();
          |
          |echo "Before binding", "\n";
          |$nonstaticUnscoped(); echo "\n";
          |$nonstaticScoped(); echo "\n";
          |
          |echo "After binding, null scope, no instance", "\n";
          |$d = $nonstaticUnscoped->bindTo(null, null); $d(); echo "\n";
          |$d = $nonstaticScoped->bindTo(null, null); $d(); echo "\n";
          |
          |echo "After binding, null scope, with instance", "\n";
          |$d = $nonstaticUnscoped->bindTo(new A, null); $d(); echo "\n";
          |$d = $nonstaticScoped->bindTo(new A, null); $d(); echo "\n";
          |
          |echo "After binding, with scope, no instance", "\n";
          |$d = $nonstaticUnscoped->bindTo(null, 'A'); $d(); echo "\n";
          |$d = $nonstaticScoped->bindTo(null, 'A'); $d(); echo "\n";
          |
          |echo "After binding, with scope, with instance", "\n";
          |$d = $nonstaticUnscoped->bindTo(new A, 'A'); $d(); echo "\n";
          |$d = $nonstaticScoped->bindTo(new A, 'A'); $d(); echo "\n";
          |
          |echo "Done.\n";
          |
          |""".stripMargin
      ).result must haveOutput(
        """Before binding
          |bool(false)
          |bool(false)
          |
          |bool(true)
          |bool(true)
          |
          |After binding, null scope, no instance
          |bool(false)
          |bool(false)
          |
          |bool(false)
          |bool(false)
          |
          |After binding, null scope, with instance
          |bool(false)
          |bool(true)
          |
          |bool(false)
          |bool(true)
          |
          |After binding, with scope, no instance
          |bool(true)
          |bool(false)
          |
          |bool(true)
          |bool(false)
          |
          |After binding, with scope, with instance
          |bool(true)
          |bool(true)
          |
          |bool(true)
          |bool(true)
          |
          |Done.
          |""".stripMargin
      )
    }

    "Closure 045: Closures created in static methods are static, even without the keyword" in {
      // Zend/tests/closure_045.phpt
      script(
        """<?php
          |
          |class A {
          |static function foo() {
          |	return function () {};
          |}
          |}
          |
          |$a = A::foo();
          |$a->bindTo(new A);
          |
          |echo "Done.\n";
          |
          |""".stripMargin
      ).result must haveOutput(
        """
          |Warning: Cannot bind an instance to a static closure in /zend/Closure5Spec.inlinePhp on line 10
          |Done.
          |""".stripMargin
      )
    }

    "Closure 046: Rebinding: preservation of previous scope when \"static\" given as scope arg (same as closure #041)" in {
      // Zend/tests/closure_046.phpt
      script(
        """<?php
          |
          |/* It's impossible to preserve the previous scope when doing so would break
          | * the invariants that, for non-static closures, having a scope is equivalent
          | * to having a bound instance. */
          |
          |$nonstaticUnscoped = function () { var_dump(isset(A::$priv)); var_dump(isset($this)); };
          |
          |class A {
          |	private static $priv = 7;
          |	function getClosure() {
          |		return function() { var_dump(isset(A::$priv)); var_dump(isset($this)); };
          |	}
          |}
          |class B extends A {}
          |
          |$a = new A();
          |$nonstaticScoped = $a->getClosure();
          |
          |echo "Before binding", "\n";
          |$nonstaticUnscoped(); echo "\n";
          |$nonstaticScoped(); echo "\n";
          |
          |echo "After binding, no instance", "\n";
          |$d = $nonstaticUnscoped->bindTo(null, "static"); $d(); echo "\n";
          |$d = $nonstaticScoped->bindTo(null, "static"); $d(); echo "\n";
          |//$d should have been turned to static
          |$d->bindTo($d);
          |
          |echo "After binding, with same-class instance for the bound one", "\n";
          |$d = $nonstaticUnscoped->bindTo(new A, "static"); $d(); echo "\n";
          |$d = $nonstaticScoped->bindTo(new A, "static"); $d(); echo "\n";
          |
          |echo "After binding, with different instance for the bound one", "\n";
          |$d = $nonstaticScoped->bindTo(new B, "static"); $d(); echo "\n";
          |
          |echo "Done.\n";
          |
          |""".stripMargin
      ).result must haveOutput(
        """Before binding
          |bool(false)
          |bool(false)
          |
          |bool(true)
          |bool(true)
          |
          |After binding, no instance
          |bool(false)
          |bool(false)
          |
          |bool(true)
          |bool(false)
          |
          |
          |Warning: Cannot bind an instance to a static closure in /zend/Closure5Spec.inlinePhp on line 28
          |After binding, with same-class instance for the bound one
          |bool(false)
          |bool(true)
          |
          |bool(true)
          |bool(true)
          |
          |After binding, with different instance for the bound one
          |bool(true)
          |bool(true)
          |
          |Done.
          |""".stripMargin
      )
    }
  }
}
