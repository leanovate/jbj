/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                 (Tests based on PHP)   */

package de.leanovate.jbj.core.tests.zend

import org.specs2.mutable.SpecificationWithJUnit
import de.leanovate.jbj.core.tests.TestJbjExecutor

class Namespace9Spec extends SpecificationWithJUnit with TestJbjExecutor {
  "Namespace tests 080-" should {
    "080: bracketed namespaces and __HALT_COMPILER();" in {
      // ../php-src/Zend/tests/ns_080.phpt
      script(
        """<?php
          |namespace foo {
          |echo "hi\n";
          |}
          |__HALT_COMPILER();
          |namespace unprocessed {
          |echo "should not echo\n";
          |}
          |?>
          |===DONE===
          |""".stripMargin
      ).result must haveOutput(
        """hi
          |""".stripMargin
      )
    }

    "081: bracketed namespace with nested unbracketed namespace" in {
      // ../php-src/Zend/tests/ns_081.phpt
      script(
        """<?php
          |namespace foo {
          |use \foo;
          |class bar {
          |	function __construct() {echo __METHOD__,"\n";}
          |}
          |new foo;
          |new bar;
          |namespace oops;
          |class foo {
          |	function __construct() {echo __METHOD__,"\n";}
          |}
          |use foo\bar as foo1;
          |new foo1;
          |new foo;
          |}
          |?>
          |===DONE===
          |""".stripMargin
      ).result must haveOutput(
        """
          |Fatal error: Cannot mix bracketed namespace declarations with unbracketed namespace declarations in /zend/Namespace9Spec.inlinePhp on line 9
          |""".stripMargin
      )
    }

    "082: bracketed namespace with closing tag" in {
      // ../php-src/Zend/tests/ns_082.phpt
      script(
        """<?php
          |namespace foo {
          |}
          |namespace ok {
          |echo "ok\n";
          |}
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """ok
          |""".stripMargin
      )
    }

    "083: bracketed namespace with junk before the ns declaration" in {
      // ../php-src/Zend/tests/ns_083.phpt
      script(
        """<?php
          |$a = 'oops';
          |echo $a;
          |namespace foo {
          |}
          |namespace ok {
          |echo "ok\n";
          |}
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """
          |Fatal error: Namespace declaration statement has to be the very first statement in the script in /zend/Namespace9Spec.inlinePhp on line 2
          |""".stripMargin
      )
    }

    "084: unbracketed namespace with nested bracketed namespace" in {
      // ../php-src/Zend/tests/ns_084.phpt
      script(
        """<?php
          |namespace foo;
          |use \foo;
          |class bar {
          |	function __construct() {echo __METHOD__,"\n";}
          |}
          |new foo;
          |new bar;
          |namespace oops {
          |class foo {
          |	function __construct() {echo __METHOD__,"\n";}
          |}
          |use foo\bar as foo1;
          |new foo1;
          |new foo;
          |}
          |?>
          |===DONE===
          |""".stripMargin
      ).result must haveOutput(
        """
          |Fatal error: Cannot mix bracketed namespace declarations with unbracketed namespace declarations in /zend/Namespace9Spec.inlinePhp on line 9
          |""".stripMargin
      )
    }

    "085: bracketed namespace" in {
      // ../php-src/Zend/tests/ns_085.phpt
      script(
        """<?php
          |namespace foo {
          |use \foo;
          |class bar {
          |	function __construct() {echo __METHOD__,"\n";}
          |}
          |new foo;
          |new bar;
          |}
          |namespace {
          |class foo {
          |	function __construct() {echo __METHOD__,"\n";}
          |}
          |use foo\bar as foo1;
          |new foo1;
          |new foo;
          |echo "===DONE===\n";
          |}
          |""".stripMargin
      ).result must haveOutput(
        """foo::__construct
          |foo\bar::__construct
          |foo\bar::__construct
          |foo::__construct
          |===DONE===
          |""".stripMargin
      )
    }

    "086: bracketed namespace with encoding" in {
      // ../php-src/Zend/tests/ns_086.phpt
      script(
        """<?php
          |declare(encoding='utf-8');
          |namespace foo {
          |use \foo;
          |class bar {
          |	function __construct() {echo __METHOD__,"\n";}
          |}
          |new foo;
          |new bar;
          |}
          |namespace {
          |class foo {
          |	function __construct() {echo __METHOD__,"\n";}
          |}
          |use foo\bar as foo1;
          |new foo1;
          |new foo;
          |echo "===DONE===\n";
          |}
          |""".stripMargin
      ).result must haveOutput(
        """foo::__construct
          |foo\bar::__construct
          |foo\bar::__construct
          |foo::__construct
          |===DONE===
          |""".stripMargin
      )
    }
  }
}
