package de.leanovate.jbj.core.tests.lang

import de.leanovate.jbj.core.tests.TestJbjExecutor
import org.specs2.mutable.SpecificationWithJUnit
import scala.io.Source

class Lang3Spec extends SpecificationWithJUnit with TestJbjExecutor {
  "Language test 3" should {
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
      // lang/021
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

    "Regression test" in {
      // lang/023
      script(
        """PHP Regression Test
          |
          |<?php
          |
          |include("023-1.inc");
          |
          |$wedding_timestamp = mktime(20,0,0,8,31,1997);
          |$time_left=$wedding_timestamp-time();
          |
          |if ($time_left>0) {
          |  $days = $time_left/(24*3600);
          |  $time_left -= $days*24*3600;
          |  $hours = $time_left/3600;
          |  $time_left -= $hours*3600;
          |  $minutes = $time_left/60;
          |  echo "Limor Ullmann is getting married on ".($wedding_date=date("l, F dS, Y",$wedding_timestamp)).",\nwhich is $days days, $hours hours and $minutes minutes from now.\n";
          |  echo "Her hashed wedding date is $wedding_date.\n";
          |} else {
          |  echo "Limor Ullmann is now Limor Baruch :I\n";
          |}
          |?>""".stripMargin
      ).result must haveOutput(
        """PHP Regression Test
          |
          |<html>
          |<head>
          |
          |*** Testing assignments and variable aliasing: ***<br>
          |This should read "blah": blah<br>
          |This should read "this is nifty": this is nifty<br>
          |*************************************************<br>
          |
          |*** Testing integer operators ***<br>
          |Correct result - 8:  8<br>
          |Correct result - 8:  8<br>
          |Correct result - 2:  2<br>
          |Correct result - -2:  -2<br>
          |Correct result - 15:  15<br>
          |Correct result - 15:  15<br>
          |Correct result - 2:  2<br>
          |Correct result - 3:  3<br>
          |*********************************<br>
          |
          |*** Testing real operators ***<br>
          |Correct result - 8:  8<br>
          |Correct result - 8:  8<br>
          |Correct result - 2:  2<br>
          |Correct result - -2:  -2<br>
          |Correct result - 15:  15<br>
          |Correct result - 15:  15<br>
          |Correct result - 2:  2<br>
          |Correct result - 3:  3<br>
          |*********************************<br>
          |
          |*** Testing if/elseif/else control ***<br>
          |
          |This  works<br>
          |this_still_works<br>
          |should_print<br>
          |
          |
          |*** Seriously nested if's test ***<br>
          |** spelling correction by kluzz **
          |Only two lines of text should follow:<br>
          |this should be displayed. should be:  $i=1, $j=0.  is:  $i=1, $j=0<br>
          |this is supposed to be displayed. should be:  $i=2, $j=4.  is:  $i=2, $j=4<br>
          |3 loop iterations should follow:<br>
          |2 4<br>
          |3 4<br>
          |4 4<br>
          |**********************************<br>
          |
          |*** C-style else-if's ***<br>
          |This should be displayed<br>
          |*************************<br>
          |
          |*** WHILE tests ***<br>
          |0 is smaller than 20<br>
          |1 is smaller than 20<br>
          |2 is smaller than 20<br>
          |3 is smaller than 20<br>
          |4 is smaller than 20<br>
          |5 is smaller than 20<br>
          |6 is smaller than 20<br>
          |7 is smaller than 20<br>
          |8 is smaller than 20<br>
          |9 is smaller than 20<br>
          |10 is smaller than 20<br>
          |11 is smaller than 20<br>
          |12 is smaller than 20<br>
          |13 is smaller than 20<br>
          |14 is smaller than 20<br>
          |15 is smaller than 20<br>
          |16 is smaller than 20<br>
          |17 is smaller than 20<br>
          |18 is smaller than 20<br>
          |19 is smaller than 20<br>
          |20 equals 20<br>
          |21 is greater than 20<br>
          |22 is greater than 20<br>
          |23 is greater than 20<br>
          |24 is greater than 20<br>
          |25 is greater than 20<br>
          |26 is greater than 20<br>
          |27 is greater than 20<br>
          |28 is greater than 20<br>
          |29 is greater than 20<br>
          |30 is greater than 20<br>
          |31 is greater than 20<br>
          |32 is greater than 20<br>
          |33 is greater than 20<br>
          |34 is greater than 20<br>
          |35 is greater than 20<br>
          |36 is greater than 20<br>
          |37 is greater than 20<br>
          |38 is greater than 20<br>
          |39 is greater than 20<br>
          |*******************<br>
          |
          |
          |*** Nested WHILEs ***<br>
          |Each array variable should be equal to the sum of its indices:<br>
          |${test00}[0] = 0<br>
          |${test00}[1] = 1<br>
          |${test00}[2] = 2<br>
          |${test01}[0] = 1<br>
          |${test01}[1] = 2<br>
          |${test01}[2] = 3<br>
          |${test02}[0] = 2<br>
          |${test02}[1] = 3<br>
          |${test02}[2] = 4<br>
          |${test10}[0] = 1<br>
          |${test10}[1] = 2<br>
          |${test10}[2] = 3<br>
          |${test11}[0] = 2<br>
          |${test11}[1] = 3<br>
          |${test11}[2] = 4<br>
          |${test12}[0] = 3<br>
          |${test12}[1] = 4<br>
          |${test12}[2] = 5<br>
          |${test20}[0] = 2<br>
          |${test20}[1] = 3<br>
          |${test20}[2] = 4<br>
          |${test21}[0] = 3<br>
          |${test21}[1] = 4<br>
          |${test21}[2] = 5<br>
          |${test22}[0] = 4<br>
          |${test22}[1] = 5<br>
          |${test22}[2] = 6<br>
          |*********************<br>
          |
          |*** hash test... ***<br>
          |commented out...
          |**************************<br>
          |
          |*** Hash resizing test ***<br>
          |ba<br>
          |baa<br>
          |baaa<br>
          |baaaa<br>
          |baaaaa<br>
          |baaaaaa<br>
          |baaaaaaa<br>
          |baaaaaaaa<br>
          |baaaaaaaaa<br>
          |baaaaaaaaaa<br>
          |ba<br>
          |10<br>
          |baa<br>
          |9<br>
          |baaa<br>
          |8<br>
          |baaaa<br>
          |7<br>
          |baaaaa<br>
          |6<br>
          |baaaaaa<br>
          |5<br>
          |baaaaaaa<br>
          |4<br>
          |baaaaaaaa<br>
          |3<br>
          |baaaaaaaaa<br>
          |2<br>
          |baaaaaaaaaa<br>
          |1<br>
          |**************************<br>
          |
          |
          |*** break/continue test ***<br>
          |$i should go from 0 to 2<br>
          |$j should go from 3 to 4, and $q should go from 3 to 4<br>
          |  $j=3<br>
          |    $q=3<br>
          |    $q=4<br>
          |  $j=4<br>
          |    $q=3<br>
          |    $q=4<br>
          |$j should go from 0 to 2<br>
          |  $j=0<br>
          |  $j=1<br>
          |  $j=2<br>
          |$k should go from 0 to 2<br>
          |    $k=0<br>
          |    $k=1<br>
          |    $k=2<br>
          |$i=0<br>
          |$j should go from 3 to 4, and $q should go from 3 to 4<br>
          |  $j=3<br>
          |    $q=3<br>
          |    $q=4<br>
          |  $j=4<br>
          |    $q=3<br>
          |    $q=4<br>
          |$j should go from 0 to 2<br>
          |  $j=0<br>
          |  $j=1<br>
          |  $j=2<br>
          |$k should go from 0 to 2<br>
          |    $k=0<br>
          |    $k=1<br>
          |    $k=2<br>
          |$i=1<br>
          |$j should go from 3 to 4, and $q should go from 3 to 4<br>
          |  $j=3<br>
          |    $q=3<br>
          |    $q=4<br>
          |  $j=4<br>
          |    $q=3<br>
          |    $q=4<br>
          |$j should go from 0 to 2<br>
          |  $j=0<br>
          |  $j=1<br>
          |  $j=2<br>
          |$k should go from 0 to 2<br>
          |    $k=0<br>
          |    $k=1<br>
          |    $k=2<br>
          |$i=2<br>
          |***********************<br>
          |
          |*** Nested file include test ***<br>
          |<html>
          |This is Finish.phtml.  This file is supposed to be included
          |from regression_test.phtml.  This is normal HTML.
          |and this is PHP code, 2+2=4
          |</html>
          |********************************<br>
          |
          |Tests completed.<br>
          |Limor Ullmann is now Limor Baruch :I
          |""".stripMargin
      )
    }

    "Looped regression test (may take a while)" in {
      // lang/024
      script(
        Source.fromInputStream(getClass.getResourceAsStream("024.php")).mkString("")
      ).result must haveOutput(
        Source.fromInputStream(getClass.getResourceAsStream("024.expected")).mkString("")
      )
    }

    "Mean recursion test" in {
      // lang/025
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
      // lang/026
      script(
        """<?php echo "\"\t\\'" . '\n\\\'a\\\b\\' ?>"""
      ).result must haveOutput(
        """"	\'\n\'a\\b\"""
      )
    }

    "Testing do-while loop" in {
      // lang/027
      script(
        """<?php
          |$i=3;
          |do {
          |	echo $i;
          |	$i--;
          |} while($i>0);
          |?>""".stripMargin
      ).result must haveOutput(
        """321""".stripMargin
      )
    }

    "Testing calling user-level functions from C" in {
      // lang/028
      script(
        Source.fromInputStream(getClass.getResourceAsStream("028.php")).mkString("")
      ).result must haveOutput(
        Source.fromInputStream(getClass.getResourceAsStream("028.expected")).mkString("")
      )
    }
  }
}
