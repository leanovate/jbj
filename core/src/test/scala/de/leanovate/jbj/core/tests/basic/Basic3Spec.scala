/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                 (Tests based on PHP)   */

package de.leanovate.jbj.core.tests.basic

import org.specs2.mutable.SpecificationWithJUnit
import de.leanovate.jbj.core.tests.TestJbjExecutor

class Basic3Spec extends SpecificationWithJUnit with TestJbjExecutor {
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

}
