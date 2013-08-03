package de.leanovate.jbj.tests.lang

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.FreeSpec
import de.leanovate.jbj.tests.TestJbjExecutor
import org.scalatest.matchers.MustMatchers

@RunWith(classOf[JUnitRunner])
class ScriptTagSpec extends FreeSpec with TestJbjExecutor with MustMatchers {
  "Script Tag" - {
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
  }

}
