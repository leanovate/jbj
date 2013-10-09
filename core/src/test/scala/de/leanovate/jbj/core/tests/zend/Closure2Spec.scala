/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                 (Tests based on PHP)   */

package de.leanovate.jbj.core.tests.zend

import org.specs2.mutable.SpecificationWithJUnit
import de.leanovate.jbj.core.tests.TestJbjExecutor

class Closure2Spec extends SpecificationWithJUnit with TestJbjExecutor {
  "Closure tests 010-019" should {
    "Closure 010: Closure calls itself" in {
      // Zend/tests/closure_010.phpt
      script(
        """<?php
          |$i = 3;
          |$lambda = function ($lambda) use (&$i) {
          |    if ($i==0) return;
          |    echo $i--."\n";
          |    $lambda($lambda);
          |};
          |$lambda($lambda);
          |echo "$i\n";
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """3
          |2
          |1
          |0
          |""".stripMargin
      )
    }

    "Closure 011: Lexical copies not static in closure" in {
      // Zend/tests/closure_011.phpt
      script(
        """<?php
          |$i = 1;
          |$lambda = function () use ($i) {
          |    return ++$i;
          |};
          |$lambda();
          |echo $lambda()."\n";
          |//early prototypes gave 3 here because $i was static in $lambda
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """2
          |""".stripMargin
      )
    }

    "Closure 012: Undefined lexical variables" in {
      // Zend/tests/closure_012.phpt
      script(
        """<?php
          |$lambda = function () use ($i) {
          |    return ++$i;
          |};
          |$lambda();
          |$lambda();
          |var_dump($i);
          |$lambda = function () use (&$i) {
          |    return ++$i;
          |};
          |$lambda();
          |$lambda();
          |var_dump($i);
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """
          |Notice: Undefined variable: i in /zend/Closure2Spec.inlinePhp on line 2
          |
          |Notice: Undefined variable: i in /zend/Closure2Spec.inlinePhp on line 7
          |NULL
          |int(2)
          |""".stripMargin
      )
    }
  }
}
