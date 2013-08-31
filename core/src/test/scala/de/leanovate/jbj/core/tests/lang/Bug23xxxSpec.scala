/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.core.tests.lang

import org.specs2.mutable.SpecificationWithJUnit
import de.leanovate.jbj.core.tests.TestJbjExecutor

class Bug23xxxSpec extends SpecificationWithJUnit with TestJbjExecutor {
  "Bugs #23xxx" should {
    "Bug #23384 (use of class constants in statics)" in {
      // lang/bug23384.phpt
      script(
        """<?php
          |define('TEN', 10);
          |class Foo {
          |    const HUN = 100;
          |    function test($x = Foo::HUN) {
          |        static $arr2 = array(TEN => 'ten');
          |        static $arr = array(Foo::HUN => 'ten');
          |
          |        print_r($arr);
          |        print_r($arr2);
          |        print_r($x);
          |    }
          |}
          |
          |Foo::test();
          |echo Foo::HUN."\n";
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """
          |Strict Standards: Non-static method Foo::test() should not be called statically in /lang/Bug23xxxSpec.inlinePhp on line 15
          |Array
          |(
          |    [100] => ten
          |)
          |Array
          |(
          |    [10] => ten
          |)
          |100100
          |""".stripMargin
      )
    }

    "Bug #23524 (Improper handling of constants in array indices)" in {
      // lang/bug23524.phpt
      script(
        """<?php
          |  echo "Begin\n";
          |  define("THE_CONST",123);
          |  function f($a=array(THE_CONST=>THE_CONST)) {
          |    print_r($a);
          |  }
          |  f();
          |  f();
          |  f();
          |  echo "Done";
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """Begin
          |Array
          |(
          |    [123] => 123
          |)
          |Array
          |(
          |    [123] => 123
          |)
          |Array
          |(
          |    [123] => 123
          |)
          |Done""".stripMargin
      )
    }
  }
}
