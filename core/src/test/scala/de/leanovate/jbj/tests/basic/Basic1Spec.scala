package de.leanovate.jbj.tests.basic

import de.leanovate.jbj.tests.TestJbjExecutor
import org.specs2.mutable.SpecificationWithJUnit

class Basic1Spec extends SpecificationWithJUnit with TestJbjExecutor {
  "Basic test 1" should {
    "Trivial \"Hello World\" test" in {
      // basic/001
      script(
        """<?php echo "Hello World"?>"""
      ).result must haveOutput(
        """Hello World"""
      )
    }

    "Add 3 variables together and print result" in {
      // basic/006
      script(
        """<?php $a=1; $b=2; $c=3; $d=$a+$b+$c; echo $d?>"""
      ).result must haveOutput(
        """6"""
      )
    }

    "Multiply 3 variables and print result" in {
      // basic/007
      script(
        """<?php $a=2; $b=4; $c=8; $d=$a*$b*$c; echo $d?>"""
      ).result must haveOutput(
        """64"""
      )
    }

    "Divide 3 variables and print result" in {
      // basic/008
      script(
        """<?php $a=27; $b=3; $c=3; $d=$a/$b/$c; echo $d?>"""
      ).result must haveOutput(
        """3"""
      )
    }

    "Subtract 3 variables and print result" in {
      // basic/009
      script(
        """<?php $a=27; $b=7; $c=10; $d=$a-$b-$c; echo $d?>"""
      ).result must haveOutput(
        """10"""
      )
    }


  }
}
