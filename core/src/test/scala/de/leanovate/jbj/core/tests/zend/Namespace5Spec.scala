package de.leanovate.jbj.core.tests.zend

import org.specs2.mutable.SpecificationWithJUnit
import de.leanovate.jbj.core.tests.TestJbjExecutor

class Namespace5Spec extends SpecificationWithJUnit with TestJbjExecutor {
  "Namespace test 040-049" should {
    "040: Constant declaration and usage in namespace" in {
      // Zend/tests/ns_040.phpt
      script(
        """<?php
          |namespace X;
          |use X as Y;
          |const A = "ok\n";
          |const B = A;
          |function f1($x=A) {
          |	echo $x;
          |}
          |function f2($x=\X\A) {
          |	echo $x;
          |}
          |function f3($x=Y\A) {
          |	echo $x;
          |}
          |function f4($x=\X\A) {
          |	echo $x;
          |}
          |function f5($x=B) {
          |	echo $x;
          |}
          |function f6($x=array(A)) {
          |	echo $x[0];
          |}
          |function f7($x=array("aaa"=>A)) {
          |	echo $x["aaa"];
          |}
          |function f8($x=array(A=>"aaa\n")) {
          |	echo $x["ok\n"];
          |}
          |echo A;
          |echo \X\A;
          |echo Y\A;
          |echo \X\A;
          |f1();
          |f2();
          |f3();
          |f4();
          |echo B;
          |f5();
          |f6();
          |f7();
          |f8();
          |""".stripMargin
      ).result must haveOutput(
        """ok
          |ok
          |ok
          |ok
          |ok
          |ok
          |ok
          |ok
          |ok
          |ok
          |ok
          |ok
          |aaa
          |""".stripMargin
      )
    }

    "041: Constants in namespace" in {
      // Zend/tests/ns_041.phpt
      script(
        """<?php
          |namespace test\ns1;
          |
          |const FOO = "ok\n";
          |
          |echo(FOO);
          |echo(\test\ns1\FOO);
          |echo(\test\ns1\FOO);
          |echo(BAR);
          |
          |const BAR = "ok\n";
          |
          |""".stripMargin
      ).result must haveOutput(
        """ok
          |ok
          |ok
          |
          |Notice: Use of undefined constant BAR - assumed 'BAR' in /zend/Namespace5Spec.inlinePhp on line 9
          |BAR""".stripMargin
      )
    }

    "042: Import in namespace and constants" in {
      // Zend/tests/ns_042.phpt
      script(
        """<?php
          |namespace test\ns1;
          |
          |const FOO = "ok\n";
          |
          |use test\ns1 as ns2;
          |use test as ns3;
          |
          |echo FOO;
          |echo \test\ns1\FOO;
          |echo \test\ns1\FOO;
          |echo ns2\FOO;
          |echo ns3\ns1\FOO;
          |""".stripMargin
      ).result must haveOutput(
        """ok
          |ok
          |ok
          |ok
          |ok
          |""".stripMargin
      )
    }

    "043: Name conflict and constants (ns name)" in {
      // Zend/tests/ns_043.phpt
      script(
        """<?php
          |namespace test\ns1;
          |
          |const INI_ALL = 0;
          |
          |var_dump(INI_ALL);
          |""".stripMargin
      ).result must haveOutput(
        """int(0)
          |""".stripMargin
      )
    }
  }
}
