/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                 (Tests based on PHP)   */

package de.leanovate.jbj.core.tests.parsing

import de.leanovate.jbj.core.tests.TestJbjExecutor
import org.specs2.mutable.SpecificationWithJUnit

class TagSpec extends SpecificationWithJUnit with TestJbjExecutor {
  "Script Tag" should {
    "<?php" in {
      script(
        """Start <div><?php echo "Hello World"?></div> End"""
      ).result must haveOutput(
        """Start <div>Hello World</div> End"""
      )
    }

    "<?" in {
      script(
        """Start <div><? echo "Hello World"?></div> End"""
      ).withShortOpenTag(true).result must haveOutput(
        """Start <div>Hello World</div> End"""
      )
    }

    "<?=" in {
      script(
        """Start <div><?= "Hello World"?></div> End"""
      ).withShortOpenTag(true).result must haveOutput(
        """Start <div>Hello World</div> End"""
      )
    }

    "<%" in {
      script(
        """Start <div><% echo "Hello World"%></div> End"""
      ).withAspTags(true).result must haveOutput(
        """Start <div>Hello World</div> End"""
      )
    }

    "<%=" in {
      script(
        """Start <div><%= "Hello World"%></div> End"""
      ).withAspTags(true).result must haveOutput(
        """Start <div>Hello World</div> End"""
      )
    }

    """<script language="php">""" in {
      script(
        """Start <div><script language="php">echo "Hello World"</script></div> End"""
      ).result must haveOutput(
        """Start <div>Hello World</div> End"""
      )
    }
  }
}
