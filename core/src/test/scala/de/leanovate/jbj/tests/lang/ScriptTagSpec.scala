package de.leanovate.jbj.tests.lang

import de.leanovate.jbj.tests.TestJbjExecutor
import org.specs2.mutable.SpecificationWithJUnit

class ScriptTagSpec extends SpecificationWithJUnit with TestJbjExecutor {
  "Script Tag" should {
    "<script> tag" in {
      // lang/script_tag
      script(
        """<script language=php> echo "ola\n";</script>
          |<script language="php"> echo "ola2\n";</script>
          |<script language='php'> echo "ola3\n";</script>
          |texto <sc <s <script> <script language> <script language=>
          |<script language=php>
          |#comment
          |echo "oi\n"; //ignore here
          |# 2nd comment
          |""".stripMargin
      ).result must haveOutput (
        """ola
          |ola2
          |ola3
          |texto <sc <s <script> <script language> <script language=>
          |oi
          |""".stripMargin
      )
    }

    "short_open_tag: On" in {
      // lang/short_tags_001
      script(
        """<?
          |echo "Used a short tag\n";
          |?>
          |Finished""".stripMargin
      ).result must haveOutput(
        """Used a short tag
          |Finished""".stripMargin
      )
    }
  }

}
