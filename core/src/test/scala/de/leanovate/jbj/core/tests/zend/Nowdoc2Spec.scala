/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                 (Tests based on PHP)   */

package de.leanovate.jbj.core.tests.zend

import org.specs2.mutable.SpecificationWithJUnit
import de.leanovate.jbj.core.tests.TestJbjExecutor

class Nowdoc2Spec extends SpecificationWithJUnit with TestJbjExecutor {
  "Nowdoc tests 010-017" should {
    "Torture the T_END_NOWDOC rules with variable expansions (nowdoc)" in {
      // Zend/tests/nowdoc_010.phpt
      script(
        """<?php
          |
          |require_once 'nowdoc.inc';
          |$fooledYou = '';
          |
          |print <<<'ENDOFNOWDOC'
          |{$fooledYou}ENDOFNOWDOC{$fooledYou}
          |ENDOFNOWDOC{$fooledYou}
          |{$fooledYou}ENDOFNOWDOC
          |
          |ENDOFNOWDOC;
          |
          |$x = <<<'ENDOFNOWDOC'
          |{$fooledYou}ENDOFNOWDOC{$fooledYou}
          |ENDOFNOWDOC{$fooledYou}
          |{$fooledYou}ENDOFNOWDOC
          |
          |ENDOFNOWDOC;
          |
          |print "{$x}";
          |
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """{$fooledYou}ENDOFNOWDOC{$fooledYou}
          |ENDOFNOWDOC{$fooledYou}
          |{$fooledYou}ENDOFNOWDOC
          |{$fooledYou}ENDOFNOWDOC{$fooledYou}
          |ENDOFNOWDOC{$fooledYou}
          |{$fooledYou}ENDOFNOWDOC
          |""".stripMargin
      )
    }

    "Nowdocs CAN be used as static scalars." in {
      // Zend/tests/nowdoc_011.phpt
      script(
        """<?php
          |
          |require_once 'nowdoc.inc';
          |
          |class e {
          |
          |    const E = <<<'THISMUSTNOTERROR'
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

    "Test false labels" in {
      // ../php-src/Zend/tests/nowdoc_012.phpt
      script(
        """<?php
          |
          |require_once 'nowdoc.inc';
          |
          |$x = <<<'ENDOFNOWDOC'
          |This is a nowdoc test.
          |NOTREALLYEND;
          |Another line
          |NOTENDEITHER;
          |ENDOFNOWDOCWILLBESOON
          |Now let's finish it
          |ENDOFNOWDOC;
          |print "{$x}\n";
          |
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """This is a nowdoc test.
          |NOTREALLYEND;
          |Another line
          |NOTENDEITHER;
          |ENDOFNOWDOCWILLBESOON
          |Now let's finish it
          |""".stripMargin
      )
    }

    "Test nowdoc and line numbering" in {
      // Zend/tests/nowdoc_015.phpt
      script(
        """<?php
          |function error_handler($num, $msg, $file, $line, $vars) {
          |	echo $line,"\n";
          |}
          |set_error_handler('error_handler');
          |trigger_error("line", E_USER_ERROR);
          |$x = <<<EOF
          |EOF;
          |var_dump($x);
          |trigger_error("line", E_USER_ERROR);
          |$x = <<<'EOF'
          |EOF;
          |var_dump($x);
          |trigger_error("line", E_USER_ERROR);
          |$x = <<<EOF
          |test
          |EOF;
          |var_dump($x);
          |trigger_error("line", E_USER_ERROR);
          |$x = <<<'EOF'
          |test
          |EOF;
          |var_dump($x);
          |trigger_error("line", E_USER_ERROR);
          |$x = <<<EOF
          |test1
          |test2
          |
          |test3
          |
          |
          |EOF;
          |var_dump($x);
          |trigger_error("line", E_USER_ERROR);
          |$x = <<<'EOF'
          |test1
          |test2
          |
          |test3
          |
          |
          |EOF;
          |var_dump($x);
          |trigger_error("line", E_USER_ERROR);
          |echo "ok\n";
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """6
          |string(0) ""
          |10
          |string(0) ""
          |14
          |string(4) "test"
          |19
          |string(4) "test"
          |24
          |string(20) "test1
          |test2
          |
          |test3
          |
          |"
          |34
          |string(20) "test1
          |test2
          |
          |test3
          |
          |"
          |44
          |ok
          |""".stripMargin
      )
    }

    "Testing nowdocs with escape sequences" in {
      // Zend/tests/nowdoc_016.phpt
      script(
        """<?php
          |
          |$test = <<<'TEST'
          |TEST;
          |
          |var_dump(strlen($test));
          |
          |$test = <<<'TEST'
          |\
          |TEST;
          |
          |var_dump(strlen($test));
          |
          |$test = <<<'TEST'
          |\0
          |TEST;
          |
          |var_dump(strlen($test));
          |
          |$test = <<<'TEST'
          |\n
          |TEST;
          |
          |var_dump(strlen($test));
          |
          |$test = <<<'TEST'
          |\\'
          |TEST;
          |
          |var_dump(strlen($test));
          |
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """int(0)
          |int(1)
          |int(2)
          |int(2)
          |int(3)
          |""".stripMargin
      )
    }

    "Testing nowdoc in default value for property" in {
      // Zend/tests/nowdoc_017.phpt
      script(
        """<?php
          |
          |class foo {
          |    public $bar = <<<'EOT'
          |bar
          |EOT;
          |}
          |
          |print "ok!\n";
          |
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """ok!
          |""".stripMargin
      )
    }
  }
}
