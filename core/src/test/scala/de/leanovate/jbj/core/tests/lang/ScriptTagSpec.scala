/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                 (Tests based on PHP)   */

package de.leanovate.jbj.core.tests.lang

import de.leanovate.jbj.core.tests.TestJbjExecutor
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
          | """.stripMargin
      ).result must haveOutput(
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
      ).withShortOpenTag(true).result must haveOutput(
        """Used a short tag
          |Finished""".stripMargin
      )
    }

    "short_open_tag: Off" in {
      // lang/short_tags.002.phpt
      script(
        """<?
          |echo "Used a short tag\n";
          |?>
          |Finished
          |""".stripMargin
      ).withShortOpenTag(false).result must haveOutput(
        """<?
          |echo "Used a short tag\n";
          |?>
          |Finished
          |""".stripMargin
      )
    }

    "short_open_tag: On, asp_tags: On" in {
      // lang/short_tags.003.phpt
      script(
        """<?='this should get echoed'?>
          |
          |<%= 'so should this' %>
          |
          |<?php
          |$a = 'This gets echoed twice';
          |?>
          |
          |<?= $a?>
          |
          |<%= $a%>
          |
          |<? $b=3; ?>
          |
          |<?php
          |   echo "{$b}";
          |?>
          |""".stripMargin
      ).withShortOpenTag(true).withAspTags(true).result must haveOutput(
        """this should get echoed
          |so should this
          |
          |This gets echoed twice
          |This gets echoed twice
          |
          |3""".stripMargin
      )
    }

    "short_open_tag: Off, asp_tags: Off" in {
      // lang/short_tags.004.phpt
      script(
        """<%= 'so should this' %>
          |
          |<?php
          |$a = 'This gets echoed twice';
          |?>
          |
          |<?= $a?>
          |
          |<%= $a%>
          |
          |<? $b=3; ?>
          |
          |<?php
          |   echo "{$b}";
          |?>
          |<?= "{$b}"?>
          |""".stripMargin
      ).withShortOpenTag(false).withAspTags(false).result must haveOutput(
        """<%= 'so should this' %>
          |
          |
          |This gets echoed twice
          |<%= $a%>
          |
          |<? $b=3; ?>
          |
          |
          |Notice: Undefined variable: b in /lang/ScriptTagSpec.inlinePhp on line 14
          |
          |Notice: Undefined variable: b in /lang/ScriptTagSpec.inlinePhp on line 16
          |""".stripMargin
      )
    }
  }
}
