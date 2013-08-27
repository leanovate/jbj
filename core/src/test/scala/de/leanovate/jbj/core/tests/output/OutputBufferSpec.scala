package de.leanovate.jbj.core.tests.output

import org.specs2.mutable.SpecificationWithJUnit
import de.leanovate.jbj.core.tests.TestJbjExecutor

class OutputBufferSpec extends SpecificationWithJUnit with TestJbjExecutor {
  "output buffer" should {
    "output buffering - nothing" in {
      // output/ob_001.phpt
      script(
        """<?php
          |echo "foo\n";
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """foo
          |""".stripMargin
      )
    }
  }
}
