/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                 (Tests based on PHP)   */

package de.leanovate.jbj.core.tests.special

import org.specs2.mutable.SpecificationWithJUnit
import de.leanovate.jbj.core.tests.TestJbjExecutor

class StringFuncSpec extends SpecificationWithJUnit with TestJbjExecutor {
  "string functions" should {
    "strpos / strstr" in {
      script(
        """<?php
          |
          |var_dump(strstr("haystackneedlehaystackneedlehaystack", "needle"));
          |var_dump(strstr("haystackneedlehaystackneedlehaystack", "needle", true));
          |var_dump(strstr("haystackneedelhaystackneedelhaystack", "needle"));
          |var_dump(strstr("haystackneedelhaystackneedelhaystack", "needle", true));
          |
          |var_dump(strpos("haystackneedlehaystackneedlehaystack", "needle"));
          |var_dump(strpos("haystackneedlehaystackneedlehaystack", "needle", 20));
          |var_dump(strpos("haystackneedelhaystackneedelhaystack", "needle"));
          |var_dump(strpos("haystackneedelhaystackneedelhaystack", "needle", 20));
          |""".stripMargin
      ).result must haveOutput(
        """string(28) "needlehaystackneedlehaystack"
          |string(8) "haystack"
          |bool(false)
          |bool(false)
          |int(8)
          |int(22)
          |bool(false)
          |bool(false)
          |""".stripMargin
      )
    }

    "str_replace regular" in {
      script(
        """<?php
          |// Provides: <body text='black'>
          |$bodytag = str_replace("%body%", "black", "<body text='%body%'>");
          |echo $bodytag . "\n";
          |
          |// Provides: Hll Wrld f PHP
          |$vowels = array("a", "e", "i", "o", "u", "A", "E", "I", "O", "U");
          |$onlyconsonants = str_replace($vowels, "", "Hello World of PHP");
          |echo $onlyconsonants . "\n";
          |
          |// Provides: You should eat pizza, beer, and ice cream every day
          |$phrase  = "You should eat fruits, vegetables, and fiber every day.";
          |$healthy = array("fruits", "vegetables", "fiber");
          |$yummy   = array("pizza", "beer", "ice cream");
          |
          |$newphrase = str_replace($healthy, $yummy, $phrase);
          |echo $newphrase . "\n";
          |
          |// Provides: 2
          |$count = 0;
          |$str = str_replace("ll", "", "good golly miss molly!", $count);
          |echo $count . "\n";
          |echo $str . "\n";
          |?>""".stripMargin
      ).result must haveOutput(
        """<body text='black'>
          |Hll Wrld f PHP
          |You should eat pizza, beer, and ice cream every day.
          |2
          |good goy miss moy!
          |""".stripMargin
      )
    }

    "str_replace gotchas" in {
      script(
        """<?php
          |// Order of replacement
          |$str     = "Line 1\nLine 2\rLine 3\r\nLine 4\n";
          |$order   = array("\r\n", "\n", "\r");
          |$replace = '<br />';
          |
          |// Processes \r\n's first so they aren't converted twice.
          |$newstr = str_replace($order, $replace, $str);
          |echo $newstr . "\n";
          |
          |// Outputs F because A is replaced with B, then B is replaced with C, and so on...
          |// Finally E is replaced with F, because of left to right replacements.
          |$search  = array('A', 'B', 'C', 'D', 'E');
          |$replace = array('B', 'C', 'D', 'E', 'F');
          |$subject = 'A';
          |echo str_replace($search, $replace, $subject) . "\n";
          |
          |// Outputs: apearpearle pear
          |// For the same reason mentioned above
          |$letters = array('a', 'p');
          |$fruit   = array('apple', 'pear');
          |$text    = 'a p';
          |$output  = str_replace($letters, $fruit, $text);
          |echo $output . "\n";
          |?>""".stripMargin
      ).result must haveOutput(
        """Line 1<br />Line 2<br />Line 3<br />Line 4<br />
          |F
          |apearpearle pear
          |""".stripMargin
      )
    }
  }
}
