/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                 (Tests based on PHP)   */

package de.leanovate.jbj.core.tests.zend

import org.specs2.mutable.SpecificationWithJUnit
import de.leanovate.jbj.core.tests.TestJbjExecutor

class Basic1Spec extends SpecificationWithJUnit with TestJbjExecutor {
  "Basic tests 001-009" should {
    "func_num_args() tests" in {
      // Zend/tests/001.phpt
      script(
        """<?php
          |
          |function test1() {
          |	var_dump(func_num_args());
          |}
          |
          |function test2($a) {
          |	var_dump(func_num_args());
          |}
          |
          |function test3($a, $b) {
          |	var_dump(func_num_args());
          |}
          |
          |test1();
          |test2(1);
          |test2();
          |test3(1,2);
          |
          |call_user_func("test1");
          |call_user_func("test3", 1);
          |call_user_func("test3", 1, 2);
          |
          |class test {
          |	static function test1($a) {
          |		var_dump(func_num_args());
          |	}
          |}
          |
          |test::test1(1);
          |var_dump(func_num_args());
          |
          |echo "Done\n";
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """int(0)
          |int(1)
          |
          |Warning: Missing argument 1 for test2(), called in /zend/Basic1Spec.inlinePhp on line 17 and defined in /zend/Basic1Spec.inlinePhp on line 7
          |int(0)
          |int(2)
          |int(0)
          |
          |Warning: Missing argument 2 for test3(), called in /zend/Basic1Spec.inlinePhp on line 21 and defined in /zend/Basic1Spec.inlinePhp on line 11
          |int(1)
          |int(2)
          |int(1)
          |
          |Warning: func_num_args():  Called from the global scope - no function context in /zend/Basic1Spec.inlinePhp on line 31
          |int(-1)
          |Done
          |""".stripMargin
      )
    }

    "func_get_arg() tests" in {
      // Zend/tests/002.phpt
      script(
        """<?php
          |
          |function test1() {
          |	var_dump(func_get_arg(-10));
          |	var_dump(func_get_arg(0));
          |	var_dump(func_get_arg(1));
          |}
          |
          |function test2($a) {
          |	var_dump(func_get_arg(0));
          |	var_dump(func_get_arg(1));
          |}
          |
          |function test3($a, $b) {
          |	var_dump(func_get_arg(0));
          |	var_dump(func_get_arg(1));
          |	var_dump(func_get_arg(2));
          |}
          |
          |test1();
          |test1(10);
          |test2(1);
          |test2();
          |test3(1,2);
          |
          |call_user_func("test1");
          |call_user_func("test3", 1);
          |call_user_func("test3", 1, 2);
          |
          |class test {
          |	static function test1($a) {
          |		var_dump(func_get_arg(0));
          |		var_dump(func_get_arg(1));
          |	}
          |}
          |
          |test::test1(1);
          |var_dump(func_get_arg(1));
          |
          |echo "Done\n";
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """
          |Warning: func_get_arg():  The argument number should be >= 0 in /zend/Basic1Spec.inlinePhp on line 4
          |bool(false)
          |
          |Warning: func_get_arg():  Argument 0 not passed to function in /zend/Basic1Spec.inlinePhp on line 5
          |bool(false)
          |
          |Warning: func_get_arg():  Argument 1 not passed to function in /zend/Basic1Spec.inlinePhp on line 6
          |bool(false)
          |
          |Warning: func_get_arg():  The argument number should be >= 0 in /zend/Basic1Spec.inlinePhp on line 4
          |bool(false)
          |int(10)
          |
          |Warning: func_get_arg():  Argument 1 not passed to function in /zend/Basic1Spec.inlinePhp on line 6
          |bool(false)
          |int(1)
          |
          |Warning: func_get_arg():  Argument 1 not passed to function in /zend/Basic1Spec.inlinePhp on line 11
          |bool(false)
          |
          |Warning: Missing argument 1 for test2(), called in /zend/Basic1Spec.inlinePhp on line 23 and defined in /zend/Basic1Spec.inlinePhp on line 9
          |
          |Warning: func_get_arg():  Argument 0 not passed to function in /zend/Basic1Spec.inlinePhp on line 10
          |bool(false)
          |
          |Warning: func_get_arg():  Argument 1 not passed to function in /zend/Basic1Spec.inlinePhp on line 11
          |bool(false)
          |int(1)
          |int(2)
          |
          |Warning: func_get_arg():  Argument 2 not passed to function in /zend/Basic1Spec.inlinePhp on line 17
          |bool(false)
          |
          |Warning: func_get_arg():  The argument number should be >= 0 in /zend/Basic1Spec.inlinePhp on line 4
          |bool(false)
          |
          |Warning: func_get_arg():  Argument 0 not passed to function in /zend/Basic1Spec.inlinePhp on line 5
          |bool(false)
          |
          |Warning: func_get_arg():  Argument 1 not passed to function in /zend/Basic1Spec.inlinePhp on line 6
          |bool(false)
          |
          |Warning: Missing argument 2 for test3(), called in /zend/Basic1Spec.inlinePhp on line 27 and defined in /zend/Basic1Spec.inlinePhp on line 14
          |int(1)
          |
          |Warning: func_get_arg():  Argument 1 not passed to function in /zend/Basic1Spec.inlinePhp on line 16
          |bool(false)
          |
          |Warning: func_get_arg():  Argument 2 not passed to function in /zend/Basic1Spec.inlinePhp on line 17
          |bool(false)
          |int(1)
          |int(2)
          |
          |Warning: func_get_arg():  Argument 2 not passed to function in /zend/Basic1Spec.inlinePhp on line 17
          |bool(false)
          |int(1)
          |
          |Warning: func_get_arg():  Argument 1 not passed to function in /zend/Basic1Spec.inlinePhp on line 33
          |bool(false)
          |
          |Warning: func_get_arg():  Called from the global scope - no function context in /zend/Basic1Spec.inlinePhp on line 38
          |bool(false)
          |Done
          |""".stripMargin
      )
    }

    "func_get_args() tests" in {
      // ../php-src/Zend/tests/003.phpt
      script(
        """<?php
          |
          |function test1() {
          |	var_dump(func_get_args());
          |}
          |
          |function test2($a) {
          |	var_dump(func_get_args());
          |}
          |
          |function test3($a, $b) {
          |	var_dump(func_get_args());
          |}
          |
          |test1();
          |test1(10);
          |test2(1);
          |test2();
          |test3(1,2);
          |
          |call_user_func("test1");
          |call_user_func("test3", 1);
          |call_user_func("test3", 1, 2);
          |
          |class test {
          |	static function test1($a) {
          |		var_dump(func_get_args());
          |	}
          |}
          |
          |test::test1(1);
          |var_dump(func_get_args());
          |
          |echo "Done\n";
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """array(0) {
          |}
          |array(1) {
          |  [0]=>
          |  int(10)
          |}
          |array(1) {
          |  [0]=>
          |  int(1)
          |}
          |
          |Warning: Missing argument 1 for test2(), called in /zend/Basic1Spec.inlinePhp on line 18 and defined in /zend/Basic1Spec.inlinePhp on line 7
          |array(0) {
          |}
          |array(2) {
          |  [0]=>
          |  int(1)
          |  [1]=>
          |  int(2)
          |}
          |array(0) {
          |}
          |
          |Warning: Missing argument 2 for test3(), called in /zend/Basic1Spec.inlinePhp on line 22 and defined in /zend/Basic1Spec.inlinePhp on line 11
          |array(1) {
          |  [0]=>
          |  int(1)
          |}
          |array(2) {
          |  [0]=>
          |  int(1)
          |  [1]=>
          |  int(2)
          |}
          |array(1) {
          |  [0]=>
          |  int(1)
          |}
          |
          |Warning: func_get_args():  Called from the global scope - no function context in /zend/Basic1Spec.inlinePhp on line 32
          |bool(false)
          |Done
          |""".stripMargin
      )
    }

    "strncmp() tests" in {
      // Zend/tests/004.phpt
      script(
        """<?php
          |
          |var_dump(strncmp("", ""));
          |var_dump(strncmp("", "", 100));
          |var_dump(strncmp("aef", "dfsgbdf", -1));
          |var_dump(strncmp("fghjkl", "qwer", 0));
          |var_dump(strncmp("qwerty", "qwerty123", 6));
          |var_dump(strncmp("qwerty", "qwerty123", 7));
          |
          |echo "Done\n";
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """
          |Warning: strncmp() expects exactly 3 parameters, 2 given in /zend/Basic1Spec.inlinePhp on line 3
          |NULL
          |int(0)
          |
          |Warning: Length must be greater than or equal to 0 in /zend/Basic1Spec.inlinePhp on line 5
          |bool(false)
          |int(0)
          |int(0)
          |int(-1)
          |Done
          |""".stripMargin
      )
    }

    "strcasecmp() tests" in {
      // Zend/tests/005.phpt
      script(
        """<?php
          |
          |var_dump(strcasecmp(""));
          |var_dump(strcasecmp("", ""));
          |var_dump(strcasecmp("aef", "dfsgbdf"));
          |var_dump(strcasecmp("qwe", "qwer"));
          |var_dump(strcasecmp("qwerty", "QweRty"));
          |var_dump(strcasecmp("qwErtY", "qwerty"));
          |var_dump(strcasecmp("q123", "Q123"));
          |var_dump(strcasecmp("01", "01"));
          |
          |echo "Done\n";
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """
          |Warning: strcasecmp() expects exactly 2 parameters, 1 given in /zend/Basic1Spec.inlinePhp on line 3
          |NULL
          |int(0)
          |int(-3)
          |int(-1)
          |int(0)
          |int(0)
          |int(0)
          |int(0)
          |Done
          |""".stripMargin
      )
    }

    "strncasecmp() tests" in {
      // Zend/tests/006.phpt
      script(
        """<?php
          |
          |var_dump(strncasecmp(""));
          |var_dump(strncasecmp("", "", -1));
          |var_dump(strncasecmp("aef", "dfsgbdf", 0));
          |var_dump(strncasecmp("aef", "dfsgbdf", 10));
          |var_dump(strncasecmp("qwe", "qwer", 3));
          |var_dump(strncasecmp("qwerty", "QweRty", 6));
          |var_dump(strncasecmp("qwErtY", "qwer", 7));
          |var_dump(strncasecmp("q123", "Q123", 3));
          |var_dump(strncasecmp("01", "01", 1000));
          |
          |echo "Done\n";
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """
          |Warning: strncasecmp() expects exactly 3 parameters, 1 given in /zend/Basic1Spec.inlinePhp on line 3
          |NULL
          |
          |Warning: Length must be greater than or equal to 0 in /zend/Basic1Spec.inlinePhp on line 4
          |bool(false)
          |int(0)
          |int(-3)
          |int(0)
          |int(0)
          |int(2)
          |int(0)
          |int(0)
          |Done
          |""".stripMargin
      )
    }
  }
}
