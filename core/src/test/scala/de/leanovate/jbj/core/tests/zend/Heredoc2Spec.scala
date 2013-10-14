/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                 (Tests based on PHP)   */

package de.leanovate.jbj.core.tests.zend

import org.specs2.mutable.SpecificationWithJUnit
import de.leanovate.jbj.core.tests.TestJbjExecutor

class Heredoc2Spec extends SpecificationWithJUnit with TestJbjExecutor {
  "Heredoc tests 010-018" should {
    "Torture the T_END_HEREDOC rules with variable expansions (heredoc)" in {
      // Zend/tests/heredoc_010.phpt
      script(
        """<?php
          |
          |require_once 'nowdoc.inc';
          |$fooledYou = '';
          |
          |print <<<ENDOFHEREDOC
          |{$fooledYou}ENDOFHEREDOC{$fooledYou}
          |ENDOFHEREDOC{$fooledYou}
          |{$fooledYou}ENDOFHEREDOC
          |
          |ENDOFHEREDOC;
          |
          |$x = <<<ENDOFHEREDOC
          |{$fooledYou}ENDOFHEREDOC{$fooledYou}
          |ENDOFHEREDOC{$fooledYou}
          |{$fooledYou}ENDOFHEREDOC
          |
          |ENDOFHEREDOC;
          |
          |print "{$x}";
          |
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """ENDOFHEREDOC
          |ENDOFHEREDOC
          |ENDOFHEREDOC
          |ENDOFHEREDOC
          |ENDOFHEREDOC
          |ENDOFHEREDOC
          |""".stripMargin
      )
    }

    "STATIC heredocs CAN be used as static scalars." in {
      // Zend/tests/heredoc_011.phpt
      script(
        """<?php
          |
          |require_once 'nowdoc.inc';
          |
          |class e {
          |
          |    const E = <<<THISMUSTNOTERROR
          |If you DON'T see this, something's wrong.
          |THISMUSTNOTERROR;
          |
          |};
          |
          |print e::E . "\n";
          |
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """If you DON'T see this, something's wrong.
          |""".stripMargin
      )
    }

    "Heredoc with double quotes" in {
      // Zend/tests/heredoc_012.phpt
      script(
        """<?php
          |$test = "foo";
          |$var = <<<"MYLABEL"
          |test: $test
          |MYLABEL;
          |echo $var;
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """test: foo""".stripMargin
      )
    }

    "Heredoc with double quotes and wrong prefix" in {
      // Zend/tests/heredoc_013.phpt
      script(
        """<?php
          |$test = "foo";
          |$var = prefix<<<"MYLABEL"
          |test: $test
          |MYLABEL;
          |echo $var;
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """
          |Parse error: `keyword ';'' expected but heredoc start MYLABEL found in /zend/Heredoc2Spec.inlinePhp on line 3
          |""".stripMargin
      )
    }

    "Heredoc with double quotes syntax but missing second quote" in {
      // Zend/tests/heredoc_014.phpt
      script(
        """<?php
          |$test = "foo";
          |$var = <<<"MYLABEL
          |test: $test
          |MYLABEL;
          |echo $var;
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """
          |Parse error: `keyword '&'' expected but keyword '<<' found in /zend/Heredoc2Spec.inlinePhp on line 3
          |""".stripMargin
      )
    }

    "Testing heredoc with escape sequences" in {
      // Zend/tests/heredoc_015.phpt
      script(
        """<?php
          |
          |$test = <<<TEST
          |TEST;
          |
          |var_dump(strlen($test) == 0);
          |
          |$test = <<<TEST
          |\
          |TEST;
          |
          |var_dump(strlen($test) == 1);
          |
          |$test = <<<TEST
          |\0
          |TEST;
          |
          |var_dump(strlen($test) == 1);
          |
          |$test = <<<TEST
          |\n
          |TEST;
          |
          |var_dump(strlen($test) == 1);
          |
          |$test = <<<TEST
          |\\'
          |TEST;
          |
          |var_dump(strlen($test) == 2);
          |
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """bool(true)
          |bool(true)
          |bool(true)
          |bool(true)
          |bool(true)
          |""".stripMargin
      )
    }

    "Testing heredoc (double quotes) with escape sequences" in {
      // Zend/tests/heredoc_016.phpt
      script(
        """<?php
          |
          |$test = <<<"TEST"
          |TEST;
          |
          |var_dump(strlen($test) == 0);
          |
          |$test = <<<"TEST"
          |\
          |TEST;
          |
          |var_dump(strlen($test) == 1);
          |
          |$test = <<<"TEST"
          |\0
          |TEST;
          |
          |var_dump(strlen($test) == 1);
          |
          |$test = <<<"TEST"
          |\n
          |TEST;
          |
          |var_dump(strlen($test) == 1);
          |
          |$test = <<<"TEST"
          |\\'
          |TEST;
          |
          |var_dump(strlen($test) == 2);
          |
          |
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """bool(true)
          |bool(true)
          |bool(true)
          |bool(true)
          |bool(true)
          |""".stripMargin
      )
    }

    "Testinh heredoc syntax" in {
      // Zend/tests/heredoc_017.phpt
      script(
        """<?php
          |
          |$a = <<<A
          |	A;
          |;
          | A;
          |\;
          |A;
          |
          |var_dump(strlen($a) == 12);
          |
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """bool(true)
          |""".stripMargin
      )
    }

    "Testing heredoc with tabs before identifier" in {
      // Zend/tests/heredoc_018.phpt
      script(
        """<?php
          |
          |$heredoc = <<<	A
          |
          |foo
          |
          |	A;
          |A;
          |
          |var_dump(strlen($heredoc) == 9);
          |
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """bool(true)
          |""".stripMargin
      )
    }
  }
}
