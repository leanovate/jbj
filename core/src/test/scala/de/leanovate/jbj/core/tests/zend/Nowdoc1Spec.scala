/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                 (Tests based on PHP)   */

package de.leanovate.jbj.core.tests.zend

import org.specs2.mutable.SpecificationWithJUnit
import de.leanovate.jbj.core.tests.TestJbjExecutor

class Nowdoc1Spec extends SpecificationWithJUnit with TestJbjExecutor {
  "Nowdoc tests 001-009" should {
    "basic nowdoc syntax" in {
      // Zend/tests/nowdoc_001.phpt
      script(
        """<?php
          |
          |require_once 'nowdoc.inc';
          |
          |print <<<'ENDOFNOWDOC'
          |This is a nowdoc test.
          |
          |ENDOFNOWDOC;
          |
          |$x = <<<'ENDOFNOWDOC'
          |This is another nowdoc test.
          |With another line in it.
          |ENDOFNOWDOC;
          |
          |print "{$x}";
          |
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """This is a nowdoc test.
          |This is another nowdoc test.
          |With another line in it.""".stripMargin
      )
    }

    "basic binary nowdoc syntax" in {
      // ../php-src/Zend/tests/nowdoc_002.phpt
      script(
        """<?php
          |
          |require_once 'nowdoc.inc';
          |
          |print b<<<'ENDOFNOWDOC'
          |This is a nowdoc test.
          |
          |ENDOFNOWDOC;
          |
          |$x = b<<<'ENDOFNOWDOC'
          |This is another nowdoc test.
          |
          |ENDOFNOWDOC;
          |
          |print "{$x}";
          |
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """This is a nowdoc test.
          |This is another nowdoc test.
          |""".stripMargin
      )
    }

    "simple variable replacement test (nowdoc)" in {
      // Zend/tests/nowdoc_003.phpt
      script(
        """<?php
          |
          |require_once 'nowdoc.inc';
          |
          |print <<<'ENDOFNOWDOC'
          |This is nowdoc test #$a.
          |
          |ENDOFNOWDOC;
          |
          |$x = <<<'ENDOFNOWDOC'
          |This is nowdoc test #$b.
          |
          |ENDOFNOWDOC;
          |
          |print "{$x}";
          |
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """This is nowdoc test #$a.
          |This is nowdoc test #$b.
          |""".stripMargin
      )
    }

    "braces variable replacement test (nowdoc)" in {
      // Zend/tests/nowdoc_004.phpt
      script(
        """<?php
          |
          |require_once 'nowdoc.inc';
          |
          |print <<<'ENDOFNOWDOC'
          |This is nowdoc test #{$a}.
          |
          |ENDOFNOWDOC;
          |
          |$x = <<<'ENDOFNOWDOC'
          |This is nowdoc test #{$b}.
          |
          |ENDOFNOWDOC;
          |
          |print "{$x}";
          |
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """This is nowdoc test #{$a}.
          |This is nowdoc test #{$b}.
          |""".stripMargin
      )
    }

    "unbraced complex variable replacement test (nowdoc)" in {
      // Zend/tests/nowdoc_005.phpt
      script(
        """<?php
          |
          |require_once 'nowdoc.inc';
          |
          |print <<<'ENDOFNOWDOC'
          |This is nowdoc test #s $a, $b, $c['c'], and $d->d.
          |
          |ENDOFNOWDOC;
          |
          |$x = <<<'ENDOFNOWDOC'
          |This is nowdoc test #s $a, $b, $c['c'], and $d->d.
          |
          |ENDOFNOWDOC;
          |
          |print "{$x}";
          |
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """This is nowdoc test #s $a, $b, $c['c'], and $d->d.
          |This is nowdoc test #s $a, $b, $c['c'], and $d->d.
          |""".stripMargin
      )
    }

    "braced complex variable replacement test (nowdoc)" in {
      // Zend/tests/nowdoc_006.phpt
      script(
        """<?php
          |
          |require_once 'nowdoc.inc';
          |
          |print <<<'ENDOFNOWDOC'
          |This is nowdoc test #s {$a}, {$b}, {$c['c']}, and {$d->d}.
          |
          |ENDOFNOWDOC;
          |
          |$x = <<<'ENDOFNOWDOC'
          |This is nowdoc test #s {$a}, {$b}, {$c['c']}, and {$d->d}.
          |
          |ENDOFNOWDOC;
          |
          |print "{$x}";
          |
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """This is nowdoc test #s {$a}, {$b}, {$c['c']}, and {$d->d}.
          |This is nowdoc test #s {$a}, {$b}, {$c['c']}, and {$d->d}.
          |""".stripMargin
      )
    }

    "braced and unbraced complex variable replacement test (nowdoc)" in {
      // Zend/tests/nowdoc_007.phpt
      script(
        """<?php
          |
          |require_once 'nowdoc.inc';
          |
          |print <<<'ENDOFNOWDOC'
          |This is nowdoc test #s $a, {$b}, {$c['c']}, and {$d->d}.
          |
          |ENDOFNOWDOC;
          |
          |$x = <<<'ENDOFNOWDOC'
          |This is nowdoc test #s $a, {$b}, {$c['c']}, and {$d->d}.
          |
          |ENDOFNOWDOC;
          |
          |print "{$x}";
          |
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """This is nowdoc test #s $a, {$b}, {$c['c']}, and {$d->d}.
          |This is nowdoc test #s $a, {$b}, {$c['c']}, and {$d->d}.
          |""".stripMargin
      )
    }

    "empty doc test (nowdoc)" in {
      // Zend/tests/nowdoc_008.phpt
      script(
        """<?php
          |
          |require_once 'nowdoc.inc';
          |
          |print <<<'ENDOFNOWDOC'
          |ENDOFNOWDOC;
          |
          |$x = <<<'ENDOFNOWDOC'
          |ENDOFNOWDOC;
          |
          |print "{$x}";
          |
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """""".stripMargin
      )
    }

    "Torture the T_END_NOWDOC rules (nowdoc)" in {
      // Zend/tests/nowdoc_009.phpt
      script(
        """<?php
          |
          |require_once 'nowdoc.inc';
          |
          |print <<<'ENDOFNOWDOC'
          |ENDOFNOWDOC    ;
          |    ENDOFNOWDOC;
          |ENDOFNOWDOC
          |    ENDOFNOWDOC
          |$ENDOFNOWDOC;
          |
          |ENDOFNOWDOC;
          |
          |$x = <<<'ENDOFNOWDOC'
          |ENDOFNOWDOC    ;
          |    ENDOFNOWDOC;
          |ENDOFNOWDOC
          |    ENDOFNOWDOC
          |$ENDOFNOWDOC;
          |
          |ENDOFNOWDOC;
          |
          |print "{$x}";
          |
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """ENDOFNOWDOC    ;
          |    ENDOFNOWDOC;
          |ENDOFNOWDOC
          |    ENDOFNOWDOC
          |$ENDOFNOWDOC;
          |ENDOFNOWDOC    ;
          |    ENDOFNOWDOC;
          |ENDOFNOWDOC
          |    ENDOFNOWDOC
          |$ENDOFNOWDOC;
          |""".stripMargin
      )
    }
  }
}
