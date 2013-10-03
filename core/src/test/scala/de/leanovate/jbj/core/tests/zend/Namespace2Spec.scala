/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                 (Tests based on PHP)   */

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

    "012: Import in namespace and functions" in {
      // Zend/tests/ns_012.phpt
      script(
        """<?php
          |namespace test\ns1;
          |
          |function foo() {
          |  echo __FUNCTION__,"\n";
          |}
          |
          |use test\ns1 as ns2;
          |use test as ns3;
          |
          |foo();
          |bar();
          |\test\ns1\foo();
          |\test\ns1\bar();
          |ns2\foo();
          |ns2\bar();
          |ns3\ns1\foo();
          |ns3\ns1\bar();
          |
          |function bar() {
          |  echo __FUNCTION__,"\n";
          |}
          |
          |""".stripMargin
      ).result must haveOutput(
        """test\ns1\foo
          |test\ns1\bar
          |test\ns1\foo
          |test\ns1\bar
          |test\ns1\foo
          |test\ns1\bar
          |test\ns1\foo
          |test\ns1\bar
          |""".stripMargin
      )
    }

    "013: Name conflict and functions (ns name)" in {
      // Zend/tests/ns_013.phpt
      script(
        """<?php
          |namespace test\ns1;
          |
          |function strlen($x) {
          |	return __FUNCTION__;
          |}
          |
          |echo strlen("Hello"),"\n";
          |""".stripMargin
      ).result must haveOutput(
        """test\ns1\strlen
          |""".stripMargin
      )
    }

    "014: Name conflict and functions (php name)" in {
      // Zend/tests/ns_014.phpt
      script(
        """<?php
          |namespace test\ns1;
          |
          |echo strlen("Hello"),"\n";
          |""".stripMargin
      ).result must haveOutput(
        """5
          |""".stripMargin
      )
    }

    "015: Name conflict and functions (php name in case if ns name exists)" in {
      // Zend/tests/ns_015.phpt
      script(
        """<?php
          |namespace test\ns1;
          |
          |function strlen($x) {
          |	return __FUNCTION__;
          |}
          |
          |echo \strlen("Hello"),"\n";
          |""".stripMargin
      ).result must haveOutput(
        """5
          |""".stripMargin
      )
    }

    "016: Run-time name conflict and functions (ns name)" in {
      // ../php-src/Zend/tests/ns_016.phpt
      script(
        """<?php
          |namespace test\ns1;
          |
          |function strlen($x) {
          |	return __FUNCTION__;
          |}
          |
          |$x = "test\\ns1\\strlen";
          |echo $x("Hello"),"\n";
          |""".stripMargin
      ).result must haveOutput(
        """test\ns1\strlen
          |""".stripMargin
      )
    }

    "017: Run-time name conflict and functions (php name)" in {
      // ../php-src/Zend/tests/ns_017.phpt
      script(
        """<?php
          |namespace test\ns1;
          |
          |function strlen($x) {
          |	return __FUNCTION__;
          |}
          |
          |$x = "strlen";
          |echo $x("Hello"),"\n";
          |""".stripMargin
      ).result must haveOutput(
        """5
          |""".stripMargin
      )
    }

    "018: __NAMESPACE__ constant and runtime names (ns name)" in {
      // ../php-src/Zend/tests/ns_018.phpt
      script(
        """<?php
          |namespace test;
          |
          |function foo() {
          |	return __FUNCTION__;
          |}
          |
          |$x = __NAMESPACE__ . "\\foo";
          |echo $x(),"\n";
          |""".stripMargin
      ).result must haveOutput(
        """test\foo
          |""".stripMargin
      )
    }

    "019: __NAMESPACE__ constant and runtime names (php name)" in {
      // ../php-src/Zend/tests/ns_019.phpt
      script(
        """<?php
          |function foo() {
          |	return __FUNCTION__;
          |}
          |
          |$x = __NAMESPACE__ . "\\foo";
          |echo $x(),"\n";
          |""".stripMargin
      ).result must haveOutput(
        """foo
          |""".stripMargin
      )
    }
  }
}
