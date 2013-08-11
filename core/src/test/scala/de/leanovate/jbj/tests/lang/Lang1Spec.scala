package de.leanovate.jbj.tests.lang

import de.leanovate.jbj.tests.TestJbjExecutor
import org.specs2.mutable.SpecificationWithJUnit

class Lang1Spec extends SpecificationWithJUnit with TestJbjExecutor {
  "Language test 1" should {
    "Simple If condition test" in {
      // lang/001
      script(
        """<?php $a=1; if($a>0) { echo "Yes"; } ?>"""
      ).result must haveOutput(
        """Yes"""
      )
    }

    "Simple While Loop Test" in {
      // lang/002
      script(
        """<?php
          |$a=1;
          |while ($a<10) {
          |	echo $a;
          |	$a++;
          |}
          |?>""".stripMargin
      ).result must haveOutput(
        """123456789"""
      )
    }
    "Simple Switch Test" in {
      // lang/003
      script(
        """<?php
          |$a=1;
          |switch($a) {
          |	case 0:
          |		echo "bad";
          |		break;
          |	case 1:
          |		echo "good";
          |		break;
          |	default:
          |		echo "bad";
          |		break;
          |}
          |?>""".stripMargin
      ).result must haveOutput(
        """good"""
      )
    }

    "Simple If/Else Test" in {
      // lang/004
      script(
        """<?php
          |$a=1;
          |if($a==0) {
          |	echo "bad";
          |} else {
          |	echo "good";
          |}
          |?>""".stripMargin
      ).result must haveOutput(
        """good"""
      )
    }

    "Simple If/ElseIf/Else Test" in {
      // lang/005
      script(
        """<?php
          |$a=1;
          |
          |if($a==0) {
          |	echo "bad";
          |} elseif($a==3) {
          |	echo "bad";
          |} else {
          |	echo "good";
          |}
          |?>""".stripMargin
      ).result must haveOutput(
        """good"""
      )
    }

    "Nested If/ElseIf/Else Test" in {
      // lang/006
      script(
        """<?php
          |$a=1;
          |$b=2;
          |
          |if($a==0) {
          |	echo "bad";
          |} elseif($a==3) {
          |	echo "bad";
          |} else {
          |	if($b==1) {
          |		echo "bad";
          |	} elseif($b==2) {
          |		echo "good";
          |	} else {
          |		echo "bad";
          |	}
          |}
          |?>""".stripMargin
      ).result must haveOutput(
        """good"""
      )
    }

    "Function call with global and static variables" in {
      // lang/007
      script(
        """<?php
          |error_reporting(0);
          |$a = 10;
          |
          |function Test()
          |{
          |	static $a=1;
          |	global $b;
          |	$c = 1;
          |	$b = 5;
          |	echo "$a $b ";
          |	$a++;
          |	$c++;
          |	echo "$a $c ";
          |}
          |
          |Test();
          |echo "$a $b $c ";
          |Test();
          |echo "$a $b $c ";
          |Test();
          |?>""".stripMargin
      ).result must haveOutput(
        """1 5 2 2 10 5  2 5 3 2 10 5  3 5 4 2 """
      )
    }

    "Testing recursive function" in {
      // lang/008
      script(
        """<?php
          |
          |function Test()
          |{
          |	static $a=1;
          |	echo "$a ";
          |	$a++;
          |	if($a<10): Test(); endif;
          |}
          |
          |Test();
          |
          |?>""".stripMargin
      ).result must haveOutput(
        """1 2 3 4 5 6 7 8 9 """.stripMargin
      )
    }
    "Testing function parameter passing" in {
      // lang/009
      script(
        """<?php
          |function test ($a,$b) {
          |	echo $a+$b;
          |}
          |test(1,2);
          |?>""".stripMargin
      ).result must haveOutput(
        """3"""
      )
    }
  }
}