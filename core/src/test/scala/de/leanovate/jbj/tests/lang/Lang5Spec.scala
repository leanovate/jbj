package de.leanovate.jbj.tests.lang

import de.leanovate.jbj.tests.TestJbjExecutor
import org.specs2.mutable.SpecificationWithJUnit

class Lang5Spec extends SpecificationWithJUnit with TestJbjExecutor {
  "Language test 5" should {
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
