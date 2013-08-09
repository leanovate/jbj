package de.leanovate.jbj.tests.lang

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.FreeSpec
import de.leanovate.jbj.tests.TestJbjExecutor
import org.scalatest.matchers.MustMatchers

@RunWith(classOf[JUnitRunner])
class Lang5Spec extends FreeSpec with TestJbjExecutor with MustMatchers {
  "Language test 5" - {
    "Dynamic call for static methods" in {
      // lang/043
      script(
        """<?php
          |class A {
          |    static function foo() { return 'foo'; }
          |}
          |
          |$classname       =  'A';
          |$wrongClassname  =  'B';
          |
          |echo $classname::foo()."\n";
          |echo $wrongClassname::foo()."\n";
          |?>
          |===DONE===""".stripMargin
      ).result must haveOutput(
        """foo
          |
          |Fatal error: Class 'B' not found in /lang/Lang5Spec.inlinePhp on line 10
          |""".stripMargin
      )
    }

    "Dynamic call for static methods dynamically named" in {
      // lang/044
      script(
        """<?php
          |class A {
          |    static function foo() { return 'foo'; }
          |}
          |$classname        =  'A';
          |$wrongClassname   =  'B';
          |
          |$methodname       =  'foo';
          |
          |echo $classname::$methodname()."\n";
          |
          |echo $wrongClassname::$methodname()."\n";
          |?>
          |===DONE===""".stripMargin
      ).result must haveOutput(
        """foo
          |
          |Fatal error: Class 'B' not found in /lang/Lang5Spec.inlinePhp on line 12
          |""".stripMargin
      )
    }
  }
}
