/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                 (Tests based on PHP)   */

package de.leanovate.jbj.core.tests.parsing

import de.leanovate.jbj.core.tests.TestJbjExecutor
import org.specs2.mutable.SpecificationWithJUnit

class HeredocSpec extends SpecificationWithJUnit with TestJbjExecutor {
  "Heredoc" should {
    "Static heredoc" in {
      script(
        """<?php
          |
          |$a=<<<END
          |First line
          |Second line
          |END;
          |
          |echo $a;
          |
          |?>""".stripMargin
      ).result must haveOutput(
        """First line
          |Second line""".stripMargin
      )
    }

    "Simple variable interpolation" in {
      script(
        """<?php
          |
          |$a=123;
          |$b="Test";
          |
          |$c=<<<SOMETHING
          |Int: $a
          |Str: $b
          |SOMETHING;
          |
          |echo $c;
          |
          |?>""".stripMargin
      ).result must haveOutput(
        """Int: 123
          |Str: Test""".stripMargin
      )
    }
  }
}
