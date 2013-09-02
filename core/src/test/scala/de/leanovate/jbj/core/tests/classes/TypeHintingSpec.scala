/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                 (Tests based on PHP)   */

package de.leanovate.jbj.core.tests.classes

import org.specs2.mutable.SpecificationWithJUnit
import de.leanovate.jbj.core.tests.TestJbjExecutor

class TypeHintingSpec extends SpecificationWithJUnit with TestJbjExecutor {
  "type hinting" should {
    "ZE2 class type hinting" in {
      // classes/type_hinting_001.phpt
      script(
        """<?php
          |
          |interface Foo {
          |	function a(Foo $foo);
          |}
          |
          |interface Bar {
          |	function b(Bar $bar);
          |}
          |
          |class FooBar implements Foo, Bar {
          |	function a(Foo $foo) {
          |		// ...
          |	}
          |
          |	function b(Bar $bar) {
          |		// ...
          |	}
          |}
          |
          |class Blort {
          |}
          |
          |$a = new FooBar;
          |$b = new Blort;
          |
          |$a->a($b);
          |$a->b($b);
          |
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """
          |Catchable fatal error: Argument 1 passed to FooBar::a() must implement interface Foo, instance of Blort given, called in /classes/TypeHintingSpec.inlinePhp on line 27 and defined in /classes/TypeHintingSpec.inlinePhp on line 12
          |""".stripMargin
      )
    }
  }
}
