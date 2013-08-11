package de.leanovate.jbj.tests.parsing

import de.leanovate.jbj.tests.TestJbjExecutor
import org.specs2.mutable.SpecificationWithJUnit

class StmtDelimSpec extends SpecificationWithJUnit with TestJbjExecutor {
  "Stmt delimiter tests" should {
    "No ; after block" in {
      script(
        """<?php $a=1; if ($a < 2) { echo "Hurra"; } echo "bla" ?>"""
      ).result must haveOutput(
        """Hurrabla"""
      )
    }

    "No ; at end of script" in {
      script(
        """<?php echo "Hurra"; ?><?php echo "bla" ?>"""
      ).result must haveOutput(
        """Hurrabla"""
      )
    }

    "Conditional inline" in {
      script(
        """Begin <?php $a=1; if($a>2) { echo "true" ?>true<?php } else { echo "fa"; ?>lse<?php } ?> End"""
      ).result must haveOutput(
        """Begin false End"""
      )

    }
  }
}
