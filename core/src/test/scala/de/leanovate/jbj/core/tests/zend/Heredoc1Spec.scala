/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                 (Tests based on PHP)   */

package de.leanovate.jbj.core.tests.zend

import org.specs2.mutable.SpecificationWithJUnit
import de.leanovate.jbj.core.tests.TestJbjExecutor

class Heredoc1Spec extends SpecificationWithJUnit with TestJbjExecutor {
  "Heredoc tests 001-009" should {
    "basic heredoc syntax" in {
      // Zend/tests/heredoc_001.phpt
      script(
        """<?php
          |
          |require_once 'nowdoc.inc';
          |
          |print <<<ENDOFHEREDOC
          |This is a heredoc test.
          |
          |ENDOFHEREDOC;
          |
          |$x = <<<ENDOFHEREDOC
          |This is another heredoc test.
          |
          |ENDOFHEREDOC;
          |
          |print "{$x}";
          |
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """This is a heredoc test.
          |This is another heredoc test.
          |""".stripMargin
      )
    }

    "basic binary heredoc syntax" in {
      // Zend/tests/heredoc_002.phpt
      script(
        """<?php
          |
          |require_once 'nowdoc.inc';
          |
          |print b<<<ENDOFHEREDOC
          |This is a heredoc test.
          |
          |ENDOFHEREDOC;
          |
          |$x = b<<<ENDOFHEREDOC
          |This is another heredoc test.
          |
          |ENDOFHEREDOC;
          |
          |print "{$x}";
          |
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """This is a heredoc test.
          |This is another heredoc test.
          |""".stripMargin
      )
    }

    "simple variable replacement test (heredoc)" in {
      // Zend/tests/heredoc_003.phpt
      script(
        """<?php
          |
          |require_once 'nowdoc.inc';
          |
          |print <<<ENDOFHEREDOC
          |This is heredoc test #$a.
          |
          |ENDOFHEREDOC;
          |
          |$x = <<<ENDOFHEREDOC
          |This is heredoc test #$b.
          |
          |ENDOFHEREDOC;
          |
          |print "{$x}";
          |
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """This is heredoc test #1.
          |This is heredoc test #2.
          |""".stripMargin
      )
    }
  }
}
