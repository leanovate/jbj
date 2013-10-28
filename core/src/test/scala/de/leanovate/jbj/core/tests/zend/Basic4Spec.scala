/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                 (Tests based on PHP)   */

package de.leanovate.jbj.core.tests.zend

import org.specs2.mutable.SpecificationWithJUnit
import de.leanovate.jbj.core.tests.TestJbjExecutor

class Basic4Spec extends SpecificationWithJUnit with TestJbjExecutor {
  "Basic tests 030-038" should {
    "Testing array with '[]' passed as argument by value" in {
      // Zend/tests/031.phpt
      script(
        """<?php
          |
          |function test($var) { }
          |test($arr[]);
          |
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """
          |Fatal error: Cannot use [] for reading in /zend/Basic4Spec.inlinePhp on line 4
          |""".stripMargin
      )
    }

    "Testing array with '[]' passed as argument by reference" in {
      // ../php-src/Zend/tests/032.phpt
      script(
        """<?php
          |
          |function test(&$var) { }
          |test($arr[]);
          |
          |print "ok!\n";
          |
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """ok!
          |""".stripMargin
      )
    }

    "Using undefined multidimensional array" in {
      // Zend/tests/033.phpt
      script(
        """<?php
          |
          |$arr[1][2][3][4][5];
          |
          |echo $arr[1][2][3][4][5];
          |
          |$arr[1][2][3][4][5]->foo;
          |
          |$arr[1][2][3][4][5]->foo = 1;
          |
          |$arr[][] = 2;
          |
          |$arr[][]->bar = 2;
          |
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """
          |Notice: Undefined variable: arr in /zend/Basic4Spec.inlinePhp on line 3
          |
          |Notice: Undefined variable: arr in /zend/Basic4Spec.inlinePhp on line 5
          |
          |Notice: Undefined variable: arr in /zend/Basic4Spec.inlinePhp on line 7
          |
          |Notice: Trying to get property of non-object in /zend/Basic4Spec.inlinePhp on line 7
          |
          |Warning: Creating default object from empty value in /zend/Basic4Spec.inlinePhp on line 9
          |
          |Warning: Creating default object from empty value in /zend/Basic4Spec.inlinePhp on line 13
          |""".stripMargin
      )
    }

    "Testing multiples 'default:' in switch" in {
      // Zend/tests/034.phpt
      script(
        """<?php
          |
          |switch (1) {
          |	case 2:
          |		print 'foo';
          |		break;
          |	case 3:
          |		print 'bar';
          |		break;
          |	default:
          |		print 1;
          |		break;
          |	default:
          |		print 2;
          |		break;
          |	default:
          |		print 3;
          |		break;
          |}
          |
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """3""".stripMargin
      )
    }

    "Using 'static' and 'global' in global scope" in {
      // Zend/tests/035.phpt
      script(
        """<?php
          |
          |static $var, $var, $var = -1;
          |var_dump($var);
          |
          |global $var, $var, $var;
          |var_dump($var);
          |
          |var_dump($GLOBALS['var']);
          |
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """int(-1)
          |int(-1)
          |int(-1)
          |""".stripMargin
      )
    }

    "Trying to use lambda in array offset" in {
      // Zend/tests/036.phpt
      script(
        """<?php
          |
          |$test[function(){}] = 1;
          |$a{function() { }} = 1;
          |
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """
          |Warning: Illegal offset type in /zend/Basic4Spec.inlinePhp on line 3
          |
          |Warning: Illegal offset type in /zend/Basic4Spec.inlinePhp on line 4
          |""".stripMargin
      )
    }
  }
}
