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
  }
}
