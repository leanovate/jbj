/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                 (Tests based on PHP)   */

/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                 (Tests based on PHP)   */

package de.leanovate.jbj.core.tests.classes

import org.specs2.mutable.SpecificationWithJUnit
import de.leanovate.jbj.core.tests.TestJbjExecutor

class CallSpec extends SpecificationWithJUnit with TestJbjExecutor {
  "__call method" should {
    "ZE2 __call()" in {
      // classes/__call_001
      script(
        """<?php
          |
          |class Caller {
          |	public $x = array(1, 2, 3);
          |
          |	function __call($m, $a) {
          |		echo "Method $m called:\n";
          |		var_dump($a);
          |		return $this->x;
          |	}
          |}
          |
          |$foo = new Caller();
          |$a = $foo->test(1, '2', 3.4, true);
          |var_dump($a);
          |
          |?>""".stripMargin
      ).result must haveOutput(
        """Method test called:
          |array(4) {
          |  [0]=>
          |  int(1)
          |  [1]=>
          |  string(1) "2"
          |  [2]=>
          |  float(3.4)
          |  [3]=>
          |  bool(true)
          |}
          |array(3) {
          |  [0]=>
          |  int(1)
          |  [1]=>
          |  int(2)
          |  [2]=>
          |  int(3)
          |}
          |""".stripMargin
      )
    }

    "ZE2 __call() signature check" in {
      // classes/__call_002
      script(
        """<?php
          |
          |class Test {
          |	function __call() {
          |	}
          |}
          |
          |?>""".stripMargin
      ).result must haveOutput(
        """
          |Fatal error: Method Test::__call() must take exactly 2 arguments in /classes/CallSpec.inlinePhp on line 4
          |""".stripMargin
      )
    }

    "Force pass-by-reference to __call" in {
      // classes/__call_003.phpt
      script(
        """<?php
          |  class C
          |  {
          |      function __call($name, $values)
          |      {
          |          $values[0][0] = 'changed';
          |      }
          |  }
          |
          |  $a = array('original');
          |
          |  $b = array('original');
          |  $hack =& $b[0];
          |
          |  $c = new C;
          |  $c->f($a);
          |  $c->f($b);
          |
          |  var_dump($a, $b);
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """array(1) {
          |  [0]=>
          |  string(8) "original"
          |}
          |array(1) {
          |  [0]=>
          |  &string(7) "changed"
          |}
          |""".stripMargin
      )
    }

    "When __call() is invoked via ::, ensure current scope's __call() is favoured over the specified class's  __call()." in {
      // classes/__call_004.phpt
      script(
        """<?php
          |class A {
          |	function __call($strMethod, $arrArgs) {
          |		echo "In " . __METHOD__ . "($strMethod, array(" . implode(',',$arrArgs) . "))\n";
          |		var_dump($this);
          |	}
          |}
          |
          |class B extends A {
          |	function __call($strMethod, $arrArgs) {
          |		echo "In " . __METHOD__ . "($strMethod, array(" . implode(',',$arrArgs) . "))\n";
          |		var_dump($this);
          |	}
          |
          |	function test() {
          |		A::test1(1,'a');
          |		B::test2(1,'a');
          |		self::test3(1,'a');
          |		parent::test4(1,'a');
          |	}
          |}
          |
          |$b = new B();
          |$b->test();
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """In B::__call(test1, array(1,a))
          |object(B)#1 (0) {
          |}
          |In B::__call(test2, array(1,a))
          |object(B)#1 (0) {
          |}
          |In B::__call(test3, array(1,a))
          |object(B)#1 (0) {
          |}
          |In B::__call(test4, array(1,a))
          |object(B)#1 (0) {
          |}
          |""".stripMargin
      )
    }

    "When __call() is invoked via ::, ensure private implementation of __call() in superclass is accessed without error." in {
      // classes/__call_005.phpt
      script(
        """<?php
          |class A {
          |	private function __call($strMethod, $arrArgs) {
          |		echo "In " . __METHOD__ . "($strMethod, array(" . implode(',',$arrArgs) . "))\n";
          |		var_dump($this);
          |	}
          |}
          |
          |class B extends A {
          |	function test() {
          |		A::test1(1,'a');
          |		B::test2(1,'a');
          |		self::test3(1,'a');
          |		parent::test4(1,'a');
          |	}
          |}
          |
          |$b = new B();
          |$b->test();
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """
          |Warning: The magic method __call() must have public visibility and cannot be static in /classes/CallSpec.inlinePhp on line 3
          |In A::__call(test1, array(1,a))
          |object(B)#1 (0) {
          |}
          |In A::__call(test2, array(1,a))
          |object(B)#1 (0) {
          |}
          |In A::__call(test3, array(1,a))
          |object(B)#1 (0) {
          |}
          |In A::__call(test4, array(1,a))
          |object(B)#1 (0) {
          |}
          |""".stripMargin
      )
    }
  }
}
