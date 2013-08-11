package de.leanovate.jbj.tests.basic

import de.leanovate.jbj.tests.TestJbjExecutor
import org.specs2.mutable.SpecificationWithJUnit

class Basic2Spec extends SpecificationWithJUnit with TestJbjExecutor {
  "Basic test 2" should {
    "Testing $argc and $argv handling (GET)" in {
      // basic/011
      script(
        """<?php
          |$argc = $_SERVER['argc'];
          |$argv = $_SERVER['argv'];
          |
          |for ($i=0; $i<$argc; $i++) {
          |	echo "$i: ".$argv[$i]."\n";
          |}
          |
          |?>""".stripMargin
      ).withGet("?ab+cd+ef+123+test").result must haveOutput(
        """0: ab
          |1: cd
          |2: ef
          |3: 123
          |4: test
          |""".stripMargin
      )
    }

    "Testing $argc and $argv handling (cli)" in {
      // basic/012
      script(
        """<?php
          |$argc = $_SERVER['argc'];
          |$argv = $_SERVER['argv'];
          |
          |for ($i=1; $i<$argc; $i++) {
          |	echo ($i-1).": ".$argv[$i]."\n";
          |}
          |
          |?>""".stripMargin
      ).withCommandLine("ab cd ef 123 test").result must haveOutput(
        """0: ab
          |1: cd
          |2: ef
          |3: 123
          |4: test
          |""".stripMargin
      )
    }

    "POST Method test and arrays" in {
      // basic/013
      script(
        """<?php
          |var_dump($_POST['a']);
          |?>""".stripMargin
      ).withPost("", "a[]=1").result must haveOutput(
        """array(1) {
          |  [0]=>
          |  string(1) "1"
          |}
          |""".stripMargin
      )
    }

    "POST Method test and arrays - 2" in {
      // basic/014
      script(
        """<?php
          |var_dump($_POST['a']);
          |?>""".stripMargin
      ).withPost("", "a[]=1&a[]=1").result must haveOutput(
        """array(2) {
          |  [0]=>
          |  string(1) "1"
          |  [1]=>
          |  string(1) "1"
          |}
          |""".stripMargin
      )
    }

    "POST Method test and arrays - 3" in {
      // basic/015
      script(
        """<?php
          |var_dump($_POST['a']);
          |?>""".stripMargin
      ).withPost("", "a[]=1&a[0]=5").result must haveOutput(
        """array(1) {
          |  [0]=>
          |  string(1) "5"
          |}
          |""".stripMargin
      )
    }

    "POST Method test and arrays - 4" in {
      // basic/016
      script(
        """<?php
          |var_dump($_POST['a']);
          |?>""".stripMargin
      ).withPost("", "a[a]=1&a[b]=3").result must haveOutput(
        """array(2) {
          |  ["a"]=>
          |  string(1) "1"
          |  ["b"]=>
          |  string(1) "3"
          |}
          |""".stripMargin
      )
    }

    "POST Method test and arrays - 5" in {
      // basic/017
      script(
        """<?php
          |var_dump($_POST['a']);
          |?>""".stripMargin
      ).withPost("", "a[]=1&a[a]=1&a[b]=3").result must haveOutput(
        """array(3) {
          |  [0]=>
          |  string(1) "1"
          |  ["a"]=>
          |  string(1) "1"
          |  ["b"]=>
          |  string(1) "3"
          |}
          |""".stripMargin
      )
    }

    "POST Method test and arrays - 6" in {
      // basic/018
      script(
        """<?php
          |var_dump($_POST['a']);
          |var_dump($_POST['b']);
          |?>""".stripMargin
      ).withPost("", "a[][]=1&a[][]=3&b[a][b][c]=1&b[a][b][d]=1").result must haveOutput(
        """array(2) {
          |  [0]=>
          |  array(1) {
          |    [0]=>
          |    string(1) "1"
          |  }
          |  [1]=>
          |  array(1) {
          |    [0]=>
          |    string(1) "3"
          |  }
          |}
          |array(1) {
          |  ["a"]=>
          |  array(1) {
          |    ["b"]=>
          |    array(2) {
          |      ["c"]=>
          |      string(1) "1"
          |      ["d"]=>
          |      string(1) "1"
          |    }
          |  }
          |}
          |""".stripMargin
      )
    }

    "POST Method test and arrays - 7" in {
      // basic/019
      script(
        """<?php
          |var_dump($_POST['a']);
          |?>""".stripMargin
      ).withPost("", "a[]=1&a[]]=3&a[[]=4").result must haveOutput(
        """array(3) {
          |  [0]=>
          |  string(1) "1"
          |  [1]=>
          |  string(1) "3"
          |  ["["]=>
          |  string(1) "4"
          |}
          |""".stripMargin
      )
    }

    "POST Method test and arrays - 8" in {
      // basic/020
      script(
        """<?php
          |var_dump($_POST['a']);
          |?>""".stripMargin
      ).withPost("", "a[a[]]=1&a[b[]]=3").result must haveOutput(
        """array(2) {
          |  ["a["]=>
          |  string(1) "1"
          |  ["b["]=>
          |  string(1) "3"
          |}
          |""".stripMargin
      )
    }
  }
}
