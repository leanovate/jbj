/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                 (Tests based on PHP)   */

package de.leanovate.jbj.core.tests.lang

import de.leanovate.jbj.core.tests.TestJbjExecutor
import org.specs2.mutable.SpecificationWithJUnit

class Lang4Spec extends SpecificationWithJUnit with TestJbjExecutor {
  "Language test 4" should {
    "$this in constructor test" in {
      // lang/030
      script(
        """<?php
          |class foo {
          |	function foo($name) {
          |     	$GLOBALS['List']= &$this;
          |     	$this->Name = $name;
          |		$GLOBALS['List']->echoName();
          |	}
          |
          |	function echoName() {
          |     	$GLOBALS['names'][]=$this->Name;
          |	}
          |}
          |
          |function &foo2(&$foo) {
          |	return $foo;
          |}
          |
          |
          |$bar1 =new foo('constructor');
          |$bar1->Name = 'outside';
          |$bar1->echoName();
          |$List->echoName();
          |
          |$bar1 =& foo2(new foo('constructor'));
          |$bar1->Name = 'outside';
          |$bar1->echoName();
          |
          |$List->echoName();
          |
          |print ($names==array('constructor','outside','outside','constructor','outside','outside')) ? 'success':'failure';
          |?>""".stripMargin
      ).result must haveOutput(
        """success""".stripMargin
      )
    }

    "Bug #16227 (Internal hash position bug on assignment)" in {
      // lang/031
      script(
        """<?php
          |// reported by php.net@alienbill.com
          |$arrayOuter = array("key1","key2");
          |$arrayInner = array("0","1");
          |
          |print "Correct - with inner loop reset.\n";
          |
          |while(list(,$o) = each($arrayOuter)){
          |	reset($arrayInner);
          |	while(list(,$i) = each($arrayInner)){
          |	    	print "inloop $i for $o\n";
          |	}
          |}
          |reset($arrayOuter);
          |reset($arrayInner);
          |
          |print "What happens without inner loop reset.\n";
          |
          |while(list(,$o) = each($arrayOuter)){
          |	while(list(,$i) = each($arrayInner)){
          |		print "inloop $i for $o\n";
          |	}
          |}
          |reset($arrayOuter);
          |reset($arrayInner);
          |
          |print "What happens without inner loop reset but copy.\n";
          |
          |while(list(,$o) = each($arrayOuter)){
          |	$placeholder = $arrayInner;
          |	while(list(,$i) = each($arrayInner)){
          |		print "inloop $i for $o\n";
          |	}
          |}
          |reset($arrayOuter);
          |reset($arrayInner);
          |
          |print "What happens with inner loop reset over copy.\n";
          |
          |while(list(,$o) = each($arrayOuter)){
          |	$placeholder = $arrayInner;
          |	while(list(,$i) = each($placeholder)){
          |		print "inloop $i for $o\n";
          |	}
          |}
          |reset($arrayOuter);
          |reset($arrayInner);
          |?>""".stripMargin
      ).result must haveOutput(
        """Correct - with inner loop reset.
          |inloop 0 for key1
          |inloop 1 for key1
          |inloop 0 for key2
          |inloop 1 for key2
          |What happens without inner loop reset.
          |inloop 0 for key1
          |inloop 1 for key1
          |What happens without inner loop reset but copy.
          |inloop 0 for key1
          |inloop 1 for key1
          |inloop 0 for key2
          |inloop 1 for key2
          |What happens with inner loop reset over copy.
          |inloop 0 for key1
          |inloop 1 for key1
          |inloop 0 for key2
          |inloop 1 for key2
          |""".stripMargin
      )
    }

    "Class method registration" in {
      // lang/032
      script(
        """<?php
          |class A {
          |	function foo() {}
          |}
          |
          |class B extends A {
          |	function foo() {}
          |}
          |
          |class C extends B {
          |	function foo() {}
          |}
          |
          |class D extends A {
          |}
          |
          |class F extends D {
          |	function foo() {}
          |}
          |
          |// Following class definition should fail, but cannot test
          |/*
          |class X {
          |	function foo() {}
          |	function foo() {}
          |}
          |*/
          |
          |echo "OK\n";
          |?>""".stripMargin
      ).result must haveOutput(
        """OK
          |""".stripMargin
      )
    }

    "Alternative syntaxes test" in {
      // lang/033
      script(
        """<?php
          |$a = 1;
          |
          |echo "If: ";
          |if ($a) echo 1; else echo 0;
          |if ($a):
          |	echo 1;
          |else:
          |	echo 0;
          |endif;
          |
          |echo "\nWhile: ";
          |while ($a<5) echo $a++;
          |while ($a<9):
          |	echo ++$a;
          |endwhile;
          |
          |echo "\nFor: ";
          |for($a=0;$a<5;$a++) echo $a;
          |for($a=0;$a<5;$a++):
          |	echo $a;
          |endfor;
          |
          |echo "\nSwitch: ";
          |switch ($a):
          |	case 0;
          |		echo 0;
          |		break;
          |	case 5:
          |		echo 1;
          |		break;
          |	default;
          |		echo 0;
          |		break;
          |endswitch;
          |?>""".stripMargin
      ).result must haveOutput (
        """If: 11
          |While: 12346789
          |For: 0123401234
          |Switch: 1""".stripMargin
      )
    }

    "ZE2: set_exception_handler()" in {
      // lang/035
      script(
        """<?php
          |class MyException extends Exception {
          |	function MyException($_error) {
          |		$this->error = $_error;
          |	}
          |
          |	function getException()
          |	{
          |		return $this->error;
          |	}
          |}
          |
          |function ThrowException()
          |{
          |	throw new MyException("'This is an exception!'");
          |}
          |
          |
          |try {
          |} catch (MyException $exception) {
          |	print "There shouldn't be an exception: " . $exception->getException();
          |	print "\n";
          |}
          |
          |try {
          |	ThrowException();
          |} catch (MyException $exception) {
          |	print "There was an exception: " . $exception->getException();
          |	print "\n";
          |}
          |?>""".stripMargin
      ).result must haveOutput(
        """There was an exception: 'This is an exception!'
          |""".stripMargin
      )
    }

    "Child public element should not override parent private element in parent methods" in {
      // lang/036
      script(
        """<?php
          |class par {
          |	private $id = "foo";
          |
          |	function displayMe()
          |	{
          |		print $this->id;
          |	}
          |};
          |
          |class chld extends par {
          |	public $id = "bar";
          |	function displayHim()
          |	{
          |		parent::displayMe();
          |	}
          |};
          |
          |
          |$obj = new chld();
          |$obj->displayHim();
          |?>""".stripMargin
      ).result must haveOutput(
        """foo""".stripMargin
      )
    }

    "'Static' binding for private variables" in {
      // lang/037
      script(
        """<?php
          |
          |class par {
          |	private $id="foo";
          |
          |	function displayMe()
          |	{
          |		$this->displayChild();
          |	}
          |};
          |
          |class chld extends par {
          |	private $id = "bar";
          |
          |	function displayChild()
          |	{
          |		print $this->id;
          |	}
          |};
          |
          |
          |$obj = new chld();
          |$obj->displayMe();
          |
          |?>""".stripMargin
      ).result must haveOutput(
        """bar""".stripMargin
      )
    }
  }
}
