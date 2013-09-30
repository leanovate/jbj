package de.leanovate.jbj.core.tests.zend

import org.specs2.mutable.SpecificationWithJUnit
import de.leanovate.jbj.core.tests.TestJbjExecutor

class Namespace6Spec extends SpecificationWithJUnit with TestJbjExecutor {
  "Namespace tests 050-059" should {
    "050: Name conflict and compile-time constants (ns name)" in {
      // Zend/tests/ns_050.phpt
      script(
        """<?php
          |namespace test\ns1;
          |
          |const INI_ALL = 0;
          |
          |function foo($x = INI_ALL) {
          |	var_dump($x);
          |}
          |foo();
          |""".stripMargin
      ).result must haveOutput(
        """int(0)
          |""".stripMargin
      )
    }

    "051: Name conflict and compile-time constants (php name)" in {
      // Zend/tests/ns_051.phpt
      script(
        """<?php
          |namespace test\ns1;
          |
          |function foo($x = INI_ALL) {
          |	var_dump($x);
          |}
          |foo();
          |""".stripMargin
      ).result must haveOutput(
        """int(7)
          |""".stripMargin
      )
    }

    "052: Name conflict and compile-time constants (php name in case if ns name exists)" in {
      // Zend/tests/ns_052.phpt
      script(
        """<?php
          |namespace test\ns1;
          |
          |const INI_ALL = 0;
          |
          |function foo($x = \INI_ALL) {
          |	var_dump($x);
          |}
          |foo();
          |""".stripMargin
      ).result must haveOutput(
        """int(7)
          |""".stripMargin
      )
    }

    "053: Run-time constant definition" in {
      // Zend/tests/ns_053.phpt
      script(
        """<?php
          |namespace test\ns1;
          |
          |define(__NAMESPACE__ . '\\NAME', basename(__FILE__));
          |echo NAME."\n";
          |echo \test\ns1\NAME."\n";
          |""".stripMargin
      ).result must haveOutput(
        """Namespace6Spec.inlinePhp
          |Namespace6Spec.inlinePhp
          |""".stripMargin
      )
    }

    "055: typehints in namespaces" in {
      // Zend/tests/ns_055.phpt
      script(
        """<?php
          |namespace test\ns1;
          |
          |class Foo {
          |	function test1(Foo $x) {
          |		echo "ok\n";
          |	}
          |	function test2(\test\ns1\Foo $x) {
          |		echo "ok\n";
          |	}
          |	function test3(\Exception $x) {
          |		echo "ok\n";
          |	}
          |}
          |
          |$foo = new Foo();
          |$ex = new \Exception();
          |$foo->test1($foo);
          |$foo->test2($foo);
          |$foo->test3($ex);
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """ok
          |ok
          |ok
          |""".stripMargin
      )
    }
  }
}
