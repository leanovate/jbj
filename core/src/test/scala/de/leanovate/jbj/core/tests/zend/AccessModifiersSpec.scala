/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                 (Tests based on PHP)   */

package de.leanovate.jbj.core.tests.zend

import org.specs2.mutable.SpecificationWithJUnit
import de.leanovate.jbj.core.tests.TestJbjExecutor

class AccessModifiersSpec extends SpecificationWithJUnit with TestJbjExecutor {
  "Access modifier tests" should {
    "using multiple access modifiers (methods)" in {
      // Zend/tests/access_modifiers_001.phpt
      script(
        """<?php
          |
          |class test {
          |	static public public static final public final function foo() {
          |	}
          |}
          |
          |echo "Done\n";
          |?>
          | """.stripMargin
      ).result must haveOutput(
        """
          |Fatal error: Multiple access type modifiers are not allowed in /zend/AccessModifiersSpec.inlinePhp on line 4
          |""".stripMargin
      )
    }

    "using multiple access modifiers (attributes)" in {
      // Zend/tests/access_modifiers_002.phpt
      script(
        """<?php
          |
          |class test {
          |	static public public static final public final $var;
          |}
          |
          |echo "Done\n";
          |?>
          | """.stripMargin
      ).result must haveOutput(
        """
          |Fatal error: Multiple access type modifiers are not allowed in /zend/AccessModifiersSpec.inlinePhp on line 4
          |""".stripMargin
      )
    }

    "using multiple access modifiers (classes)" in {
      // Zend/tests/access_modifiers_003.phpt
      script(
        """<?php
          |
          |final final class test {
          |	function foo() {}
          |}
          |
          |echo "Done\n";
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """
          |Parse error: `keyword 'class'' expected but keyword 'final' found in /zend/AccessModifiersSpec.inlinePhp on line 3
          |""".stripMargin
      )
    }

    "using multiple access modifiers (abstract methods)" in {
      // Zend/tests/access_modifiers_004.phpt
      script(
        """<?php
          |
          |class test {
          |	abstract abstract function foo() {
          |	}
          |}
          |
          |echo "Done\n";
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """
          |Fatal error: Multiple abstract modifiers are not allowed in /zend/AccessModifiersSpec.inlinePhp on line 4
          |""".stripMargin
      )
    }
  }
}
