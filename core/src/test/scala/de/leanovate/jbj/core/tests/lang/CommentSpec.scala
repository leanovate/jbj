/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                 (Tests based on PHP)   */

package de.leanovate.jbj.core.tests.lang

import de.leanovate.jbj.core.tests.TestJbjExecutor
import org.specs2.mutable.SpecificationWithJUnit

class CommentSpec extends SpecificationWithJUnit with TestJbjExecutor{
  "Comment" should {
    "#-style comments" in {
      // lang/comments
      script(
        """#teste
          |#teste2
          |<?php
          |
          |#ahahah
          |#ahhfhf
          |
          |echo '#ola'; //?
          |echo "\n";
          |echo 'uhm # ah'; #ah?
          |echo "\n";
          |echo "e este, # hein?";
          |echo "\n";
          |
          |?>""".stripMargin
      ).result must haveOutput(
        """#teste
          |#teste2
          |#ola
          |uhm # ah
          |e este, # hein?
          |""".stripMargin
      )
    }

    "#-style comments (part 2)" in {
      // lang/comments2
      script(
        """<?php
          |if (1) {
          |?>
          |#<?php }""".stripMargin
      ).result must haveOutput(
        """#""".stripMargin
      )
    }
  }
}
