package de.leanovate.jbj.core.tests.zend

import org.specs2.mutable.SpecificationWithJUnit
import de.leanovate.jbj.core.tests.TestJbjExecutor

class Namespace4Spec extends SpecificationWithJUnit with TestJbjExecutor {
  "Namespace test 030-039" should {
    "030: Name ambiguity (import name & class name)" in {
      // Zend/tests/ns_030.phpt
      script(
        """<?php
          |class Foo {
          |}
          |
          |use A\B as Foo;
          |
          |new Foo();
          |""".stripMargin
      ).result must haveOutput(
        """
          |Fatal error: Cannot use A\B as Foo because the name is already in use in /zend/Namespace4Spec.inlinePhp on line 5
          |""".stripMargin
      )
    }

    "031: Namespace support for user functions (ns name)" in {
      // Zend/tests/ns_031.phpt
      script(
        """<?php
          |namespace test;
          |
          |class Test {
          |	static function foo() {
          |		echo __CLASS__,"::",__FUNCTION__,"\n";
          |	}
          |}
          |
          |function foo() {
          |	echo __FUNCTION__,"\n";
          |}
          |
          |call_user_func(__NAMESPACE__."\\foo");
          |call_user_func(__NAMESPACE__."\\test::foo");
          |""".stripMargin
      ).result must haveOutput(
        """test\foo
          |test\Test::foo
          |""".stripMargin
      )
    }

    "032: Namespace support for user functions (php name)" in {
      // Zend/tests/ns_032.phpt
      script(
        """<?php
          |class Test {
          |	static function foo() {
          |		echo __CLASS__,"::",__FUNCTION__,"\n";
          |	}
          |}
          |
          |function foo() {
          |	echo __FUNCTION__,"\n";
          |}
          |
          |call_user_func(__NAMESPACE__."\\foo");
          |call_user_func(__NAMESPACE__."\\test::foo");
          |""".stripMargin
      ).result must haveOutput(
        """foo
          |Test::foo
          |""".stripMargin
      )
    }

    "033: Import statement with non-compound name" in {
      // ../php-src/Zend/tests/ns_033.phpt
      script(
        """<?php
          |use A;
          |""".stripMargin
      ).result must haveOutput(
        """
          |Warning: The use statement with non-compound name 'A' has no effect in /zend/Namespace4Spec.inlinePhp on line 2
          |""".stripMargin
      )
    }
  }
}
