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

    "output buffering - ob_start" in {
      // output/ob_002.phpt
      script(
        """<?php
          |ob_start();
          |echo "foo\n";
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """foo
          |""".stripMargin
      )
    }

    "output buffering - ob_flush" in {
      // output/ob_003.phpt
      script(
        """<?php
          |ob_start();
          |echo "foo\n";
          |ob_flush();
          |echo "bar\n";
          |ob_flush();
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """foo
          |bar
          |""".stripMargin
      )
    }

    "output buffering - ob_clean" in {
      // output/ob_004.phpt
      script(
        """<?php
          |ob_start();
          |echo "foo\n";
          |ob_clean();
          |echo "bar\n";
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """bar
          |""".stripMargin
      )
    }

    "output buffering - ob_end_clean" in {
      // output/ob_005.phpt
      script(
        """<?php
          |ob_start();
          |echo "foo\n";
          |ob_start();
          |echo "bar\n";
          |ob_end_clean();
          |echo "baz\n";
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """foo
          |baz
          |""".stripMargin
      )
    }

    "output buffering - ob_end_flush" in {
      // output/ob_006.phpt
      script(
        """<?php
          |ob_start();
          |echo "foo\n";
          |ob_end_flush();
          |var_dump(ob_get_level());
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """foo
          |int(0)
          |""".stripMargin
      )
    }
  }
}
