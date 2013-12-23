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

    "interface_exists() tests" in {
      // Zend/tests/013.phpt
      script(
        """<?php
          |
          |interface foo {
          |}
          |
          |var_dump(interface_exists());
          |var_dump(interface_exists("qwerty"));
          |var_dump(interface_exists(""));
          |var_dump(interface_exists(array()));
          |var_dump(interface_exists("test", false));
          |var_dump(interface_exists("foo", false));
          |var_dump(interface_exists("foo"));
          |var_dump(interface_exists("stdClass", false));
          |var_dump(interface_exists("stdClass"));
          |
          |echo "Done\n";
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """
          |Warning: interface_exists() expects at least 1 parameter, 0 given in /zend/Basic2Spec.inlinePhp on line 6
          |NULL
          |bool(false)
          |bool(false)
          |
          |Warning: interface_exists() expects parameter 1 to be string, array given in /zend/Basic2Spec.inlinePhp on line 9
          |NULL
          |bool(false)
          |bool(true)
          |bool(true)
          |bool(false)
          |bool(false)
          |Done
          |""".stripMargin
      )
    }

    "get_included_files() tests" in {
      // Zend/tests/014.phpt
      script(
        """<?php
          |
          |var_dump(get_included_files());
          |
          |include(dirname(__FILE__)."/014.inc");
          |var_dump(get_included_files());
          |
          |var_dump(get_included_files(1,1));
          |
          |include_once(dirname(__FILE__)."/014.inc");
          |var_dump(get_included_files());
          |
          |var_dump(get_included_files(1));
          |
          |include(dirname(__FILE__)."/014.inc");
          |var_dump(get_included_files());
          |
          |echo "Done\n";
          |?>
          |""".stripMargin
      ).result must haveOutput(
        // This result differs from the original since we run our tests differently (i.e. without additional include
        """array(0) {
          |}
          |array(1) {
          |  [0]=>
          |  string(13) "/zend/014.inc"
          |}
          |
          |Warning: get_included_files() expects exactly 0 parameters, 2 given in /zend/Basic2Spec.inlinePhp on line 8
          |NULL
          |array(1) {
          |  [0]=>
          |  string(13) "/zend/014.inc"
          |}
          |
          |Warning: get_included_files() expects exactly 0 parameters, 1 given in /zend/Basic2Spec.inlinePhp on line 13
          |NULL
          |array(1) {
          |  [0]=>
          |  string(13) "/zend/014.inc"
          |}
          |Done
          |""".stripMargin
      )
    }

    "trigger_error() tests" in {
      // Zend/tests/015.phpt
      script(
        """<?php
          |
          |var_dump(trigger_error());
          |var_dump(trigger_error("error"));
          |var_dump(trigger_error(array()));
          |var_dump(trigger_error("error", -1));
          |var_dump(trigger_error("error", 0));
          |var_dump(trigger_error("error", E_USER_WARNING));
          |var_dump(trigger_error("error", E_USER_DEPRECATED));
          |
          |echo "Done\n";
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """
          |Warning: trigger_error() expects at least 1 parameter, 0 given in /zend/Basic2Spec.inlinePhp on line 3
          |NULL
          |
          |Notice: error in /zend/Basic2Spec.inlinePhp on line 4
          |bool(true)
          |
          |Warning: trigger_error() expects parameter 1 to be string, array given in /zend/Basic2Spec.inlinePhp on line 5
          |NULL
          |
          |Warning: Invalid error type specified in /zend/Basic2Spec.inlinePhp on line 6
          |bool(false)
          |
          |Warning: Invalid error type specified in /zend/Basic2Spec.inlinePhp on line 7
          |bool(false)
          |
          |Warning: error in /zend/Basic2Spec.inlinePhp on line 8
          |bool(true)
          |
          |Deprecated: error in /zend/Basic2Spec.inlinePhp on line 9
          |bool(true)
          |Done
          |""".stripMargin
      )
    }

    "isset() with object properties when operating on non-object" in {
      // Zend/tests/016.phpt
      script(
        """<?php
          |
          |$foo = NULL;
          |isset($foo->bar->bar);
          |
          |echo "Done\n";
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """Done
          |""".stripMargin
      )
    }


    "builtin functions tests" in {
      // Zend/tests/017.phpt
      script(
        """<?php
          |
          |var_dump(get_resource_type());
          |var_dump(get_resource_type(""));
          |$fp = fopen(__FILE__, "r");
          |var_dump(get_resource_type($fp));
          |fclose($fp);
          |var_dump(get_resource_type($fp));
          |
          |var_dump(gettype(get_loaded_extensions()));
          |var_dump(count(get_loaded_extensions()));
          |var_dump(gettype(get_loaded_extensions(true)));
          |var_dump(count(get_loaded_extensions(true)));
          |var_dump(get_loaded_extensions(true, true));
          |
          |define("USER_CONSTANT", "test");
          |
          |var_dump(get_defined_constants(true, true));
          |var_dump(gettype(get_defined_constants(true)));
          |var_dump(gettype(get_defined_constants()));
          |var_dump(count(get_defined_constants()));
          |
          |function test () {
          |}
          |
          |var_dump(get_defined_functions(true));
          |var_dump(gettype(get_defined_functions()));
          |var_dump(count(get_defined_functions()));
          |
          |var_dump(get_declared_interfaces(true));
          |var_dump(gettype(get_declared_interfaces()));
          |var_dump(count(get_declared_interfaces()));
          |
          |var_dump(get_extension_funcs());
          |var_dump(get_extension_funcs(true));
          |var_dump(gettype(get_extension_funcs("standard")));
          |var_dump(count(get_extension_funcs("standard")) > 0);
          |var_dump(gettype(get_extension_funcs("zend")));
          |var_dump(count(get_extension_funcs("zend")) > 0);
          |
          |
          |echo "Done\n";
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """
          |Warning: get_resource_type() expects exactly 1 parameter, 0 given in /zend/Basic2Spec.inlinePhp on line 3
          |NULL
          |
          |Warning: get_resource_type() expects parameter 1 to be resource, string given in /zend/Basic2Spec.inlinePhp on line 4
          |NULL
          |string(6) "stream"
          |string(7) "Unknown"
          |string(5) "array"
          |int(2)
          |string(5) "array"
          |int(2)
          |
          |Warning: get_loaded_extensions() expects at most 1 parameter, 2 given in /zend/Basic2Spec.inlinePhp on line 14
          |NULL
          |
          |Warning: get_defined_constants() expects at most 1 parameter, 2 given in /zend/Basic2Spec.inlinePhp on line 18
          |NULL
          |string(5) "array"
          |string(5) "array"
          |int(1)
          |
          |Warning: get_defined_functions() expects exactly 0 parameters, 1 given in /zend/Basic2Spec.inlinePhp on line 26
          |NULL
          |string(5) "array"
          |int(2)
          |
          |Warning: get_declared_interfaces() expects exactly 0 parameters, 1 given in /zend/Basic2Spec.inlinePhp on line 30
          |NULL
          |string(5) "array"
          |int(6)
          |
          |Warning: get_extension_funcs() expects exactly 1 parameter, 0 given in /zend/Basic2Spec.inlinePhp on line 34
          |NULL
          |bool(false)
          |string(5) "array"
          |bool(true)
          |string(5) "array"
          |bool(true)
          |Done
          |""".stripMargin
      )
    }

    "constant() tests" in {
      // Zend/tests/018.phpt
      script(
        """<?php
          |
          |var_dump(constant());
          |var_dump(constant("", ""));
          |var_dump(constant(""));
          |
          |var_dump(constant(array()));
          |
          |define("TEST_CONST", 1);
          |var_dump(constant("TEST_CONST"));
          |
          |define("TEST_CONST2", "test");
          |var_dump(constant("TEST_CONST2"));
          |
          |echo "Done\n";
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """
          |Warning: constant() expects exactly 1 parameter, 0 given in /zend/Basic2Spec.inlinePhp on line 3
          |NULL
          |
          |Warning: constant() expects exactly 1 parameter, 2 given in /zend/Basic2Spec.inlinePhp on line 4
          |NULL
          |
          |Warning: constant(): Couldn't find constant  in /zend/Basic2Spec.inlinePhp on line 5
          |NULL
          |
          |Warning: constant() expects parameter 1 to be string, array given in /zend/Basic2Spec.inlinePhp on line 7
          |NULL
          |int(1)
          |string(4) "test"
          |Done
          |""".stripMargin
      )
    }
  }
}
