/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                 (Tests based on PHP)   */

package de.leanovate.jbj.core.tests.lang

import org.specs2.mutable.SpecificationWithJUnit
import de.leanovate.jbj.core.tests.TestJbjExecutor

class Bug4xxxxSpec extends SpecificationWithJUnit with TestJbjExecutor{
  "Bug #4xxxx" should {
    "Bug #44654 (syntax error for #)" in {
      // lang/bug44654.phpt
      script(
        """#<?php echo 1; ?>
          |<?php if (1) { ?>#<?php } ?>
          |#<?php echo 1; ?>
          |""".stripMargin
      ).result must haveOutput(
        """#1##1""".stripMargin
      )
    }

    "Bug #44827 (Class error when trying to access :: as constant)" in {
      // lang/bug44827.phpt
      script(
        """<?php
          |define('::', true);
          |var_dump(constant('::'));
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """
          |Warning: Class constants cannot be defined or redefined in /lang/Bug4xxxxSpec.inlinePhp on line 2
          |
          |Warning: constant(): Couldn't find constant :: in /lang/Bug4xxxxSpec.inlinePhp on line 3
          |NULL
          |""".stripMargin
      )
    }

    "Bug #55754 (Only variables should be passed by reference for ZEND_SEND_PREFER_REF params)" in {
      // lang/bug55754.phpt
      script(
        """<?php
          |
          |current($arr = array(0 => "a"));
          |current(array(0 => "a"));
          |current($arr);
          |
          |echo "DONE";
          |
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """DONE""".stripMargin
      )
    }

    "Bug #7515 (weird & invisible referencing of objects)" in {
      // lang/bug7515.phpt
      script(
        """<?php
          |class obj {
          |	function method() {}
          |}
          |
          |$o->root=new obj();
          |
          |ob_start();
          |var_dump($o);
          |$x=ob_get_contents();
          |ob_end_clean();
          |
          |$o->root->method();
          |
          |ob_start();
          |var_dump($o);
          |$y=ob_get_contents();
          |ob_end_clean();
          |if ($x == $y) {
          |    print "success";
          |} else {
          |    print "failure
          |x=$x
          |y=$y
          |";
          |}
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """
          |Warning: Creating default object from empty value in /lang/Bug4xxxxSpec.inlinePhp on line 6
          |success""".stripMargin
      )
    }
  }
}
