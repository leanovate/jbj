/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                 (Tests based on PHP)   */

package de.leanovate.jbj.core.tests.zend

import org.specs2.mutable.SpecificationWithJUnit
import de.leanovate.jbj.core.tests.TestJbjExecutor

class Closure1Spec  extends SpecificationWithJUnit with TestJbjExecutor{
  "Closure tests 001-009" should {
    "Closure 001: Lambda without lexical variables" in {
      // Zend/tests/closure_001.phpt
      script(
        """<?php
          |
          |$lambda1 = function () {
          |	echo "Hello World!\n";
          |};
          |
          |$lambda2 = function ($x) {
          |	echo "Hello $x!\n";
          |};
          |
          |var_dump(is_callable($lambda1));
          |var_dump(is_callable($lambda2));
          |$lambda1();
          |$lambda2("Universe");
          |call_user_func($lambda1);
          |call_user_func($lambda2, "Universe");
          |
          |echo "Done\n";
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """bool(true)
          |bool(true)
          |Hello World!
          |Hello Universe!
          |Hello World!
          |Hello Universe!
          |Done
          |""".stripMargin
      )
    }

    "Closure 002: Lambda with lexical variables (global scope)" in {
      // ../php-src/Zend/tests/closure_002.phpt
      script(
        """<?php
          |
          |$x = 4;
          |
          |$lambda1 = function () use ($x) {
          |	echo "$x\n";
          |};
          |
          |$lambda2 = function () use (&$x) {
          |	echo "$x\n";
          |};
          |
          |$lambda1();
          |$lambda2();
          |$x++;
          |$lambda1();
          |$lambda2();
          |
          |echo "Done\n";
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """4
          |4
          |4
          |5
          |Done
          |""".stripMargin
      )
    }

    "Closure 003: Lambda with lexical variables (local scope)" in {
      // ../php-src/Zend/tests/closure_003.phpt
      script(
        """<?php
          |
          |function run () {
          |	$x = 4;
          |
          |	$lambda1 = function () use ($x) {
          |		echo "$x\n";
          |	};
          |
          |	$lambda2 = function () use (&$x) {
          |		echo "$x\n";
          |	};
          |
          |	$lambda1();
          |	$lambda2();
          |	$x++;
          |	$lambda1();
          |	$lambda2();
          |}
          |
          |run();
          |
          |echo "Done\n";
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """4
          |4
          |4
          |5
          |Done
          |""".stripMargin
      )
    }

    "Closure 004: Lambda with lexical variables (scope lifetime)" in {
      // ../php-src/Zend/tests/closure_004.phpt
      script(
        """<?php
          |
          |function run () {
          |	$x = 4;
          |
          |	$lambda1 = function () use ($x) {
          |		echo "$x\n";
          |	};
          |
          |	$lambda2 = function () use (&$x) {
          |		echo "$x\n";
          |		$x++;
          |	};
          |
          |	return array($lambda1, $lambda2);
          |}
          |
          |list ($lambda1, $lambda2) = run();
          |
          |$lambda1();
          |$lambda2();
          |$lambda1();
          |$lambda2();
          |
          |echo "Done\n";
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """4
          |4
          |4
          |5
          |Done
          |""".stripMargin
      )
    }
  }
}
