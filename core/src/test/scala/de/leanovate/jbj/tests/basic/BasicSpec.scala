package de.leanovate.jbj.tests.basic

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.FreeSpec
import de.leanovate.jbj.tests.TestJbjExecutor
import org.scalatest.matchers.MustMatchers

@RunWith(classOf[JUnitRunner])
class BasicSpec extends FreeSpec with TestJbjExecutor with MustMatchers {
  "Basic test" - {
    "Trivial \"Hello World\" test" in {
      // basic/001
      script(
        """<?php echo "Hello World"?>"""
      ) must haveOutput(
        """Hello World"""
      )
    }

    "Add 3 variables together and print result" in {
      // basic/006
      script(
        """<?php $a=1; $b=2; $c=3; $d=$a+$b+$c; echo $d?>"""
      ) must haveOutput(
        """6"""
      )
    }

    "Multiply 3 variables and print result" in {
      // basic/007
      script(
        """<?php $a=2; $b=4; $c=8; $d=$a*$b*$c; echo $d?>"""
      ) must haveOutput(
        """64"""
      )
    }

    "Divide 3 variables and print result" in {
      // basic/008
      script(
        """<?php $a=27; $b=3; $c=3; $d=$a/$b/$c; echo $d?>"""
      ) must haveOutput(
        """3.0"""
      )
    }

    "Subtract 3 variables and print result" in {
      // basic/009
      script(
        """<?php $a=27; $b=7; $c=10; $d=$a-$b-$c; echo $d?>"""
      ) must haveOutput(
        """10"""
      )
    }


  }
}
