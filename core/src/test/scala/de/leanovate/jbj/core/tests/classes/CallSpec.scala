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
  }
}
