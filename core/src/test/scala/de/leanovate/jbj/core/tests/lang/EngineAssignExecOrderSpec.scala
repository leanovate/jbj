/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                 (Tests based on PHP)   */

package de.leanovate.jbj.core.tests.lang

import org.specs2.mutable.SpecificationWithJUnit
import de.leanovate.jbj.core.tests.TestJbjExecutor

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

    "Evaluation order during assignments. 2" in {
      // lang/engine_assignExecutionOrder_002
      script(
        """<?php
          |
          |// simple case with missing element
          |$f = array("hello","item2","bye");
          |list($a,,$b) = $f;
          |echo "A=$a B=$b\n";
          |
          |
          |// Warning: Cannot use a scalar value as an array in %s on line %d
          |$c[$c=1] = 1;
          |
          |// i++ evaluated first, so $d[0] is 10
          |$d = array(0,10);
          |$i = 0;
          |$d[$i++] = $i*10;
          |// expected array is 10,10
          |var_dump($d);
          |
          |// the f++++ makes f into 2, so $e 0 and 1 should both be 30
          |$e = array(0,0);
          |$f = 0;
          |$g1 = array(10,10);
          |$g2 = array(20,20);
          |$g3 = array(30,30);
          |$g = array($g1,$g2,$g3);
          |list($e[$f++],$e[$f++]) = $g[$f];
          |// expect 30,30
          |var_dump($e);
          |
          |
          |$i1 = array(1,2);
          |$i2 = array(10,20);
          |$i3 = array(100,200);
          |$i4 = array(array(1000,2000),3000);
          |$i = array($i1,$i2,$i3,$i4);
          |$j = array(0,0,0);
          |$h = 0;
          |// a list of lists
          |list(list($j[$h++],$j[$h++]),$j[$h++]) = $i[$h];
          |var_dump($j);
          |
          |
          |// list of lists with just variable assignments - expect 100,200,300
          |$k3 = array(100,200);
          |$k = array($k3,300);
          |list(list($l,$m),$n) = $k;
          |echo "L=$l M=$m N=$n\n";
          |
          |
          |// expect $x and $y to be null - this fails on php.net 5.2.1 (invalid opcode) - fixed in 5.2.3
          |list($o,$p) = 20;
          |echo "O=$o and P=$p\n";
          |
          |
          |// list of lists with blanks and nulls expect 10 20 40 50 60 70 80
          |$q1 = array(10,20,30,40);
          |$q2 = array(50,60);
          |$q3 = array($q1,$q2,null,70);
          |$q4 = array($q3,null,80);
          |
          |list(list(list($r,$s,,$t),list($u,$v),,$w),,$x) = $q4;
          |echo "$r $s $t $u $v $w $x\n";
          |
          |
          |// expect y and z to be undefined
          |list($y,$z) = array();
          |echo "Y=$y,Z=$z\n";
          |
          |// expect h to be defined and be 10
          |list($aa,$bb) = array(10);
          |echo "AA=$aa\n";
          |
          |// expect cc and dd to be 10 and 30
          |list($cc,,$dd) = array(10,20,30,40);
          |echo "CC=$cc DD=$dd\n";
          |
          |// expect the inner array to be defined
          |$ee = array("original array");
          |function f() {
          |  global $ee;
          |  $ee = array("array created in f()");
          |  return 1;
          |}
          |$ee["array entry created after f()"][f()] = "hello";
          |print_r($ee);
          |
          |?>""".stripMargin
      ).result must haveOutput(
        """A=hello B=bye
          |
          |Warning: Cannot use a scalar value as an array in /lang/EngineAssignExecOrderSpec.inlinePhp on line 10
          |array(2) {
          |  [0]=>
          |  int(10)
          |  [1]=>
          |  int(10)
          |}
          |array(2) {
          |  [0]=>
          |  int(30)
          |  [1]=>
          |  int(30)
          |}
          |array(3) {
          |  [0]=>
          |  int(1000)
          |  [1]=>
          |  int(2000)
          |  [2]=>
          |  int(3000)
          |}
          |L=100 M=200 N=300
          |O= and P=
          |10 20 40 50 60 70 80
          |
          |Notice: Undefined offset: 1 in /lang/EngineAssignExecOrderSpec.inlinePhp on line 66
          |
          |Notice: Undefined offset: 0 in /lang/EngineAssignExecOrderSpec.inlinePhp on line 66
          |Y=,Z=
          |
          |Notice: Undefined offset: 1 in /lang/EngineAssignExecOrderSpec.inlinePhp on line 70
          |AA=10
          |CC=10 DD=30
          |Array
          |(
          |    [0] => array created in f()
          |    [array entry created after f()] => Array
          |        (
          |            [1] => hello
          |        )
          |
          |)
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

    "Evaluation order during assignments. 4" in {
      // lang/engine_assignExecutionOrder_004
      script(
        """<?php
          |
          |function i1() {
          |        echo "i1\n";
          |        return 1;
          |}
          |
          |function i2() {
          |        echo "i2\n";
          |        return 1;
          |}
          |
          |function i3() {
          |        echo "i3\n";
          |        return 3;
          |}
          |
          |function i4() {
          |        global $a;
          |        $a = array(10, 11, 12, 13, 14);
          |        echo "i4\n";
          |        return 4;
          |}
          |
          |$a = 0; // $a should not be indexable till the i4 has been executed
          |list($a[i1()+i2()], , list($a[i3()], $a[i4()]), $a[]) = array (0, 1, array(30, 40), 3, 4);
          |
          |var_dump($a);
          |
          |?>""".stripMargin
      ).result must haveOutput(
        """i1
          |i2
          |i3
          |i4
          |array(6) {
          |  [0]=>
          |  int(10)
          |  [1]=>
          |  int(11)
          |  [2]=>
          |  int(0)
          |  [3]=>
          |  int(30)
          |  [4]=>
          |  int(40)
          |  [5]=>
          |  int(3)
          |}
          |""".stripMargin
      )
    }

    "Evaluation order during assignments. 5" in {
      // lang/engine_assignExecutionOrder_005
      script(
        """<?php
          |
          |function i1() {
          |        echo "i1\n";
          |        return 0;
          |}
          |
          |function i2() {
          |        echo "i2\n";
          |        return 0;
          |}
          |
          |function i3() {
          |        echo "i3\n";
          |        return 0;
          |}
          |
          |function i4() {
          |        echo "i4\n";
          |        return 0;
          |}
          |
          |function i5() {
          |        echo "i5\n";
          |        return 0;
          |}
          |
          |function i6() {
          |        echo "i6\n";
          |        return 0;
          |}
          |
          |$a = array(array(0));
          |$b = array(array(1));
          |$c = array(array(2));
          |
          |$a[i1()][i2()] = $b[i3()][i4()] = $c[i5()][i6()];
          |
          |var_dump($a);
          |var_dump($b);
          |var_dump($c);
          |
          |?>""".stripMargin
      ).result must haveOutput(
        """i1
          |i2
          |i3
          |i4
          |i5
          |i6
          |array(1) {
          |  [0]=>
          |  array(1) {
          |    [0]=>
          |    int(2)
          |  }
          |}
          |array(1) {
          |  [0]=>
          |  array(1) {
          |    [0]=>
          |    int(2)
          |  }
          |}
          |array(1) {
          |  [0]=>
          |  array(1) {
          |    [0]=>
          |    int(2)
          |  }
          |}
          |""".stripMargin
      )
    }

    "Evaluation order during assignments. 6" in {
      // lang/engine_assignExecutionOrder_006
      script(
        """<?php
          |
          |function i1() {
          |        echo "i1\n";
          |        return 0;
          |}
          |
          |function i2() {
          |        echo "i2\n";
          |        return 0;
          |}
          |
          |function i3() {
          |        echo "i3\n";
          |        return 0;
          |}
          |
          |function i4() {
          |        echo "i4\n";
          |        return 0;
          |}
          |
          |function i5() {
          |        echo "i5\n";
          |        return 0;
          |}
          |
          |function i6() {
          |        echo "i6\n";
          |        return 0;
          |}
          |
          |$a = array(array(0));
          |$b = array(array(1));
          |$c = array(array(2));
          |
          |$a[i1()][i2()] = ($b[i3()][i4()] = $c[i5()][i6()]);
          |var_dump($a);
          |var_dump($b);
          |var_dump($c);
          |
          |$a[i1()][i2()] = $b[i3()][i4()] = -$c[i5()][i6()];
          |var_dump($a);
          |var_dump($b);
          |var_dump($c);
          |
          |$a[i1()][i2()] = -($b[i3()][i4()] = +($c[i5()][i6()]));
          |var_dump($a);
          |var_dump($b);
          |var_dump($c);
          |
          |
          |?>""".stripMargin
      ).result must haveOutput(
        """i1
          |i2
          |i3
          |i4
          |i5
          |i6
          |array(1) {
          |  [0]=>
          |  array(1) {
          |    [0]=>
          |    int(2)
          |  }
          |}
          |array(1) {
          |  [0]=>
          |  array(1) {
          |    [0]=>
          |    int(2)
          |  }
          |}
          |array(1) {
          |  [0]=>
          |  array(1) {
          |    [0]=>
          |    int(2)
          |  }
          |}
          |i1
          |i2
          |i3
          |i4
          |i5
          |i6
          |array(1) {
          |  [0]=>
          |  array(1) {
          |    [0]=>
          |    int(-2)
          |  }
          |}
          |array(1) {
          |  [0]=>
          |  array(1) {
          |    [0]=>
          |    int(-2)
          |  }
          |}
          |array(1) {
          |  [0]=>
          |  array(1) {
          |    [0]=>
          |    int(2)
          |  }
          |}
          |i1
          |i2
          |i3
          |i4
          |i5
          |i6
          |array(1) {
          |  [0]=>
          |  array(1) {
          |    [0]=>
          |    int(-2)
          |  }
          |}
          |array(1) {
          |  [0]=>
          |  array(1) {
          |    [0]=>
          |    int(2)
          |  }
          |}
          |array(1) {
          |  [0]=>
          |  array(1) {
          |    [0]=>
          |    int(2)
          |  }
          |}
          |""".stripMargin
      )
    }

    "Check key execution order with &new. " in {
      // lang/angine_assignExecutionOrder_007
      script(
        """<?php
          |$a[2][3] = 'stdClass';
          |$a[$i=0][++$i] =& new $a[++$i][++$i];
          |print_r($a);
          |
          |$o = new stdClass;
          |$o->a =& new $a[$i=2][++$i];
          |$o->a->b =& new $a[$i=2][++$i];
          |print_r($o);
          |?>""".stripMargin
      ).result must haveOutput(
        """
          |Deprecated: Assigning the return value of new by reference is deprecated in /lang/EngineAssignExecOrderSpec.inlinePhp on line 3
          |
          |Deprecated: Assigning the return value of new by reference is deprecated in /lang/EngineAssignExecOrderSpec.inlinePhp on line 7
          |
          |Deprecated: Assigning the return value of new by reference is deprecated in /lang/EngineAssignExecOrderSpec.inlinePhp on line 8
          |Array
          |(
          |    [2] => Array
          |        (
          |            [3] => stdClass
          |        )
          |
          |    [0] => Array
          |        (
          |            [1] => stdClass Object
          |                (
          |                )
          |
          |        )
          |
          |)
          |stdClass Object
          |(
          |    [a] => stdClass Object
          |        (
          |            [b] => stdClass Object
          |                (
          |                )
          |
          |        )
          |
          |)
          |""".stripMargin
      )
    }

    "Ensure by value assignments leave temporaries on the stack, for all sorts of assignees." in {
      // lang/engine_assignExecutionorder_008
      script(
        """<?php
          |error_reporting(E_ALL & ~E_STRICT);
          |
          |function f() { return 0; }
          |$a[0][1] = 'good';
          |$a[1][1] = 'bad';
          |
          |echo "\n" . '$i=f() :';
          |echo $a[$i=f()][++$i];
          |unset($i);
          |
          |echo "\n" . '$$x=f() :';
          |$x='i';
          |echo $a[$$x=f()][++$$x];
          |unset($i, $x);
          |
          |echo "\n" . '${\'i\'}=f() :';
          |echo $a[${'i'}=f()][++${'i'}];
          |unset(${'i'});
          |
          |echo "\n" . '$i[0]=f() :';
          |echo $a[$i[0]=f()][++$i[0]];
          |unset($i);
          |
          |echo "\n" . '$i[0][0]=f() :';
          |echo $a[$i[0][0]=f()][++$i[0][0]];
          |unset($i);
          |
          |echo "\n" . '$i->p=f() :';
          |echo $a[$i->p=f()][++$i->p];
          |unset($i);
          |
          |echo "\n" . '$i->p->q=f() :';
          |echo $a[$i->p->q=f()][++$i->p->q];
          |unset($i);
          |
          |echo "\n" . '$i->p[0]=f() :';
          |echo $a[$i->p[0]=f()][++$i->p[0]];
          |unset($i);
          |
          |echo "\n" . '$i->p[0]->p=f() :';
          |echo $a[$i->p[0]->p=f()][++$i->p[0]->p];
          |unset($i);
          |
          |Class C {
          |	static $p;
          |}
          |
          |echo "\n" . 'C::$p=f() :';
          |echo $a[C::$p=f()][++C::$p];
          |
          |echo "\n" . 'C::$p[0]=f() :';
          |C::$p = array();
          |echo $a[C::$p[0]=f()][++C::$p[0]];
          |
          |echo "\n" . 'C::$p->q=f() :';
          |C::$p = new stdclass;
          |echo $a[C::$p->q=f()][++C::$p->q];
          |?>""".stripMargin
      ).result must haveOutput(
        """
          |$i=f() :good
          |$$x=f() :good
          |${'i'}=f() :good
          |$i[0]=f() :good
          |$i[0][0]=f() :good
          |$i->p=f() :
          |Warning: Creating default object from empty value in /lang/EngineAssignExecOrderSpec.inlinePhp on line 30
          |good
          |$i->p->q=f() :
          |Warning: Creating default object from empty value in /lang/EngineAssignExecOrderSpec.inlinePhp on line 34
          |good
          |$i->p[0]=f() :good
          |$i->p[0]->p=f() :
          |Warning: Creating default object from empty value in /lang/EngineAssignExecOrderSpec.inlinePhp on line 42
          |good
          |C::$p=f() :good
          |C::$p[0]=f() :good
          |C::$p->q=f() :good""".stripMargin
      )
    }

    "Execution ordering with comparison operators." in {
      // lang/engine_assignExecutionOrder_009
      script(
        """<?php
          |function f($x) {
          |	echo "f($x) ";
          |	return $x;
          |}
          |
          |echo "Function call args:\n";
          |var_dump(f($i=0) < f(++$i));
          |var_dump(f($i=0) <= f(++$i));
          |var_dump(f($i=0) > f(++$i));
          |var_dump(f($i=0) >= f(++$i));
          |
          |echo "\nArray indices:\n";
          |$a[1][2] = 0;
          |$a[3][4] = 1;
          |$i=0;
          |var_dump($a[$i=1][++$i] < $a[++$i][++$i]);
          |var_dump($a[$i=1][++$i] <= $a[++$i][++$i]);
          |var_dump($a[$i=1][++$i] > $a[++$i][++$i]);
          |var_dump($a[$i=1][++$i] >= $a[++$i][++$i]);
          |?>""".stripMargin
      ).result must haveOutput(
        """Function call args:
          |f(0) f(1) bool(true)
          |f(0) f(1) bool(true)
          |f(0) f(1) bool(false)
          |f(0) f(1) bool(false)
          |
          |Array indices:
          |bool(true)
          |bool(true)
          |bool(false)
          |bool(false)
          |""".stripMargin
      )
    }
  }
}
