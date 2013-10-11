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
  }
}
