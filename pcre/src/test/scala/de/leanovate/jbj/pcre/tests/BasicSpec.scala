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

    "preg_* with bogus vals" in {
      // ../php-src/ext/pcre/tests/002.phpt
      script(
        """<?php
          |
          |var_dump(preg_match());
          |var_dump(preg_match_all());
          |var_dump(preg_match_all('//', '', $dummy, 0xdead));
          |
          |var_dump(preg_quote());
          |var_dump(preg_quote(''));
          |
          |var_dump(preg_replace('/(.)/', '${1}${1', 'abc'));
          |var_dump(preg_replace('/.++\d*+[/', 'for ($', 'abc'));
          |var_dump(preg_replace('/(.)/e', 'for ($', 'abc'));
          |
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """
          |Warning: preg_match() expects at least 2 parameters, 0 given in /BasicSpec.inlinePhp on line 3
          |bool(false)
          |
          |Warning: preg_match_all() expects at least 2 parameters, 0 given in /BasicSpec.inlinePhp on line 4
          |bool(false)
          |
          |Warning: preg_match_all(): Invalid flags specified in /BasicSpec.inlinePhp on line 5
          |NULL
          |
          |Warning: preg_quote() expects at least 1 parameter, 0 given in /BasicSpec.inlinePhp on line 7
          |NULL
          |string(0) ""
          |string(12) "a${1b${1c${1"
          |
          |Warning: preg_replace(): Compilation failed: Unclosed character class at offset 7 in /BasicSpec.inlinePhp on line 11
          |NULL
          |
          |Deprecated: preg_replace(): The /e modifier is deprecated, use preg_replace_callback instead in /BasicSpec.inlinePhp on line 12
          |
          |Fatal error: preg_replace(): Failed evaluating code: for ($ in /BasicSpec.inlinePhp on line 12
          |""".stripMargin
      )
    }

    "abusing preg_match_all()" in {
      // ../php-src/ext/pcre/tests/003.phpt
      script(
        """<?php
          |
          |foreach (array(PREG_PATTERN_ORDER, PREG_SET_ORDER) as $flag) {
          |	var_dump(preg_match_all('~
          |		(?P<date>
          |		(?P<year>(\d{2})?\d\d) -
          |		(?P<month>(?:\d\d|[a-zA-Z]{2,3})) -
          |		(?P<day>[0-3]?\d))
          |		~x',
          |		'2006-05-13 e outra data: "12-Aug-37"', $m, $flag));
          |
          |	var_dump($m);
          |}
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """int(2)
          |array(10) {
          |  [0]=>
          |  array(2) {
          |    [0]=>
          |    string(10) "2006-05-13"
          |    [1]=>
          |    string(9) "12-Aug-37"
          |  }
          |  ["date"]=>
          |  array(2) {
          |    [0]=>
          |    string(10) "2006-05-13"
          |    [1]=>
          |    string(9) "12-Aug-37"
          |  }
          |  [1]=>
          |  array(2) {
          |    [0]=>
          |    string(10) "2006-05-13"
          |    [1]=>
          |    string(9) "12-Aug-37"
          |  }
          |  ["year"]=>
          |  array(2) {
          |    [0]=>
          |    string(4) "2006"
          |    [1]=>
          |    string(2) "12"
          |  }
          |  [2]=>
          |  array(2) {
          |    [0]=>
          |    string(4) "2006"
          |    [1]=>
          |    string(2) "12"
          |  }
          |  [3]=>
          |  array(2) {
          |    [0]=>
          |    string(2) "20"
          |    [1]=>
          |    string(0) ""
          |  }
          |  ["month"]=>
          |  array(2) {
          |    [0]=>
          |    string(2) "05"
          |    [1]=>
          |    string(3) "Aug"
          |  }
          |  [4]=>
          |  array(2) {
          |    [0]=>
          |    string(2) "05"
          |    [1]=>
          |    string(3) "Aug"
          |  }
          |  ["day"]=>
          |  array(2) {
          |    [0]=>
          |    string(2) "13"
          |    [1]=>
          |    string(2) "37"
          |  }
          |  [5]=>
          |  array(2) {
          |    [0]=>
          |    string(2) "13"
          |    [1]=>
          |    string(2) "37"
          |  }
          |}
          |int(2)
          |array(2) {
          |  [0]=>
          |  array(10) {
          |    [0]=>
          |    string(10) "2006-05-13"
          |    ["date"]=>
          |    string(10) "2006-05-13"
          |    [1]=>
          |    string(10) "2006-05-13"
          |    ["year"]=>
          |    string(4) "2006"
          |    [2]=>
          |    string(4) "2006"
          |    [3]=>
          |    string(2) "20"
          |    ["month"]=>
          |    string(2) "05"
          |    [4]=>
          |    string(2) "05"
          |    ["day"]=>
          |    string(2) "13"
          |    [5]=>
          |    string(2) "13"
          |  }
          |  [1]=>
          |  array(10) {
          |    [0]=>
          |    string(9) "12-Aug-37"
          |    ["date"]=>
          |    string(9) "12-Aug-37"
          |    [1]=>
          |    string(9) "12-Aug-37"
          |    ["year"]=>
          |    string(2) "12"
          |    [2]=>
          |    string(2) "12"
          |    [3]=>
          |    string(0) ""
          |    ["month"]=>
          |    string(3) "Aug"
          |    [4]=>
          |    string(3) "Aug"
          |    ["day"]=>
          |    string(2) "37"
          |    [5]=>
          |    string(2) "37"
          |  }
          |}
          |""".stripMargin
      )
    }
  }
}
