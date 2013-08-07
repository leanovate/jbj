package de.leanovate.jbj.tests.lang

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.FreeSpec
import de.leanovate.jbj.tests.TestJbjExecutor
import org.scalatest.matchers.MustMatchers

@RunWith(classOf[JUnitRunner])
class Lang3Spec extends FreeSpec with TestJbjExecutor with MustMatchers {
  "Language test 3" - {
    "Switch test 1" in {
      // func/020
      script(
        """<?php
          |
          |$i="abc";
          |
          |for ($j=0; $j<10; $j++) {
          |switch (1) {
          |  case 1:
          |  	echo "In branch 1\n";
          |  	switch ($i) {
          |  		case "ab":
          |  			echo "This doesn't work... :(\n";
          |  			break;
          |  		case "abcd":
          |  			echo "This works!\n";
          |  			break;
          |  		case "blah":
          |  			echo "Hmmm, no worki\n";
          |  			break;
          |  		default:
          |  			echo "Inner default...\n";
          |  	}
          |  	for ($blah=0; $blah<200; $blah++) {
          |  	  if ($blah==100) {
          |  	    echo "blah=$blah\n";
          |  	  }
          |  	}
          |  	break;
          |  case 2:
          |  	echo "In branch 2\n";
          |  	break;
          |  case $i:
          |  	echo "In branch \$i\n";
          |  	break;
          |  case 4:
          |  	echo "In branch 4\n";
          |  	break;
          |  default:
          |  	echo "Hi, I'm default\n";
          |  	break;
          | }
          |}
          |?>""".stripMargin
      ).result must haveOutput(
        """In branch 1
          |Inner default...
          |blah=100
          |In branch 1
          |Inner default...
          |blah=100
          |In branch 1
          |Inner default...
          |blah=100
          |In branch 1
          |Inner default...
          |blah=100
          |In branch 1
          |Inner default...
          |blah=100
          |In branch 1
          |Inner default...
          |blah=100
          |In branch 1
          |Inner default...
          |blah=100
          |In branch 1
          |Inner default...
          |blah=100
          |In branch 1
          |Inner default...
          |blah=100
          |In branch 1
          |Inner default...
          |blah=100
          |""".stripMargin
      )
    }

    "Switch test 2" in {
      // func/021
      script(
        """<?php
          |
          |for ($i=0; $i<=5; $i++)
          |{
          |  echo "i=$i\n";
          |
          |  switch($i) {
          |    case 0:
          |      echo "In branch 0\n";
          |      break;
          |    case 1:
          |      echo "In branch 1\n";
          |      break;
          |    case 2:
          |      echo "In branch 2\n";
          |      break;
          |    case 3:
          |      echo "In branch 3\n";
          |      break 2;
          |    case 4:
          |      echo "In branch 4\n";
          |      break;
          |    default:
          |      echo "In default\n";
          |      break;
          |  }
          |}
          |echo "hi\n";
          |?>""".stripMargin
      ).result must haveOutput(
        """i=0
          |In branch 0
          |i=1
          |In branch 1
          |i=2
          |In branch 2
          |i=3
          |In branch 3
          |hi
          |""".stripMargin
      )
    }

    "Switch test 3" in {
      // lang/022
      script(
        """<?php
          |
          |function switchtest ($i, $j)
          |{
          |	switch ($i) {
          |		case 0:
          |				switch($j) {
          |					case 0:
          |						echo "zero";
          |						break;
          |					case 1:
          |						echo "one";
          |						break;
          |					default:
          |						echo $j;
          |						break;
          |				}
          |				echo "\n";
          |				break;
          |		default:
          |				echo "Default taken\n";
          |	}
          |}
          |for ($i=0; $i<3; $i++) {
          |  for ($k=0; $k<10; $k++) {
          |    switchtest (0,$k);
          |  }
          |}
          |?>""".stripMargin
      ).result must haveOutput(
        """zero
          |one
          |2
          |3
          |4
          |5
          |6
          |7
          |8
          |9
          |zero
          |one
          |2
          |3
          |4
          |5
          |6
          |7
          |8
          |9
          |zero
          |one
          |2
          |3
          |4
          |5
          |6
          |7
          |8
          |9
          |""".stripMargin
      )
    }

    "Mean recursion test" in {
      // func/025
      script(
        """<?php
          |function RekTest ($nr) {
          |	echo " $nr ";
          |	$j=$nr+1;
          |	while ($j < 10) {
          |	  echo " a ";
          |	  RekTest($j);
          |	  $j++;
          |	  echo " b $j ";
          |	}
          |	echo "\n";
          |}
          |
          |RekTest(0);
          |?>""".stripMargin
      ).result must haveOutput(
        " 0  a  1  a  2  a  3  a  4  a  5  a  6  a  7  a  8  a  9 \n b 10 \n b 9  a  9 \n b 10 \n" +
          " b 8  a  8  a  9 \n b 10 \n b 9  a  9 \n b 10 \n b 7  a  7  a  8  a  9 \n b 10 \n b 9  a  9 \n b 10 \n" +
          " b 8  a  8  a  9 \n b 10 \n b 9  a  9 \n b 10 \n b 6  a  6  a  7  a  8  a  9 \n b 10 \n b 9  a  9 \n" +
          " b 10 \n b 8  a  8  a  9 \n b 10 \n b 9  a  9 \n b 10 \n b 7  a  7  a  8  a  9 \n b 10 \n b 9  a  9 \n" +
          " b 10 \n b 8  a  8  a  9 \n b 10 \n b 9  a  9 \n b 10 \n b 5  a  5  a  6  a  7  a  8  a  9 \n b 10 \n" +
          " b 9  a  9 \n b 10 \n b 8  a  8  a  9 \n b 10 \n b 9  a  9 \n b 10 \n b 7  a  7  a  8  a  9 \n b 10 \n" +
          " b 9  a  9 \n b 10 \n b 8  a  8  a  9 \n b 10 \n b 9  a  9 \n b 10 \n b 6  a  6  a  7  a  8  a  9 \n" +
          " b 10 \n b 9  a  9 \n b 10 \n b 8  a  8  a  9 \n b 10 \n b 9  a  9 \n b 10 \n b 7  a  7  a  8  a  9 \n" +
          " b 10 \n b 9  a  9 \n b 10 \n b 8  a  8  a  9 \n b 10 \n b 9  a  9 \n b 10 \n" +
          " b 4  a  4  a  5  a  6  a  7  a  8  a  9 \n b 10 \n b 9  a  9 \n b 10 \n b 8  a  8  a  9 \n b 10 \n" +
          " b 9  a  9 \n b 10 \n b 7  a  7  a  8  a  9 \n b 10 \n b 9  a  9 \n b 10 \n b 8  a  8  a  9 \n b 10 \n" +
          " b 9  a  9 \n b 10 \n b 6  a  6  a  7  a  8  a  9 \n b 10 \n b 9  a  9 \n b 10 \n b 8  a  8  a  9 \n" +
          " b 10 \n b 9  a  9 \n b 10 \n b 7  a  7  a  8  a  9 \n b 10 \n b 9  a  9 \n b 10 \n b 8  a  8  a  9 \n" +
          " b 10 \n b 9  a  9 \n b 10 \n b 5  a  5  a  6  a  7  a  8  a  9 \n b 10 \n b 9  a  9 \n b 10 \n" +
          " b 8  a  8  a  9 \n b 10 \n b 9  a  9 \n b 10 \n b 7  a  7  a  8  a  9 \n b 10 \n b 9  a  9 \n b 10 \n" +
          " b 8  a  8  a  9 \n b 10 \n b 9  a  9 \n b 10 \n b 6  a  6  a  7  a  8  a  9 \n b 10 \n b 9  a  9 \n" +
          " b 10 \n b 8  a  8  a  9 \n b 10 \n b 9  a  9 \n b 10 \n b 7  a  7  a  8  a  9 \n b 10 \n b 9  a  9 \n" +
          " b 10 \n b 8  a  8  a  9 \n b 10 \n b 9  a  9 \n b 10 \n b 3  a  3  a  4  a  5  a  6  a  7  a  8  a  9 \n" +
          " b 10 \n b 9  a  9 \n b 10 \n b 8  a  8  a  9 \n b 10 \n b 9  a  9 \n b 10 \n b 7  a  7  a  8  a  9 \n" +
          " b 10 \n b 9  a  9 \n b 10 \n b 8  a  8  a  9 \n b 10 \n b 9  a  9 \n b 10 \n b 6  a  6  a  7  a  8  a  9 \n" +
          " b 10 \n b 9  a  9 \n b 10 \n b 8  a  8  a  9 \n b 10 \n b 9  a  9 \n b 10 \n b 7  a  7  a  8  a  9 \n" +
          " b 10 \n b 9  a  9 \n b 10 \n b 8  a  8  a  9 \n b 10 \n b 9  a  9 \n b 10 \n" +
          " b 5  a  5  a  6  a  7  a  8  a  9 \n b 10 \n b 9  a  9 \n b 10 \n b 8  a  8  a  9 \n b 10 \n b 9  a  9 \n" +
          " b 10 \n b 7  a  7  a  8  a  9 \n b 10 \n b 9  a  9 \n b 10 \n b 8  a  8  a  9 \n b 10 \n b 9  a  9 \n b 10 \n" +
          " b 6  a  6  a  7  a  8  a  9 \n b 10 \n b 9  a  9 \n b 10 \n b 8  a  8  a  9 \n b 10 \n b 9  a  9 \n" +
          " b 10 \n b 7  a  7  a  8  a  9 \n b 10 \n b 9  a  9 \n b 10 \n b 8  a  8  a  9 \n b 10 \n b 9  a  9 \n" +
          " b 10 \n b 4  a  4  a  5  a  6  a  7  a  8  a  9 \n b 10 \n b 9  a  9 \n b 10 \n b 8  a  8  a  9 \n" +
          " b 10 \n b 9  a  9 \n b 10 \n b 7  a  7  a  8  a  9 \n b 10 \n b 9  a  9 \n b 10 \n b 8  a  8  a  9 \n" +
          " b 10 \n b 9  a  9 \n b 10 \n b 6  a  6  a  7  a  8  a  9 \n b 10 \n b 9  a  9 \n b 10 \n b 8  a  8  a  9 \n" +
          " b 10 \n b 9  a  9 \n b 10 \n b 7  a  7  a  8  a  9 \n b 10 \n b 9  a  9 \n b 10 \n b 8  a  8  a  9 \n" +
          " b 10 \n b 9  a  9 \n b 10 \n b 5  a  5  a  6  a  7  a  8  a  9 \n b 10 \n b 9  a  9 \n b 10 \n" +
          " b 8  a  8  a  9 \n b 10 \n b 9  a  9 \n b 10 \n b 7  a  7  a  8  a  9 \n b 10 \n b 9  a  9 \n b 10 \n" +
          " b 8  a  8  a  9 \n b 10 \n b 9  a  9 \n b 10 \n b 6  a  6  a  7  a  8  a  9 \n b 10 \n b 9  a  9 \n" +
          " b 10 \n b 8  a  8  a  9 \n b 10 \n b 9  a  9 \n b 10 \n b 7  a  7  a  8  a  9 \n b 10 \n b 9  a  9 \n" +
          " b 10 \n b 8  a  8  a  9 \n b 10 \n b 9  a  9 \n b 10 \n" +
          " b 2  a  2  a  3  a  4  a  5  a  6  a  7  a  8  a  9 \n b 10 \n b 9  a  9 \n b 10 \n b 8  a  8  a  9 \n" +
          " b 10 \n b 9  a  9 \n b 10 \n b 7  a  7  a  8  a  9 \n b 10 \n b 9  a  9 \n b 10 \n b 8  a  8  a  9 \n" +
          " b 10 \n b 9  a  9 \n b 10 \n b 6  a  6  a  7  a  8  a  9 \n b 10 \n b 9  a  9 \n b 10 \n" +
          " b 8  a  8  a  9 \n b 10 \n b 9  a  9 \n b 10 \n b 7  a  7  a  8  a  9 \n b 10 \n b 9  a  9 \n b 10 \n" +
          " b 8  a  8  a  9 \n b 10 \n b 9  a  9 \n b 10 \n b 5  a  5  a  6  a  7  a  8  a  9 \n b 10 \n b 9  a  9 \n" +
          " b 10 \n b 8  a  8  a  9 \n b 10 \n b 9  a  9 \n b 10 \n b 7  a  7  a  8  a  9 \n b 10 \n b 9  a  9 \n" +
          " b 10 \n b 8  a  8  a  9 \n b 10 \n b 9  a  9 \n b 10 \n b 6  a  6  a  7  a  8  a  9 \n b 10 \n" +
          " b 9  a  9 \n b 10 \n b 8  a  8  a  9 \n b 10 \n b 9  a  9 \n b 10 \n b 7  a  7  a  8  a  9 \n b 10 \n" +
          " b 9  a  9 \n b 10 \n b 8  a  8  a  9 \n b 10 \n b 9  a  9 \n b 10 \n" +
          " b 4  a  4  a  5  a  6  a  7  a  8  a  9 \n b 10 \n b 9  a  9 \n b 10 \n b 8  a  8  a  9 \n b 10 \n" +
          " b 9  a  9 \n b 10 \n b 7  a  7  a  8  a  9 \n b 10 \n b 9  a  9 \n b 10 \n b 8  a  8  a  9 \n b 10 \n" +
          " b 9  a  9 \n b 10 \n b 6  a  6  a  7  a  8  a  9 \n b 10 \n b 9  a  9 \n b 10 \n b 8  a  8  a  9 \n" +
          " b 10 \n b 9  a  9 \n b 10 \n b 7  a  7  a  8  a  9 \n b 10 \n b 9  a  9 \n b 10 \n b 8  a  8  a  9 \n" +
          " b 10 \n b 9  a  9 \n b 10 \n b 5  a  5  a  6  a  7  a  8  a  9 \n b 10 \n b 9  a  9 \n b 10 \n" +
          " b 8  a  8  a  9 \n b 10 \n b 9  a  9 \n b 10 \n b 7  a  7  a  8  a  9 \n b 10 \n b 9  a  9 \n b 10 \n" +
          " b 8  a  8  a  9 \n b 10 \n b 9  a  9 \n b 10 \n b 6  a  6  a  7  a  8  a  9 \n b 10 \n b 9  a  9 \n" +
          " b 10 \n b 8  a  8  a  9 \n b 10 \n b 9  a  9 \n b 10 \n b 7  a  7  a  8  a  9 \n b 10 \n b 9  a  9 \n" +
          " b 10 \n b 8  a  8  a  9 \n b 10 \n b 9  a  9 \n b 10 \n b 3  a  3  a  4  a  5  a  6  a  7  a  8  a  9 \n" +
          " b 10 \n b 9  a  9 \n b 10 \n b 8  a  8  a  9 \n b 10 \n b 9  a  9 \n b 10 \n b 7  a  7  a  8  a  9 \n" +
          " b 10 \n b 9  a  9 \n b 10 \n b 8  a  8  a  9 \n b 10 \n b 9  a  9 \n b 10 \n" +
          " b 6  a  6  a  7  a  8  a  9 \n b 10 \n b 9  a  9 \n b 10 \n b 8  a  8  a  9 \n b 10 \n b 9  a  9 \n" +
          " b 10 \n b 7  a  7  a  8  a  9 \n b 10 \n b 9  a  9 \n b 10 \n b 8  a  8  a  9 \n b 10 \n b 9  a  9 \n" +
          " b 10 \n b 5  a  5  a  6  a  7  a  8  a  9 \n b 10 \n b 9  a  9 \n b 10 \n b 8  a  8  a  9 \n b 10 \n" +
          " b 9  a  9 \n b 10 \n b 7  a  7  a  8  a  9 \n b 10 \n b 9  a  9 \n b 10 \n b 8  a  8  a  9 \n b 10 \n" +
          " b 9  a  9 \n b 10 \n b 6  a  6  a  7  a  8  a  9 \n b 10 \n b 9  a  9 \n b 10 \n b 8  a  8  a  9 \n" +
          " b 10 \n b 9  a  9 \n b 10 \n b 7  a  7  a  8  a  9 \n b 10 \n b 9  a  9 \n b 10 \n b 8  a  8  a  9 \n" +
          " b 10 \n b 9  a  9 \n b 10 \n b 4  a  4  a  5  a  6  a  7  a  8  a  9 \n b 10 \n b 9  a  9 \n b 10 \n" +
          " b 8  a  8  a  9 \n b 10 \n b 9  a  9 \n b 10 \n b 7  a  7  a  8  a  9 \n b 10 \n b 9  a  9 \n b 10 \n" +
          " b 8  a  8  a  9 \n b 10 \n b 9  a  9 \n b 10 \n b 6  a  6  a  7  a  8  a  9 \n b 10 \n b 9  a  9 \n" +
          " b 10 \n b 8  a  8  a  9 \n b 10 \n b 9  a  9 \n b 10 \n b 7  a  7  a  8  a  9 \n b 10 \n b 9  a  9 \n" +
          " b 10 \n b 8  a  8  a  9 \n b 10 \n b 9  a  9 \n b 10 \n b 5  a  5  a  6  a  7  a  8  a  9 \n b 10 \n" +
          " b 9  a  9 \n b 10 \n b 8  a  8  a  9 \n b 10 \n b 9  a  9 \n b 10 \n b 7  a  7  a  8  a  9 \n b 10 \n" +
          " b 9  a  9 \n b 10 \n b 8  a  8  a  9 \n b 10 \n b 9  a  9 \n b 10 \n b 6  a  6  a  7  a  8  a  9 \n" +
          " b 10 \n b 9  a  9 \n b 10 \n b 8  a  8  a  9 \n b 10 \n b 9  a  9 \n b 10 \n b 7  a  7  a  8  a  9 \n" +
          " b 10 \n b 9  a  9 \n b 10 \n b 8  a  8  a  9 \n b 10 \n b 9  a  9 \n b 10 \n"
      )
    }

    "Testing string scanner confirmance" in {
      // func/026
      script(
        """<?php echo "\"\t\\'" . '\n\\\'a\\\b\\' ?>"""
      ).result must haveOutput(
        """"	\'\n\'a\\b\"""
      )
    }
  }

}
