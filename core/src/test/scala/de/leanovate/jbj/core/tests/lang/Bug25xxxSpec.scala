/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                 (Tests based on PHP)   */

package de.leanovate.jbj.core.tests.lang

import org.specs2.mutable.SpecificationWithJUnit
import de.leanovate.jbj.core.tests.TestJbjExecutor

class Bug25xxxSpec extends SpecificationWithJUnit with TestJbjExecutor {
  "Bugs #25xxx" should {
    "Bug #25547 (error_handler and array index with function call)" in {
      // lang/bug25547.phpt
      script(
        """<?php
          |
          |function handler($errno, $errstr, $errfile, $errline, $context)
          |{
          |	echo __FUNCTION__ . "($errstr)\n";
          |}
          |
          |set_error_handler('handler');
          |
          |function foo($x) {
          |	return "foo";
          |}
          |
          |$output = array();
          |++$output[foo("bar")];
          |
          |print_r($output);
          |
          |echo "Done";
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """handler(Undefined index: foo)
          |Array
          |(
          |    [foo] => 1
          |)
          |Done""".stripMargin
      )
    }
  }
}
