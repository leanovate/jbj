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

    "output buffering - ob_get_clean" in {
      // output/ob_007.phpt
      script(
        """<?php
          |ob_start();
          |echo "foo\n";
          |var_dump(ob_get_clean());
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """string(4) "foo
          |"
          |""".stripMargin
      )
    }

    "output buffering - ob_get_contents" in {
      // output/ob_008.phpt
      script(
        """<?php
          |ob_start();
          |echo "foo\n";
          |echo ob_get_contents();
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """foo
          |foo
          |""".stripMargin
      )
    }

    "output buffering - ob_get_flush" in {
      // output/ob_009.phpt
      script(
        """<?php
          |ob_start();
          |echo "foo\n";
          |var_dump(ob_get_flush());
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """foo
          |string(4) "foo
          |"
          |""".stripMargin
      )
    }

    "output buffering - fatalism" in {
      // output/ob_010.phpt
      script(
        """<?php
          |function obh($s)
          |{
          |	print_r($s, 1);
          |}
          |ob_start("obh");
          |echo "foo\n";
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """
          |Fatal error: print_r(): Cannot use output buffering in output buffering display handlers in /output/OutputBufferSpec.inlinePhp on line 4
          |""".stripMargin
      )
    }

    "output buffering - fatalism" in {
      // output/ob_011.phpt
      script(
        """<?php
          |function obh($s)
          |{
          |	return ob_get_flush();
          |}
          |ob_start("obh");
          |echo "foo\n";
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """
          |Fatal error: ob_get_flush(): Cannot use output buffering in output buffering display handlers in /output/OutputBufferSpec.inlinePhp on line 4
          |""".stripMargin
      )
    }

    "output buffering - multiple" in {
      // output/ob_012.phpt
      script(
        """<?php
          |echo 0;
          |	ob_start();
          |		ob_start();
          |			ob_start();
          |				ob_start();
          |					echo 1;
          |				ob_end_flush();
          |				echo 2;
          |			$ob = ob_get_clean();
          |		echo 3;
          |		ob_flush();
          |		ob_end_clean();
          |	echo 4;
          |	ob_end_flush();
          |echo $ob;
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """03412""".stripMargin
      )
    }
  }
}
