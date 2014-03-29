package de.leanovate.jbj.pcre.tests

import org.specs2.mutable.SpecificationWithJUnit

class BasicSpec extends SpecificationWithJUnit with TestJbjExecutor {
  "PCRE" should {
    "abusing preg_match()" in {
      // ../php-src/ext/pcre/tests/001.phpt
      script(
        """<?php
          |
          |foreach (array('2006-05-13', '06-12-12', 'data: "12-Aug-87"') as $s) {
          |	var_dump(preg_match('~
          |		(?P<date>
          |		(?P<year>(\d{2})?\d\d) -
          |		(?P<month>(?:\d\d|[a-zA-Z]{2,3})) -
          |		(?P<day>[0-3]?\d))
          |	~x', $s, $m));
          |
          |	var_dump($m);
          |}
          |
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """int(1)
          |array(10) {
          |  [0]=>
          |  string(10) "2006-05-13"
          |  ["date"]=>
          |  string(10) "2006-05-13"
          |  [1]=>
          |  string(10) "2006-05-13"
          |  ["year"]=>
          |  string(4) "2006"
          |  [2]=>
          |  string(4) "2006"
          |  [3]=>
          |  string(2) "20"
          |  ["month"]=>
          |  string(2) "05"
          |  [4]=>
          |  string(2) "05"
          |  ["day"]=>
          |  string(2) "13"
          |  [5]=>
          |  string(2) "13"
          |}
          |int(1)
          |array(10) {
          |  [0]=>
          |  string(8) "06-12-12"
          |  ["date"]=>
          |  string(8) "06-12-12"
          |  [1]=>
          |  string(8) "06-12-12"
          |  ["year"]=>
          |  string(2) "06"
          |  [2]=>
          |  string(2) "06"
          |  [3]=>
          |  string(0) ""
          |  ["month"]=>
          |  string(2) "12"
          |  [4]=>
          |  string(2) "12"
          |  ["day"]=>
          |  string(2) "12"
          |  [5]=>
          |  string(2) "12"
          |}
          |int(1)
          |array(10) {
          |  [0]=>
          |  string(8) "12-Aug-8"
          |  ["date"]=>
          |  string(8) "12-Aug-8"
          |  [1]=>
          |  string(8) "12-Aug-8"
          |  ["year"]=>
          |  string(2) "12"
          |  [2]=>
          |  string(2) "12"
          |  [3]=>
          |  string(0) ""
          |  ["month"]=>
          |  string(3) "Aug"
          |  [4]=>
          |  string(3) "Aug"
          |  ["day"]=>
          |  string(1) "8"
          |  [5]=>
          |  string(1) "8"
          |}
          |""".stripMargin
      )
    }
  }
}
