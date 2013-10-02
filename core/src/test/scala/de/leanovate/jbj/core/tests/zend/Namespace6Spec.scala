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

    "054: namespace and interfaces" in {
      // Zend/tests/ns_054.phpt
      script(
        """<?php
          |namespace test\ns1;
          |
          |class Foo implements \SplObserver {
          |	function update(\SplSubject $x) {
          |		echo "ok\n";
          |	}
          |}
          |
          |class Bar implements \SplSubject {
          |	function attach(\SplObserver $x) {
          |		echo "ok\n";
          |	}
          |	function notify() {
          |	}
          |	function detach(\SplObserver $x) {
          |	}
          |}
          |$foo = new Foo();
          |$bar = new Bar();
          |$bar->attach($foo);
          |$foo->update($bar);
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """ok
          |ok
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

    "056: type-hint compatibility in namespaces" in {
      // Zend/tests/ns_056.phpt
      script(
        """<?php
          |namespace test\ns1;
          |use \SplObserver;
          |
          |class Foo implements SplObserver {
          |	function update(\SplSubject $x) {
          |		echo "ok\n";
          |	}
          |}
          |
          |class Bar implements \SplSubject {
          |	function attach(SplObserver $x) {
          |		echo "ok\n";
          |	}
          |	function notify() {
          |	}
          |	function detach(SplObserver $x) {
          |	}
          |}
          |$foo = new Foo();
          |$bar = new Bar();
          |$bar->attach($foo);
          |$foo->update($bar);
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """ok
          |ok
          |""".stripMargin
      )
    }

    "057: Usage of 'namespace' in compound names (inside namespace)" in {
      // Zend/tests/ns_057.phpt
      script(
        """<?php
          |namespace Test\ns1;
          |
          |const C = "const ok\n";
          |
          |function foo() {
          |	return "func ok\n";
          |}
          |
          |class foo {
          |	const C = "const ok\n";
          |	const C2 = namespace\C;
          |	static $var = "var ok\n";
          |	function __construct() {
          |		echo "class ok\n";
          |	}
          |	static function bar() {
          |		return "method ok\n";
          |	}
          |}
          |
          |function f1($x=namespace\C) {
          |	return $x;
          |}
          |function f2($x=namespace\foo::C) {
          |	return $x;
          |}
          |
          |function f3(namespace\foo $x) {
          |	return "ok\n";
          |}
          |
          |echo namespace\C;
          |echo namespace\foo();
          |echo namespace\foo::C;
          |echo namespace\foo::C2;
          |echo namespace\foo::$var;
          |echo namespace\foo::bar();
          |echo namespace\f1();
          |echo namespace\f2();
          |echo namespace\f3(new namespace\foo());
          |echo namespace\nknown;
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """const ok
          |func ok
          |const ok
          |const ok
          |var ok
          |method ok
          |const ok
          |const ok
          |class ok
          |ok
          |
          |Fatal error: Undefined constant 'Test\ns1\nknown' in /zend/Namespace6Spec.inlinePhp on line 42
          |""".stripMargin
      )
    }

    "058: Usage of 'namespace' in compound names (out of namespase)" in {
      // Zend/tests/ns_058.phpt
      script(
        """<?php
          |const C = "const ok\n";
          |
          |function foo() {
          |	return "func ok\n";
          |}
          |
          |class foo {
          |	const C = "const ok\n";
          |	const C2 = namespace\C;
          |	static $var = "var ok\n";
          |	function __construct() {
          |		echo "class ok\n";
          |	}
          |	static function bar() {
          |		return "method ok\n";
          |	}
          |}
          |
          |function f1($x=namespace\C) {
          |	return $x;
          |}
          |function f2($x=namespace\foo::C) {
          |	return $x;
          |}
          |
          |function f3(namespace\foo $x) {
          |	return "ok\n";
          |}
          |
          |echo namespace\C;
          |echo namespace\foo();
          |echo namespace\foo::C;
          |echo namespace\foo::C2;
          |echo namespace\foo::$var;
          |echo namespace\foo::bar();
          |echo namespace\f1();
          |echo namespace\f2();
          |echo namespace\f3(new namespace\foo());
          |echo namespace\nknown;
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """const ok
          |func ok
          |const ok
          |const ok
          |var ok
          |method ok
          |const ok
          |const ok
          |class ok
          |ok
          |
          |Fatal error: Undefined constant 'nknown' in /zend/Namespace6Spec.inlinePhp on line 40
          |""".stripMargin
      )
    }

    "059: Constant arrays" in {
      // Zend/tests/ns_059.phpt
      script(
        """<?php
          |const C = array();
          |""".stripMargin
      ).result must haveOutput(
        """
          |Fatal error: Arrays are not allowed as constants in /zend/Namespace6Spec.inlinePhp on line 2
          |""".stripMargin
      )
    }
  }
}
