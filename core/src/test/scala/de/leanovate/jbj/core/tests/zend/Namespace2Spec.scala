package de.leanovate.jbj.core.tests.zend

import org.specs2.mutable.SpecificationWithJUnit
import de.leanovate.jbj.core.tests.TestJbjExecutor

class Namespace2Spec extends SpecificationWithJUnit with TestJbjExecutor {
  "Namespaces test 010-019" should {
    "010: Accesing internal namespace class" in {
      // Zend/tests/ns_010.phpt
      script(
        """<?php
          |namespace X;
          |use X as Y;
          |class Foo {
          |	const C = "const ok\n";
          |	static $var = "var ok\n";
          |	function __construct() {
          |		echo "class ok\n";
          |	}
          |	static function bar() {
          |		echo "method ok\n";
          |	}
          |}
          |new Foo();
          |new Y\Foo();
          |new \X\Foo();
          |Foo::bar();
          |Y\Foo::bar();
          |\X\Foo::bar();
          |echo Foo::C;
          |echo Y\Foo::C;
          |echo \X\Foo::C;
          |echo Foo::$var;
          |echo Y\Foo::$var;
          |echo \X\Foo::$var;
          |""".stripMargin
      ).result must haveOutput(
        """class ok
          |class ok
          |class ok
          |method ok
          |method ok
          |method ok
          |const ok
          |const ok
          |const ok
          |var ok
          |var ok
          |var ok
          |""".stripMargin
      )
    }

    "011: Function in namespace" in {
      // Zend/tests/ns_011.phpt
      script(
        """<?php
          |namespace test\ns1;
          |
          |function foo() {
          |  echo __FUNCTION__,"\n";
          |}
          |
          |foo();
          |\test\ns1\foo();
          |bar();
          |\test\ns1\bar();
          |
          |function bar() {
          |  echo __FUNCTION__,"\n";
          |}
          |
          |""".stripMargin
      ).result must haveOutput(
        """test\ns1\foo
          |test\ns1\foo
          |test\ns1\bar
          |test\ns1\bar
          |""".stripMargin
      )
    }
  }
}
