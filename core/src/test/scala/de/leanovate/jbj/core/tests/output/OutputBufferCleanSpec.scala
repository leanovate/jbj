/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                 (Tests based on PHP)   */

package de.leanovate.jbj.core.tests.output

import org.specs2.mutable.SpecificationWithJUnit
import de.leanovate.jbj.core.tests.TestJbjExecutor

class OutputBufferCleanSpec extends SpecificationWithJUnit with TestJbjExecutor {
  "ob_clean()" should {
    "Test ob_clean() function : basic functionality" in {
      // output/ob_clean_basic_001.phpt
      script(
        """<?php
          |/* Prototype  : proto bool ob_clean(void)
          | * Description: Clean (delete) the current output buffer
          | * Source code: main/output.c
          | * Alias to functions:
          | */
          |
          |echo "*** Testing ob_clean() : basic functionality ***\n";
          |
          |// Zero arguments
          |echo "\n-- Testing ob_clean() function with Zero arguments --\n";
          |var_dump( ob_clean() );
          |
          |ob_start();
          |echo "You should never see this.";
          |var_dump(ob_clean());
          |
          |echo "Ensure the buffer is still active after the clean.";
          |$out = ob_get_clean();
          |var_dump($out);
          |
          |echo "Done";
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """*** Testing ob_clean() : basic functionality ***
          |
          |-- Testing ob_clean() function with Zero arguments --
          |
          |Notice: ob_clean(): failed to delete buffer. No buffer to delete in /output/OutputBufferCleanSpec.inlinePhp on line 12
          |bool(false)
          |string(61) "bool(true)
          |Ensure the buffer is still active after the clean."
          |Done""".stripMargin
      )
    }

    "Test return type and value, as well as basic behaviour, of ob_get_clean()" in {
      // output/ob_get_clean_basic_001.phpt
      script(
        """<?php
          |/*
          | * proto bool ob_get_clean(void)
          | * Function is implemented in main/output.c
          |*/
          |
          |var_dump(ob_get_clean());
          |
          |ob_start();
          |echo "Hello World";
          |var_dump(ob_get_clean());
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """bool(false)
          |string(11) "Hello World"
          |""".stripMargin
      )
    }

    "Test basic behaviour of ob_get_clean()" in {
      // output/ob_get_clean_basic_002.phpt
      script(
        """<?php
          |/*
          | * proto bool ob_get_clean(void)
          | * Function is implemented in main/output.c
          |*/
          |
          |ob_start();
          |
          |echo "Hello World";
          |
          |$out = ob_get_clean();
          |$out = strtolower($out);
          |
          |var_dump($out);
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """string(11) "hello world"
          |""".stripMargin
      )
    }
  }
}
