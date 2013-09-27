package de.leanovate.jbj.core.tests.zend

import org.specs2.mutable.SpecificationWithJUnit
import de.leanovate.jbj.core.tests.TestJbjExecutor

class Namespace1Spec extends SpecificationWithJUnit with TestJbjExecutor {
  "Namespaces" should {
    "001: Class in namespace" in {
      // ../php-src/Zend/tests/ns_001.phpt
      script(
        """<?php
          |namespace test\ns1;
          |
          |class Foo {
          |
          |  function __construct() {
          |    echo __CLASS__,"\n";
          |  }
          |
          |  function bar() {
          |    echo __CLASS__,"\n";
          |  }
          |
          |  static function baz() {
          |    echo __CLASS__,"\n";
          |  }
          |}
          |
          |$x = new Foo;
          |$x->bar();
          |Foo::baz();
          |$y = new \test\ns1\Foo;
          |$y->bar();
          |\test\ns1\Foo::baz();
          |""".stripMargin
      ).result must haveOutput(
        """test\ns1\Foo
          |test\ns1\Foo
          |test\ns1\Foo
          |test\ns1\Foo
          |test\ns1\Foo
          |test\ns1\Foo
          |""".stripMargin
      )
    }
  }
}
