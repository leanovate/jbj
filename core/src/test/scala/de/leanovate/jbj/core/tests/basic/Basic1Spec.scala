package de.leanovate.jbj.core.tests.basic

import de.leanovate.jbj.core.tests.TestJbjExecutor
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

    "Simple POST Method test" in {
      // basic/002
      script(
        """<?php
          |echo $_POST['a']; ?>
          |""".stripMargin
      ).withPost("", "a=Hello+World").result must haveOutput(
        """Hello World""".stripMargin
      )
    }

    "GET and POST Method combined" in {
      // basic/003
      script(
        """<?php
          |error_reporting(0);
          |echo "post-a=({$_POST['a']}) get-b=({$_GET['b']}) get-c=({$_GET['c']})"?>""".stripMargin
      ).withPost("?b=Hello+Again+World&c=Hi+Mom", "a=Hello+World").result must haveOutput(
        """post-a=(Hello World) get-b=(Hello Again World) get-c=(Hi Mom)""".stripMargin
      )
    }

    "Two variables in POST data" in {
      // basic/004
      script(
        """<?php
          |error_reporting(0);
          |echo "{$_POST['a']} {$_POST['b']}" ?>""".stripMargin
      ).withPost("", "a=Hello+World&b=Hello+Again+World").result must haveOutput(
        """Hello World Hello Again World""".stripMargin
      )
    }

    "Three variables in POST data" in {
      // basic/005
      script(
        """<?php
          |error_reporting(0);
          |echo "{$_POST['a']} {$_POST['b']} {$_POST['c']}"?>""".stripMargin
      ).withPost("", "a=Hello+World&b=Hello+Again+World&c=1").result must haveOutput(
        """Hello World Hello Again World 1""".stripMargin
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
