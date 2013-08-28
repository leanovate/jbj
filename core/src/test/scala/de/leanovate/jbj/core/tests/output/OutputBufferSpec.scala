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
          | """.stripMargin
      ).result must haveOutput(
        """foo
          | """.stripMargin
      )
    }

    "output buffering - ob_start" in {
      // output/ob_002.phpt
      script(
        """<?php
          |ob_start();
          |echo "foo\n";
          |?>
          | """.stripMargin
      ).result must haveOutput(
        """foo
          | """.stripMargin
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
          | """.stripMargin
      ).result must haveOutput(
        """foo
          |bar
          | """.stripMargin
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
          | """.stripMargin
      ).result must haveOutput(
        """bar
          | """.stripMargin
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
          | """.stripMargin
      ).result must haveOutput(
        """foo
          |baz
          | """.stripMargin
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
          | """.stripMargin
      ).result must haveOutput(
        """foo
          |int(0)
          | """.stripMargin
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
          | """.stripMargin
      ).result must haveOutput(
        """string(4) "foo
          |"
          | """.stripMargin
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
          | """.stripMargin
      ).result must haveOutput(
        """foo
          |foo
          | """.stripMargin
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
          | """.stripMargin
      ).result must haveOutput(
        """foo
          |string(4) "foo
          |"
          | """.stripMargin
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

    "output buffering - handlers/status" in {
      // output/ob_013.phpt
      script(
        """<?php
          |function a($s){return $s;}
          |function b($s){return $s;}
          |function c($s){return $s;}
          |function d($s){return $s;}
          |
          |ob_start();
          |ob_start('a');
          |ob_start('b');
          |ob_start('c');
          |ob_start('d');
          |ob_start();
          |
          |echo "foo\n";
          |
          |ob_flush();
          |ob_end_clean();
          |ob_flush();
          |
          |print_r(ob_list_handlers());
          |print_r(ob_get_status());
          |print_r(ob_get_status(true));
          |
          |?>
          | """.stripMargin
      ).result must haveOutput(
        """foo
          |Array
          |(
          |    [0] => default output handler
          |    [1] => a
          |    [2] => b
          |    [3] => c
          |    [4] => d
          |)
          |Array
          |(
          |    [name] => d
          |    [type] => 1
          |    [flags] => 20593
          |    [level] => 4
          |    [chunk_size] => 0
          |    [buffer_size] => 16384
          |    [buffer_used] => 96
          |)
          |Array
          |(
          |    [0] => Array
          |        (
          |            [name] => default output handler
          |            [type] => 0
          |            [flags] => 112
          |            [level] => 0
          |            [chunk_size] => 0
          |            [buffer_size] => 16384
          |            [buffer_used] => 0
          |        )
          |
          |    [1] => Array
          |        (
          |            [name] => a
          |            [type] => 1
          |            [flags] => 113
          |            [level] => 1
          |            [chunk_size] => 0
          |            [buffer_size] => 16384
          |            [buffer_used] => 0
          |        )
          |
          |    [2] => Array
          |        (
          |            [name] => b
          |            [type] => 1
          |            [flags] => 113
          |            [level] => 2
          |            [chunk_size] => 0
          |            [buffer_size] => 16384
          |            [buffer_used] => 0
          |        )
          |
          |    [3] => Array
          |        (
          |            [name] => c
          |            [type] => 1
          |            [flags] => 113
          |            [level] => 3
          |            [chunk_size] => 0
          |            [buffer_size] => 16384
          |            [buffer_used] => 4
          |        )
          |
          |    [4] => Array
          |        (
          |            [name] => d
          |            [type] => 1
          |            [flags] => 20593
          |            [level] => 4
          |            [chunk_size] => 0
          |            [buffer_size] => 16384
          |            [buffer_used] => 249
          |        )
          |
          |)
          | """.stripMargin
      )
    }

    "output buffering - stati" in {
      // output/ob_017.phpt
      script(
        """<?php
          |$stati = array();
          |function oh($str, $flags) {
          |	global $stati;
          |	$stati[] = "$flags: $str";
          |	return $str;
          |}
          |ob_start("oh", 3);
          |echo "yes";
          |echo "!\n";
          |ob_flush();
          |echo "no";
          |ob_clean();
          |echo "yes!\n";
          |echo "no";
          |ob_end_clean();
          |print_r($stati);
          |?>
          | """.stripMargin
      ).result must haveOutput(
        """yes!
          |yes!
          |Array
          |(
          |    [0] => 1: yes
          |    [1] => 4: !
          |
          |    [2] => 2: no
          |    [3] => 0: yes!
          |
          |    [4] => 10: no
          |)
          | """.stripMargin
      )
    }

    "output buffering - ob_list_handlers" in {
      // output/ob_020.phpt
      script(
        """<?php
          |print_r(ob_list_handlers());
          |
          |ob_start();
          |print_r(ob_list_handlers());
          |
          |ob_start();
          |print_r(ob_list_handlers());
          |
          |ob_end_flush();
          |print_r(ob_list_handlers());
          |
          |ob_end_flush();
          |print_r(ob_list_handlers());
          |?>
          | """.stripMargin
      ).result must haveOutput(
        """Array
          |(
          |)
          |Array
          |(
          |    [0] => default output handler
          |)
          |Array
          |(
          |    [0] => default output handler
          |    [1] => default output handler
          |)
          |Array
          |(
          |    [0] => default output handler
          |)
          |Array
          |(
          |)
          | """.stripMargin
      )
    }
  }
}
