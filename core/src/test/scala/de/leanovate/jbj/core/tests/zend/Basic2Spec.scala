/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                 (Tests based on PHP)   */

package de.leanovate.jbj.core.tests.zend

import org.specs2.mutable.SpecificationWithJUnit
import de.leanovate.jbj.core.tests.TestJbjExecutor

class Basic2Spec extends SpecificationWithJUnit with TestJbjExecutor {
  "Basic tests 010-019" should {
    "get_parent_class() tests" in {
      // Zend/tests/010.phpt
      script(
        """<?php
          |
          |interface i {
          |	function test();
          |}
          |
          |class foo implements i {
          |	function test() {
          |		var_dump(get_parent_class());
          |	}
          |}
          |
          |class bar extends foo {
          |	function test_bar() {
          |		var_dump(get_parent_class());
          |	}
          |}
          |
          |$bar = new bar;
          |$foo = new foo;
          |
          |$foo->test();
          |$bar->test();
          |$bar->test_bar();
          |
          |var_dump(get_parent_class($bar));
          |var_dump(get_parent_class($foo));
          |var_dump(get_parent_class("bar"));
          |var_dump(get_parent_class("foo"));
          |var_dump(get_parent_class("i"));
          |
          |var_dump(get_parent_class(""));
          |var_dump(get_parent_class("[[[["));
          |var_dump(get_parent_class(" "));
          |var_dump(get_parent_class(new stdclass));
          |var_dump(get_parent_class(array()));
          |var_dump(get_parent_class(1));
          |
          |echo "Done\n";
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """bool(false)
          |bool(false)
          |string(3) "foo"
          |string(3) "foo"
          |bool(false)
          |string(3) "foo"
          |bool(false)
          |bool(false)
          |bool(false)
          |bool(false)
          |bool(false)
          |bool(false)
          |bool(false)
          |bool(false)
          |Done
          |""".stripMargin
      )
    }
  }
}
