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
      // ../php-src/Zend/tests/closure_040.phpt
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
  }
}
