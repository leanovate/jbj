package de.leanovate.jbj.tests.classes

import de.leanovate.jbj.tests.TestJbjExecutor
import org.specs2.mutable.SpecificationWithJUnit

class FinalSpec extends SpecificationWithJUnit with TestJbjExecutor {
  "Final" should {
    "ZE2 A method may be redeclared final" in {
      // classes/final
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
