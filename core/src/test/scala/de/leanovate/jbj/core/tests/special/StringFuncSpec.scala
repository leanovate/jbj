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
  }
}
