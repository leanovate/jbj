package de.leanovate.jbj.tests.parsing

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.FreeSpec
import de.leanovate.jbj.tests.TestJbjExecutor
import org.scalatest.matchers.MustMatchers

@RunWith(classOf[JUnitRunner])
class StmtDelimSpec extends FreeSpec with TestJbjExecutor with MustMatchers {
  "Stmt delimiter tests" - {
    "No ; after block" in {
      script(
        """<?php $a=1; if ($a < 2) { echo "Hurra"; } echo "bla" ?>"""
      ) must haveOutput(
        """Hurrabla"""
      )
    }

    "No ; at end of script" in {
      script(
        """<?php echo "Hurra"; ?><?php echo "bla" ?>"""
      ) must haveOutput(
        """Hurrabla"""
      )
    }

    "Conditional inline" in {
      script(
        """Begin <?php $a=1; if($a>2) { echo "true" ?>true<?php } else { echo "fa"; ?>lse<?php } ?> End"""
      ) must haveOutput(
        """Begin false End"""
      )

    }
  }
}
