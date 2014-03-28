package de.leanovate.jbj.core.tests.zend

import org.specs2.mutable.SpecificationWithJUnit
import de.leanovate.jbj.core.tests.TestJbjExecutor

class DivSpec extends SpecificationWithJUnit with TestJbjExecutor {
  "Division" should {
    "dividing doubles" in {
      // ../php-src/Zend/tests/div_001.phpt
      script(
        """<?php
          |
          |$d1 = 1.1;
          |$d2 = 434234.234;
          |
          |$c = $d2 / $d1;
          |var_dump($c);
          |
          |$d1 = 1.1;
          |$d2 = "434234.234";
          |
          |$c = $d2 / $d1;
          |var_dump($c);
          |
          |$d1 = "1.1";
          |$d2 = "434234.234";
          |
          |$c = $d2 / $d1;
          |var_dump($c);
          |
          |echo "Done\n";
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """float(394758.39454545)
          |float(394758.39454545)
          |float(394758.39454545)
          |Done
          |""".stripMargin
      )
    }

    "dividing arrays" in {
      // ../php-src/Zend/tests/div_002.phpt
      script(
        """<?php
          |
          |$a = array(1,2,3);
          |$b = array(1);
          |
          |$c = $a / $b;
          |var_dump($c);
          |
          |echo "Done\n";
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """
          |Fatal error: Unsupported operand types in /zend/DivSpec.inlinePhp on line 6
          |""".stripMargin
      )
    }
  }
}
