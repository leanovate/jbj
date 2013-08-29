/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                 (Tests based on PHP)   */

package de.leanovate.jbj.core.tests.lang

import de.leanovate.jbj.core.tests.TestJbjExecutor
import org.specs2.mutable.SpecificationWithJUnit

class Lang5Spec extends SpecificationWithJUnit with TestJbjExecutor {
  "Language test 5" should {
    "foreach into array" in {
      // lang/040.phpt
      script(
        """<?php
          |$a = array(0,1);
          |$b[0]=2;
          |foreach($a as $b[0]) {
          |  echo $b[0]."\n";
          |}
          |?>
          |===DONE===
          |""".stripMargin
      ).result must haveOutput(
        """0
          |1
          |===DONE===
          |""".stripMargin
      )
    }

    "Dynamic access of static members" in {
      // lang/041.phpt
      script(
        """<?php
          |class A {
          |    public    static $b = 'foo';
          |}
          |
          |$classname       =  'A';
          |$wrongClassname  =  'B';
          |
          |echo $classname::$b."\n";
          |echo $wrongClassname::$b."\n";
          |
          |?>
          |===DONE===
          |""".stripMargin
      ).result must haveOutput(
        """foo
          |
          |Fatal error: Class 'B' not found in /lang/Lang5Spec.inlinePhp on line 10
          |""".stripMargin
      )
    }

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
