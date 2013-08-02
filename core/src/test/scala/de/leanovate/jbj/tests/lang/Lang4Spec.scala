package de.leanovate.jbj.tests.lang

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.FreeSpec
import de.leanovate.jbj.tests.TestJbjExecutor
import org.scalatest.matchers.MustMatchers

@RunWith(classOf[JUnitRunner])
class Lang4Spec extends FreeSpec with TestJbjExecutor with MustMatchers {
  "Language test 4" - {
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
      ) must haveOutput (
        """If: 11
          |While: 12346789
          |For: 0123401234
          |Switch: 1""".stripMargin
      )
    }
  }

}
