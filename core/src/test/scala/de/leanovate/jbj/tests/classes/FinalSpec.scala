package de.leanovate.jbj.tests.classes

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.FreeSpec
import de.leanovate.jbj.tests.TestJbjExecutor
import org.scalatest.matchers.MustMatchers

@RunWith(classOf[JUnitRunner])
class FinalSpec extends FreeSpec with TestJbjExecutor with MustMatchers {
  "Final" - {
    "ZE2 A method may be redeclared final" in {
      // class/final
      script(
        """<?php
          |
          |class first {
          |	function show() {
          |		echo "Call to function first::show()\n";
          |	}
          |}
          |
          |$t = new first();
          |$t->show();
          |
          |class second extends first {
          |	final function show() {
          |		echo "Call to function second::show()\n";
          |	}
          |}
          |
          |$t2 = new second();
          |$t2->show();
          |
          |echo "Done\n";
          |?>""".stripMargin
      ).result must haveOutput(
        """Call to function first::show()
          |Call to function second::show()
          |Done
          |""".stripMargin
      )
    }
  }
}
