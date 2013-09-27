package de.leanovate.jbj.core.tests.zend

import org.specs2.mutable.SpecificationWithJUnit
import de.leanovate.jbj.core.tests.TestJbjExecutor

class Namespace4Spec extends SpecificationWithJUnit with TestJbjExecutor {
  "Namespace test 030-039" should {
    "030: Name ambiguity (import name & class name)" in {
      // ../php-src/Zend/tests/ns_030.phpt
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
  }
}
