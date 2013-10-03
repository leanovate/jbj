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
      // end/tests/ns_072.phpt
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
  }
}
