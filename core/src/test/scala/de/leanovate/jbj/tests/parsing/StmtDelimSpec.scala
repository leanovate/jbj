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
      resultOf(
        """<?php $a=1; if ($a < 2) { echo "Hurra"; } echo "bla" ?>"""
      ) must be(
        """Hurrabla"""
      )
    }

    "No ; at end of script" in {
      resultOf(
        """<?php echo "Hurra"; ?><?php echo "bla" ?>"""
      ) must be(
        """Hurrabla"""
      )
    }

    "Conditional inline" in {
      resultOf(
        """Begin <?php $a=1; if($a>2) { echo "true" ?>true<?php } else { echo "fa"; ?>lse<?php } ?> End"""
      ) must be(
        """Begin false End"""
      )

    }
  }
}
