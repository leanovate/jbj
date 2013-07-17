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
      resultOf(
        """Start <div><?php echo "Hello World"?></div> End"""
      ) must be(
        """Start <div>Hello World</div> End"""
      )
    }

    "<?" in {
      resultOf(
        """Start <div><? echo "Hello World"?></div> End"""
      ) must be(
        """Start <div>Hello World</div> End"""
      )
    }

    "<?=" in {
      resultOf(
        """Start <div><?= "Hello World"?></div> End"""
      ) must be(
        """Start <div>Hello World</div> End"""
      )
    }

    "<%" in {
      resultOf(
        """Start <div><% echo "Hello World"%></div> End"""
      ) must be(
        """Start <div>Hello World</div> End"""
      )
    }

    "<%=" in {
      resultOf(
        """Start <div><%= "Hello World"%></div> End"""
      ) must be(
        """Start <div>Hello World</div> End"""
      )
    }

    """<script language="php">""" in {
      resultOf(
        """Start <div><script language="php">echo "Hello World"</script></div> End"""
      ) must be(
        """Start <div>Hello World</div> End"""
      )
    }
  }
}
