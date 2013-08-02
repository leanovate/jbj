package de.leanovate.jbj.tests.parsing

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.FreeSpec
import de.leanovate.jbj.tests.TestJbjExecutor
import org.scalatest.matchers.MustMatchers

@RunWith(classOf[JUnitRunner])
class TagSpec extends FreeSpec with TestJbjExecutor with MustMatchers {
  "Script Tag" - {
    "<?php" in {
      script(
        """Start <div><?php echo "Hello World"?></div> End"""
      ) must haveOutput(
        """Start <div>Hello World</div> End"""
      )
    }

    "<?" in {
      script(
        """Start <div><? echo "Hello World"?></div> End"""
      ) must haveOutput(
        """Start <div>Hello World</div> End"""
      )
    }

    "<?=" in {
      script(
        """Start <div><?= "Hello World"?></div> End"""
      ) must haveOutput(
        """Start <div>Hello World</div> End"""
      )
    }

    "<%" in {
      script(
        """Start <div><% echo "Hello World"%></div> End"""
      ) must haveOutput(
        """Start <div>Hello World</div> End"""
      )
    }

    "<%=" in {
      script(
        """Start <div><%= "Hello World"%></div> End"""
      ) must haveOutput(
        """Start <div>Hello World</div> End"""
      )
    }

    """<script language="php">""" in {
      script(
        """Start <div><script language="php">echo "Hello World"</script></div> End"""
      ) must haveOutput(
        """Start <div>Hello World</div> End"""
      )
    }
  }
}
