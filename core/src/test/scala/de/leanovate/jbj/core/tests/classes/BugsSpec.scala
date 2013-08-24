package de.leanovate.jbj.core.tests.classes

import de.leanovate.jbj.core.tests.TestJbjExecutor
import org.specs2.mutable.SpecificationWithJUnit

class BugsSpec extends SpecificationWithJUnit with TestJbjExecutor {
  "Bugs" should {
    "Bug #23951 (Defines not working in inherited classes)" in {
      // classes/bug23951
      script(
        """<?php
          |
          |define('FOO1', 1);
          |define('FOO2', 2);
          |
          |class A {
          |
          |    public $a_var = array(FOO1=>'foo1_value', FOO2=>'foo2_value');
          |
          |}
          |
          |class B extends A {
          |
          |    public $b_var = 'foo';
          |
          |}
          |
          |$a = new A;
          |$b = new B;
          |
          |print_r($a);
          |print_r($b->a_var);
          |print_r($b->b_var);
          |
          |?>""".stripMargin
      ).result must haveOutput(
        """A Object
          |(
          |    [a_var] => Array
          |        (
          |            [1] => foo1_value
          |            [2] => foo2_value
          |        )
          |
          |)
          |Array
          |(
          |    [1] => foo1_value
          |    [2] => foo2_value
          |)
          |foo
          |""".stripMargin
      )
    }

    "Bug #24399 (is_subclass_of() crashes when parent class doesn't exist)" in {
      // classes/bug24399
      script(
        """<?php
          |class dooh {
          |    public $blah;
          |}
          |$d = new dooh;
          |var_dump(is_subclass_of($d, 'dooh'));
          |?>""".stripMargin
      ).result must haveOutput(
        """bool(false)
          |""".stripMargin
      )
    }

    "Bug #24445 (get_parent_class() returns the current class when passed an object)" in {
      // classes/bug24445
      script(
        """<?php
          |class Test { }
          |var_dump(get_parent_class('Test'));
          |$t = new Test;
          |var_dump(get_parent_class($t));
          |?>""".stripMargin
      ).result must haveOutput(
        """bool(false)
          |bool(false)
          |""".stripMargin
      )
    }
  }
}
