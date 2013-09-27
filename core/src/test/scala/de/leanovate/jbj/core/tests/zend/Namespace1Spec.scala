package de.leanovate.jbj.core.tests.zend

import org.specs2.mutable.SpecificationWithJUnit
import de.leanovate.jbj.core.tests.TestJbjExecutor

class Namespace1Spec extends SpecificationWithJUnit with TestJbjExecutor {
  "Namespaces" should {
    "001: Class in namespace" in {
      // Zend/tests/ns_001.phpt
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

    "002: Import in namespace" in {
      // Zend/tests/ns_002.phpt
      script(
        """<?php
          |namespace test\ns1;
          |
          |class Foo {
          |  static function bar() {
          |    echo __CLASS__,"\n";
          |  }
          |}
          |
          |use test\ns1\Foo as Bar;
          |use test\ns1 as ns2;
          |use test\ns1;
          |
          |Foo::bar();
          |\test\ns1\Foo::bar();
          |Bar::bar();
          |ns2\Foo::bar();
          |ns1\Foo::bar();
          |""".stripMargin
      ).result must haveOutput(
        """test\ns1\Foo
          |test\ns1\Foo
          |test\ns1\Foo
          |test\ns1\Foo
          |test\ns1\Foo
          |""".stripMargin
      )
    }

    "003: Name conflict (ns name)" in {
      // ../php-src/Zend/tests/ns_003.phpt
      script(
        """<?php
          |namespace test\ns1;
          |
          |class Exception {
          |}
          |
          |echo get_class(new Exception()),"\n";
          |""".stripMargin
      ).result must haveOutput(
        """test\ns1\Exception
          |""".stripMargin
      )
    }

    "004: Using global class name from namespace (unqualified - fail)" in {
      // Zend/tests/ns_004.phpt
      script(
        """<?php
          |namespace test\ns1;
          |
          |echo get_class(new Exception()),"\n";
          |""".stripMargin
      ).result must haveOutput(
        """
          |Fatal error: Class 'test\ns1\Exception' not found in /zend/Namespace1Spec.inlinePhp on line 4
          |""".stripMargin
      )
    }

    "005: Name conflict (php name in case if ns name exists)" in {
      // Zend/tests/ns_005.phpt
      script(
        """<?php
          |namespace test\ns1;
          |
          |class Exception {
          |}
          |
          |echo get_class(new \Exception()),"\n";
          |""".stripMargin
      ).result must haveOutput(
        """Exception
          |""".stripMargin
      )
    }

    "006: Run-time name conflict (ns name)" in {
      // Zend/tests/ns_006.phpt
      script(
        """<?php
          |namespace test\ns1;
          |
          |class Exception {
          |}
          |
          |$x = "test\\ns1\\Exception";
          |echo get_class(new $x),"\n";
          |""".stripMargin
      ).result must haveOutput(
        """test\ns1\Exception
          |""".stripMargin
      )
    }

    "007: Run-time name conflict (php name)" in {
      // Zend/tests/ns_007.phpt
      script(
        """<?php
          |namespace test\ns1;
          |
          |class Exception {
          |}
          |
          |$x = "Exception";
          |echo get_class(new $x),"\n";
          |""".stripMargin
      ).result must haveOutput(
        """Exception
          |""".stripMargin
      )
    }
  }
}
