package de.leanovate.jbj.tests.lang

import de.leanovate.jbj.tests.TestJbjExecutor
import org.specs2.mutable.SpecificationWithJUnit

class Lang4Spec extends SpecificationWithJUnit with TestJbjExecutor {
  "Language test 4" should {
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
  }
}
