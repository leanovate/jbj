/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                 (Tests based on PHP)   */

package de.leanovate.jbj.core.tests.lang

import org.specs2.mutable.SpecificationWithJUnit
import de.leanovate.jbj.core.tests.TestJbjExecutor

class TypeHintSpec extends SpecificationWithJUnit with TestJbjExecutor {
  "type hint" should {
    "ZE2 type hinting" in {
      // lang/type_hints_001.phpt
      script(
        """<?php
          |
          |class Foo {
          |}
          |
          |class Bar {
          |}
          |
          |function type_hint_foo(Foo $a) {
          |}
          |
          |$foo = new Foo;
          |$bar = new Bar;
          |
          |type_hint_foo($foo);
          |type_hint_foo($bar);
          |
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """
          |Catchable fatal error: Argument 1 passed to type_hint_foo() must be an instance of Foo, instance of Bar given, called in /lang/TypeHintSpec.inlinePhp on line 16 and defined in /lang/TypeHintSpec.inlinePhp on line 9
          |""".stripMargin
      )
    }

    "ZE2 type hinting" in {
      // lang/type_hints_002.phpt
      script(
        """<?php
          |class P { }
          |class T {
          |	function f(P $p = NULL) {
          |		var_dump($p);
          |		echo "-\n";
          |	}
          |}
          |
          |$o=new T();
          |$o->f(new P);
          |$o->f();
          |$o->f(NULL);
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """object(P)#2 (0) {
          |}
          |-
          |NULL
          |-
          |NULL
          |-
          |""".stripMargin
      )
    }

    "ZE2 type hinting" in {
      // lang/type_hints_003.phpt
      script(
        """<?php
          |class T {
          |	function f(P $p = 42) {
          |	}
          |}
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """
          |Fatal error: Default value for parameters with a class type hint can only be NULL in /lang/TypeHintSpec.inlinePhp on line 3
          |""".stripMargin
      )
    }
  }
}
