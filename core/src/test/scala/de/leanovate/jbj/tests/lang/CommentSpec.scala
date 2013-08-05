package de.leanovate.jbj.tests.lang

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.FreeSpec
import de.leanovate.jbj.tests.TestJbjExecutor
import org.scalatest.matchers.MustMatchers

@RunWith(classOf[JUnitRunner])
class CommentSpec extends FreeSpec with TestJbjExecutor with MustMatchers {
  "Comment" - {
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
