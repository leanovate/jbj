package de.leanovate.jbj.core.tests.basic

import de.leanovate.jbj.core.tests.TestJbjExecutor
import org.specs2.mutable.SpecificationWithJUnit

class BugsSpec extends SpecificationWithJUnit with TestJbjExecutor {
  "Bugs" should {
    "Bug #20539 (PHP CLI Segmentation Fault)" in {
      // basic/bug20539.phpt
      script(
        """<?php
          |	print "good :)\n";
          |	$filename = '/tmp' . '/sess_' . session_id();
          |	var_dump(file_exists($filename));
          |	@unlink($filename);
          |?>
          |""".stripMargin
      ).withSessionAutoStart(true).result must haveOutput(
        """good :)
          |bool(true)
          |""".stripMargin
      )
    }
  }
}
