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

    "property_exists() tests" in {
      // Zend/tests/011.phpt
      script(
        """<?php
          |
          |class foo {
          |	public $pp1 = 1;
          |	private $pp2 = 2;
          |	protected $pp3 = 3;
          |
          |	function bar() {
          |		var_dump(property_exists("foo","pp1"));
          |		var_dump(property_exists("foo","pp2"));
          |		var_dump(property_exists("foo","pp3"));
          |	}
          |}
          |
          |class bar extends foo {
          |	function test() {
          |		var_dump(property_exists("foo","pp1"));
          |		var_dump(property_exists("foo","pp2"));
          |		var_dump(property_exists("foo","pp3"));
          |	}
          |}
          |
          |var_dump(property_exists());
          |var_dump(property_exists(""));
          |var_dump(property_exists("foo","pp1"));
          |var_dump(property_exists("foo","pp2"));
          |var_dump(property_exists("foo","pp3"));
          |var_dump(property_exists("foo","nonexistent"));
          |var_dump(property_exists("fo","nonexistent"));
          |var_dump(property_exists("foo",""));
          |var_dump(property_exists("","test"));
          |var_dump(property_exists("",""));
          |
          |$foo = new foo;
          |
          |var_dump(property_exists($foo,"pp1"));
          |var_dump(property_exists($foo,"pp2"));
          |var_dump(property_exists($foo,"pp3"));
          |var_dump(property_exists($foo,"nonexistent"));
          |var_dump(property_exists($foo,""));
          |var_dump(property_exists(array(),"test"));
          |var_dump(property_exists(1,"test"));
          |var_dump(property_exists(true,"test"));
          |
          |$foo->bar();
          |
          |$bar = new bar;
          |$bar->test();
          |
          |echo "Done\n";
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """
          |Warning: property_exists() expects exactly 2 parameters, 0 given in /zend/Basic2Spec.inlinePhp on line 23
          |NULL
          |
          |Warning: property_exists() expects exactly 2 parameters, 1 given in /zend/Basic2Spec.inlinePhp on line 24
          |NULL
          |bool(true)
          |bool(true)
          |bool(true)
          |bool(false)
          |bool(false)
          |bool(false)
          |bool(false)
          |bool(false)
          |bool(true)
          |bool(true)
          |bool(true)
          |bool(false)
          |bool(false)
          |
          |Warning: First parameter must either be an object or the name of an existing class in /zend/Basic2Spec.inlinePhp on line 41
          |NULL
          |
          |Warning: First parameter must either be an object or the name of an existing class in /zend/Basic2Spec.inlinePhp on line 42
          |NULL
          |
          |Warning: First parameter must either be an object or the name of an existing class in /zend/Basic2Spec.inlinePhp on line 43
          |NULL
          |bool(true)
          |bool(true)
          |bool(true)
          |bool(true)
          |bool(true)
          |bool(true)
          |Done
          |""".stripMargin
      )
    }

    "class_exists() tests" in {
      // Zend/tests/012.phpt
      script(
        """<?php
          |
          |class foo {
          |}
          |
          |var_dump(class_exists());
          |var_dump(class_exists("qwerty"));
          |var_dump(class_exists(""));
          |var_dump(class_exists(array()));
          |var_dump(class_exists("test", false));
          |var_dump(class_exists("foo", false));
          |var_dump(class_exists("foo"));
          |var_dump(class_exists("stdClass", false));
          |var_dump(class_exists("stdClass"));
          |
          |echo "Done\n";
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """
          |Warning: class_exists() expects at least 1 parameter, 0 given in /zend/Basic2Spec.inlinePhp on line 6
          |NULL
          |bool(false)
          |bool(false)
          |
          |Warning: class_exists() expects parameter 1 to be string, array given in /zend/Basic2Spec.inlinePhp on line 9
          |NULL
          |bool(false)
          |bool(true)
          |bool(true)
          |bool(true)
          |bool(true)
          |Done
          |""".stripMargin
      )
    }
  }
}
