/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.pcre.tests

import org.specs2.mutable.SpecificationWithJUnit

class PcreConstantsSpec extends SpecificationWithJUnit with TestJbjExecutor {
  "PCRE constants" should {
    "Test for pre-defined pcre constants" in {
      // ../php-src/ext/pcre/tests/pcre.constants.phpt
      script(
        """<?php
          |
          |echo "PCRE constants test\n";
          |
          |echo "PREG_PATTERN_ORDER= ", PREG_PATTERN_ORDER, "\n";
          |echo "PREG_OFFSET_CAPTURE= ", PREG_OFFSET_CAPTURE, "\n";
          |echo "PREG_SPLIT_NO_EMPTY= ", PREG_SPLIT_NO_EMPTY, "\n";
          |echo "PREG_SPLIT_DELIM_CAPTURE= ", PREG_SPLIT_DELIM_CAPTURE, "\n";
          |echo "PREG_SPLIT_OFFSET_CAPTURE= ", PREG_SPLIT_OFFSET_CAPTURE, "\n";
          |echo "PREG_GREP_INVERT= ", PREG_GREP_INVERT, "\n";
          |echo "PREG_NO_ERROR= ", PREG_NO_ERROR, "\n";
          |echo "PREG_INTERNAL_ERROR= ", PREG_INTERNAL_ERROR, "\n";
          |echo "PREG_BACKTRACK_LIMIT_ERROR= ", PREG_BACKTRACK_LIMIT_ERROR, "\n";
          |echo "PREG_RECURSION_LIMIT_ERROR= ", PREG_RECURSION_LIMIT_ERROR, "\n";
          |echo "PREG_BAD_UTF8_ERROR= ", PREG_BAD_UTF8_ERROR, "\n";
          |
          |?>
          |===Done===
          |""".stripMargin
      ).result must haveOutput(
        """PCRE constants test
          |PREG_PATTERN_ORDER= 1
          |PREG_OFFSET_CAPTURE= 256
          |PREG_SPLIT_NO_EMPTY= 1
          |PREG_SPLIT_DELIM_CAPTURE= 2
          |PREG_SPLIT_OFFSET_CAPTURE= 4
          |PREG_GREP_INVERT= 1
          |PREG_NO_ERROR= 0
          |PREG_INTERNAL_ERROR= 1
          |PREG_BACKTRACK_LIMIT_ERROR= 2
          |PREG_RECURSION_LIMIT_ERROR= 3
          |PREG_BAD_UTF8_ERROR= 4
          |===Done===
          |""".stripMargin
      )
    }
  }
}
