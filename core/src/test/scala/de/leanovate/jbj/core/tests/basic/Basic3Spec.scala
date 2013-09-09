/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                 (Tests based on PHP)   */

package de.leanovate.jbj.core.tests.basic

import org.specs2.mutable.SpecificationWithJUnit
import de.leanovate.jbj.core.tests.{TestCookieInfo, TestJbjExecutor}

class Basic3Spec extends SpecificationWithJUnit with TestJbjExecutor {
  "Basic 3 tests" should {
    "Bug #37276 (problems witch $_POST array)" in {
      // basic/021
      script(
        """<?php
          |var_dump($_FILES);
          |var_dump($_POST);
          |?>""".stripMargin
      ).withMultipartPost("/",
        "multipart/form-data; boundary=---------------------------20896060251896012921717172737",
        """-----------------------------20896060251896012921717172737
          |Content-Disposition: form-data; name="submitter"
          |
          |testname
          |-----------------------------20896060251896012921717172737
          |Content-Disposition: form-data; name="pics"; filename="bug37276.txt"
          |Content-Type: text/plain
          |
          |bug37276
          |
          |-----------------------------20896060251896012921717172737--
          | """.stripMargin.replace("\n", "\r\n")
      ).result must haveOutput(
        """array(1) {
          |  ["pics"]=>
          |  array(5) {
          |    ["name"]=>
          |    string(12) "bug37276.txt"
          |    ["type"]=>
          |    string(10) "text/plain"
          |    ["tmp_name"]=>
          |    string(14) "/tmp/something"
          |    ["error"]=>
          |    int(0)
          |    ["size"]=>
          |    int(10)
          |  }
          |}
          |array(1) {
          |  ["submitter"]=>
          |  string(8) "testname"
          |}
          |""".stripMargin
      )
    }

    "Cookies test#1" in {
      // basic022
      // we implicitly assume that the surrounding web container is able to parse cookies correctly
      script(
        """<?php
          |var_dump($_COOKIE);
          |?>""".stripMargin
      ).withGet("/", Seq(
        TestCookieInfo("cookie1", "val1  "),
        TestCookieInfo("cookie2", "val2 "),
        TestCookieInfo("cookie3", "val 3."),
        TestCookieInfo("cookie_4", " value 4 ;"),
        TestCookieInfo("cookie__5", "  value"),
        TestCookieInfo("cookie_6", "þæö"),
        TestCookieInfo("cookie_7", ""),
        TestCookieInfo("$cookie_8", ""),
        TestCookieInfo("cookie-9", "1"),
        TestCookieInfo("-_&_%_$cookie_10", "10")
      )).result must haveOutput(
        """array(10) {
          |  ["cookie1"]=>
          |  string(6) "val1  "
          |  ["cookie2"]=>
          |  string(5) "val2 "
          |  ["cookie3"]=>
          |  string(6) "val 3."
          |  ["cookie_4"]=>
          |  string(10) " value 4 ;"
          |  ["cookie__5"]=>
          |  string(7) "  value"
          |  ["cookie_6"]=>
          |  string(6) "þæö"
          |  ["cookie_7"]=>
          |  string(0) ""
          |  ["$cookie_8"]=>
          |  string(0) ""
          |  ["cookie-9"]=>
          |  string(1) "1"
          |  ["-_&_%_$cookie_10"]=>
          |  string(2) "10"
          |}
          |""".stripMargin
      )
    }

    "Cookies test#2" in {
      // basic023
      // we implicitly assume that the surrounding web container is able to parse cookies correctly
      script(
        """<?php
          |var_dump($_COOKIE);
          |?>""".stripMargin
      ).withGet("/", Seq(
        TestCookieInfo("c_o_o_k_i_e", "value"),
        TestCookieInfo("name", """"value","value",UEhQIQ=="""),
        TestCookieInfo("UEhQIQ", "=foo")
      )).result must haveOutput(
        """array(3) {
          |  ["c_o_o_k_i_e"]=>
          |  string(5) "value"
          |  ["name"]=>
          |  string(24) ""value","value",UEhQIQ=="
          |  ["UEhQIQ"]=>
          |  string(4) "=foo"
          |}
          |""".stripMargin
      )
    }

    "Test HTTP_RAW_POST_DATA creation" in {
      script(
        """<?php
          |var_dump($_POST, $HTTP_RAW_POST_DATA);
          |?>""".stripMargin
      ).withAlwaysPopulateRawPostData(true).
        withPost("/", "a=ABC&y=XYZ&c[]=1&c[]=2&c[a]=3").result must haveOutput(
        """array(3) {
          |  ["a"]=>
          |  string(3) "ABC"
          |  ["y"]=>
          |  string(3) "XYZ"
          |  ["c"]=>
          |  array(3) {
          |    [0]=>
          |    string(1) "1"
          |    [1]=>
          |    string(1) "2"
          |    ["a"]=>
          |    string(1) "3"
          |  }
          |}
          |string(30) "a=ABC&y=XYZ&c[]=1&c[]=2&c[a]=3"
          |""".stripMargin
      )
    }
  }
}
