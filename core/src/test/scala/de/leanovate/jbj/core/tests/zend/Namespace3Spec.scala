package de.leanovate.jbj.core.tests.zend

import org.specs2.mutable.SpecificationWithJUnit
import de.leanovate.jbj.core.tests.TestJbjExecutor

class Namespace3Spec extends SpecificationWithJUnit with TestJbjExecutor {
  "Namespace test 020-029" should {
    "020: Accesing internal namespace function" in {
      // Zend/tests/ns_020.phpt
      script(
        """<?php
          |namespace X;
          |use X as Y;
          |function foo() {
          |	echo __FUNCTION__,"\n";
          |}
          |foo();
          |\X\foo();
          |Y\foo();
          |\X\foo();
          |""".stripMargin
      ).result must haveOutput(
        """X\foo
          |X\foo
          |X\foo
          |X\foo
          |""".stripMargin
      )
    }

    "021: Name search priority (first look into namespace)" in {
      // Zend/tests/ns_021.phpt
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
          |foo();
          |\test\foo();
          |\test\test::foo();
          |""".stripMargin
      ).result must haveOutput(
        """test\foo
          |test\foo
          |test\Test::foo
          |""".stripMargin
      )
    }

    "022: Name search priority (first look into import, then into current namespace and then for class)" in {
      // ../php-src/Zend/tests/ns_022.phpt
      script(
        """<?php
          |namespace a\b\c;
          |
          |use a\b\c as test;
          |
          |require "ns_022.inc";
          |
          |function foo() {
          |	echo __FUNCTION__,"\n";
          |}
          |
          |test\foo();
          |\test::foo();
          |""".stripMargin
      ).result must haveOutput(
        """a\b\c\foo
          |Test::foo
          |""".stripMargin
      )
    }
  }
}
