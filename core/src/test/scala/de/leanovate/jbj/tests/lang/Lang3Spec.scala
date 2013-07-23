package de.leanovate.jbj.tests.lang

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.FreeSpec
import de.leanovate.jbj.tests.TestJbjExecutor
import org.scalatest.matchers.MustMatchers

@RunWith(classOf[JUnitRunner])
class Lang3Spec extends FreeSpec with TestJbjExecutor with MustMatchers {
  "Language test 3" - {
    "Mean recursion test" in {
      // func/025
      resultOf(
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
      ) must be(
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
      resultOf(
        """<?php echo "\"\t\\'" . '\n\\\'a\\\b\\' ?>"""
      ) must be(
        """"	\'\n\'a\\b\"""
      )
    }
  }

}
