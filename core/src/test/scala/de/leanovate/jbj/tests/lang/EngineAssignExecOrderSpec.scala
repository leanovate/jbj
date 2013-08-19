package de.leanovate.jbj.tests.lang

import org.specs2.mutable.SpecificationWithJUnit
import de.leanovate.jbj.tests.TestJbjExecutor

class EngineAssignExecOrderSpec extends SpecificationWithJUnit with TestJbjExecutor {
  "Engine assign execution order" should {
    "Evaluation order during assignments. 1" in {
      // lang/engine_assignExecutionOrder_001
      script(
        """<?php
          |
          |function f() {
          |	echo "in f()\n";
          |	return "name";
          |}
          |
          |function g() {
          |	echo "in g()\n";
          |	return "assigned value";
          |}
          |
          |
          |echo "\n\nOrder with local assignment:\n";
          |${f()} = g();
          |var_dump($name);
          |
          |echo "\n\nOrder with array assignment:\n";
          |$a[f()] = g();
          |var_dump($a);
          |
          |echo "\n\nOrder with object property assignment:\n";
          |$oa = new stdClass;
          |$oa->${f()} = g();
          |var_dump($oa);
          |
          |echo "\n\nOrder with nested object property assignment:\n";
          |$ob = new stdClass;
          |$ob->o1 = new stdClass;
          |$ob->o1->o2 = new stdClass;
          |$ob->o1->o2->${f()} = g();
          |var_dump($ob);
          |
          |echo "\n\nOrder with dim_list property assignment:\n";
          |$oc = new stdClass;
          |$oc->a[${f()}] = g();
          |var_dump($oc);
          |
          |
          |class C {
          |	public static $name = "original";
          |	public static $a = array();
          |	public static $string = "hello";
          |}
          |echo "\n\nOrder with static property assignment:\n";
          |C::${f()} = g();
          |var_dump(C::$name);
          |
          |echo "\n\nOrder with static array property assignment:\n";
          |C::$a[f()] = g();
          |var_dump(C::$a);
          |
          |echo "\n\nOrder with indexed string assignment:\n";
          |$string = "hello";
          |function getOffset() {
          |	echo "in getOffset()\n";
          |	return 0;
          |}
          |function newChar() {
          |	echo "in newChar()\n";
          |	return 'j';
          |}
          |$string[getOffset()] = newChar();
          |var_dump($string);
          |
          |echo "\n\nOrder with static string property assignment:\n";
          |C::$string[getOffset()] = newChar();
          |var_dump(C::$string);
          |
          |?>""".stripMargin
      ).result must haveOutput(
        """
          |
          |Order with local assignment:
          |in f()
          |in g()
          |string(14) "assigned value"
          |
          |
          |Order with array assignment:
          |in f()
          |in g()
          |array(1) {
          |  ["name"]=>
          |  string(14) "assigned value"
          |}
          |
          |
          |Order with object property assignment:
          |in f()
          |in g()
          |object(stdClass)#1 (1) {
          |  ["assigned value"]=>
          |  string(14) "assigned value"
          |}
          |
          |
          |Order with nested object property assignment:
          |in f()
          |in g()
          |object(stdClass)#2 (1) {
          |  ["o1"]=>
          |  object(stdClass)#3 (1) {
          |    ["o2"]=>
          |    object(stdClass)#4 (1) {
          |      ["assigned value"]=>
          |      string(14) "assigned value"
          |    }
          |  }
          |}
          |
          |
          |Order with dim_list property assignment:
          |in f()
          |in g()
          |object(stdClass)#5 (1) {
          |  ["a"]=>
          |  array(1) {
          |    ["assigned value"]=>
          |    string(14) "assigned value"
          |  }
          |}
          |
          |
          |Order with static property assignment:
          |in f()
          |in g()
          |string(14) "assigned value"
          |
          |
          |Order with static array property assignment:
          |in f()
          |in g()
          |array(1) {
          |  ["name"]=>
          |  string(14) "assigned value"
          |}
          |
          |
          |Order with indexed string assignment:
          |in getOffset()
          |in newChar()
          |string(5) "jello"
          |
          |
          |Order with static string property assignment:
          |in getOffset()
          |in newChar()
          |string(5) "jello"
          |""".stripMargin
      )
    }

    "Evaluation order during assignments. 3" in {
      // lang/engine_assignExecutionOrder_003
      script(
        """<?php
          |$b = "bb";
          |$a = "aa";
          |
          |function foo()
          |{
          |echo "Bad call\n";
          |}
          |
          |function baa()
          |{
          |echo "Good call\n";
          |}
          |
          |$bb = "baa";
          |
          |$aa = "foo";
          |
          |$c = ${$a=$b};
          |
          |$c();
          |
          |$a1 = array("dead","dead","dead");
          |$a2 = array("dead","dead","live");
          |$a3 = array("dead","dead","dead");
          |
          |$a = array($a1,$a2,$a3);
          |
          |function live()
          |{
          |echo "Good call\n";
          |}
          |
          |function dead()
          |{
          |echo "Bad call\n";
          |}
          |
          |$i = 0;
          |
          |$a[$i=1][++$i]();
          |
          |$a = -1;
          |
          |function foo1()
          |{
          |  global $a;
          |  return ++$a;
          |}
          |
          |$arr = array(array(0,0),0);
          |
          |$brr = array(0,0,array(0,0,0,5),0);
          |$crr = array(0,0,0,0,array(0,0,0,0,0,10),0,0);
          |
          |$arr[foo1()][foo1()] = $brr[foo1()][foo1()] +
          |                     $crr[foo1()][foo1()];
          |
          |$val = $arr[0][1];
          |echo "Expect 15 and get...$val\n";
          |
          |$x = array(array(0),0);
          |function mod($b)
          |{
          |global $x;
          |$x = $b;
          |return 0;
          |}
          |
          |$x1 = array(array(1),1);
          |$x2 = array(array(2),2);
          |$x3 = array(array(3),3);
          |$bx = array(10);
          |
          |$x[mod($x1)][mod($x2)] = $bx[mod($x3)];
          |
          |// expecting 10,3
          |
          |var_dump($x);
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """Good call
          |Good call
          |Expect 15 and get...15
          |array(2) {
          |  [0]=>
          |  array(1) {
          |    [0]=>
          |    int(10)
          |  }
          |  [1]=>
          |  int(3)
          |}
          |""".stripMargin
      )
    }
  }
}
