/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                 (Tests based on PHP)   */

package de.leanovate.jbj.core.tests.zend

import org.specs2.mutable.SpecificationWithJUnit
import de.leanovate.jbj.core.tests.TestJbjExecutor

class Namespace8Spec extends SpecificationWithJUnit with TestJbjExecutor {
  "Namespace tests 070-079" should {
    "Testing parameter type-hinted with default value inside namespace" in {
      // Zend/tests/ns_070.phpt
      script(
        """<?php
          |
          |namespace foo;
          |
          |class bar {
          |	public function __construct(\stdclass $x = NULL) {
          |		var_dump($x);
          |	}
          |}
          |
          |new bar(new \stdclass);
          |new bar(null);
          |
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """object(stdClass)#2 (0) {
          |}
          |NULL
          |""".stripMargin
      )
    }

    "Testing parameter type-hinted (array) with default value inside namespace" in {
      // Zend/tests/ns_071.phpt
      script(
        """<?php
          |
          |namespace foo;
          |
          |class bar {
          |	public function __construct(array $x = NULL) {
          |		var_dump($x);
          |	}
          |}
          |
          |new bar(null);
          |new bar(new \stdclass);
          |
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """NULL
          |
          |Catchable fatal error: Argument 1 passed to foo\bar::__construct() must be of the type array, object given, called in /zend/Namespace8Spec.inlinePhp on line 12 and defined in /zend/Namespace8Spec.inlinePhp on line 6
          |""".stripMargin
      )
    }

    "Testing parameter type-hinted with interface" in {
      // Zend/tests/ns_072.phpt
      script(
        """<?php
          |
          |namespace foo;
          |
          |interface foo {
          |
          |}
          |
          |class bar {
          |	public function __construct(foo $x = NULL) {
          |		var_dump($x);
          |	}
          |}
          |
          |class test implements foo {
          |
          |}
          |
          |
          |new bar(new test);
          |new bar(null);
          |new bar(new \stdclass);
          |
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """object(foo\test)#2 (0) {
          |}
          |NULL
          |
          |Catchable fatal error: Argument 1 passed to foo\bar::__construct() must implement interface foo\foo, instance of stdClass given, called in /zend/Namespace8Spec.inlinePhp on line 22 and defined in /zend/Namespace8Spec.inlinePhp on line 10
          |""".stripMargin
      )
    }

    "Testing type-hinted lambda parameter inside namespace" in {
      // Zend/tests/ns_073.phpt
      script(
        """<?php
          |
          |namespace foo;
          |
          |$x = function (\stdclass $x = NULL) {
          |	var_dump($x);
          |};
          |
          |$x(NULL);
          |$x(new \stdclass);
          |
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """NULL
          |object(stdClass)#2 (0) {
          |}
          |""".stripMargin
      )
    }

    "Testing type-hinted lambda parameter inside namespace" in {
      // Zend/tests/ns_074.phpt
      script(
        """<?php
          |
          |namespace foo;
          |
          |$x = function (\stdclass $x = NULL) {
          |	var_dump($x);
          |};
          |
          |class stdclass extends \stdclass { }
          |
          |$x(NULL);
          |$x(new stdclass);
          |$x(new \stdclass);
          |
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """NULL
          |object(foo\stdclass)#2 (0) {
          |}
          |object(stdClass)#3 (0) {
          |}
          |""".stripMargin
      )
    }

    "075: Redefining compile-time constants" in {
      // Zend/tests/ns_075.phpt
      script(
        """<?php
          |namespace foo;
          |const NULL = 1;
          |
          |echo NULL;
          |""".stripMargin
      ).result must haveOutput(
        """
          |Fatal error: Cannot redeclare constant 'NULL' in /zend/Namespace8Spec.inlinePhp on line 3
          |""".stripMargin
      )
    }

    "076: Unknown constants in namespace" in {
      // Zend/tests/ns_076.phpt
      script(
        """<?php
          |namespace foo;
          |
          |$a = array(unknown => unknown);
          |
          |echo unknown;
          |echo "\n";
          |var_dump($a);
          |echo \nknown;
          |""".stripMargin
      ).result must haveOutput(
        """
          |Notice: Use of undefined constant unknown - assumed 'unknown' in /zend/Namespace8Spec.inlinePhp on line 4
          |
          |Notice: Use of undefined constant unknown - assumed 'unknown' in /zend/Namespace8Spec.inlinePhp on line 4
          |
          |Notice: Use of undefined constant unknown - assumed 'unknown' in /zend/Namespace8Spec.inlinePhp on line 6
          |unknown
          |array(1) {
          |  ["unknown"]=>
          |  string(7) "unknown"
          |}
          |
          |Fatal error: Undefined constant 'nknown' in /zend/Namespace8Spec.inlinePhp on line 9
          |""".stripMargin
      )
    }

    "077: Unknown compile-time constants in namespace" in {
      // Zend/tests/ns_077_1.phpt
      script(
        """<?php
          |namespace foo;
          |
          |function foo($a = array(0 => \nknown))
          |{
          |}
          |
          |foo();
          |""".stripMargin
      ).result must haveOutput(
        """
          |Fatal error: Undefined constant 'nknown' in /zend/Namespace8Spec.inlinePhp on line 4
          |""".stripMargin
      )
    }

    "077: Unknown compile-time constants in namespace" in {
      // Zend/tests/ns_077_2.phpt
      script(
        """<?php
          |namespace foo;
          |
          |function foo($a = array(\nknown => unknown))
          |{
          |}
          |
          |foo();
          |""".stripMargin
      ).result must haveOutput(
        """
          |Fatal error: Undefined constant 'nknown' in /zend/Namespace8Spec.inlinePhp on line 4
          |""".stripMargin
      )
    }

    "077: Unknown compile-time constants in namespace" in {
      // Zend/tests/ns_077_3.phpt
      script(
        """<?php
          |namespace foo;
          |
          |function foo($a = array(namespace\nknown => unknown))
          |{
          |}
          |
          |foo();
          |""".stripMargin
      ).result must haveOutput(
        """
          |Fatal error: Undefined constant 'foo\nknown' in /zend/Namespace8Spec.inlinePhp on line 4
          |""".stripMargin
      )
    }

    "077: Unknown compile-time constants in namespace" in {
      // Zend/tests/ns_077_4.phpt
      script(
        """<?php
          |namespace foo;
          |
          |function foo($a = array(0 => namespace\nknown))
          |{
          |}
          |
          |foo();
          |""".stripMargin
      ).result must haveOutput(
        """
          |Fatal error: Undefined constant 'foo\nknown' in /zend/Namespace8Spec.inlinePhp on line 4
          |""".stripMargin
      )
    }

    "077: Unknown compile-time constants in namespace" in {
      // Zend/tests/ns_077_5.phpt
      script(
        """<?php
          |
          |function foo($a = array(0 => \nknown))
          |{
          |}
          |
          |foo();
          |""".stripMargin
      ).result must haveOutput(
        """
          |Fatal error: Undefined constant 'nknown' in /zend/Namespace8Spec.inlinePhp on line 3
          |""".stripMargin
      )
    }

    "077: Unknown compile-time constants in namespace" in {
      // Zend/tests/ns_077_6.phpt
      script(
        """<?php
          |
          |function foo($a = array(0 => \nknown))
          |{
          |}
          |
          |foo();
          |""".stripMargin
      ).result must haveOutput(
        """
          |Fatal error: Undefined constant 'nknown' in /zend/Namespace8Spec.inlinePhp on line 3
          |""".stripMargin
      )
    }

    "077: Unknown compile-time constants in namespace" in {
      // Zend/tests/ns_077_7.phpt
      script(
        """<?php
          |
          |function foo($a = array(0 => namespace\nknown))
          |{
          |}
          |
          |foo();
          |""".stripMargin
      ).result must haveOutput(
        """
          |Fatal error: Undefined constant 'nknown' in /zend/Namespace8Spec.inlinePhp on line 3
          |""".stripMargin
      )
    }

    "077: Unknown compile-time constants in namespace" in {
      // Zend/tests/ns_077_8.phpt
      script(
        """<?php
          |
          |function foo($a = array(namespace\nknown => unknown))
          |{
          |}
          |
          |foo();
          |""".stripMargin
      ).result must haveOutput(
        """
          |Fatal error: Undefined constant 'nknown' in /zend/Namespace8Spec.inlinePhp on line 3
          |""".stripMargin
      )
    }
  }
}
