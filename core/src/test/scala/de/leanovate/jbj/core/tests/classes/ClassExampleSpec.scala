/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                 (Tests based on PHP)   */

package de.leanovate.jbj.core.tests.classes

import de.leanovate.jbj.core.tests.TestJbjExecutor
import de.leanovate.jbj.core.runtime.exception.FatalErrorJbjException
import org.specs2.mutable.SpecificationWithJUnit

class ClassExampleSpec extends SpecificationWithJUnit with TestJbjExecutor {
  "Class examples" should {
    "ZE2 An abstract class cannot be instanciated" in {
      // classes/class_abstract
      script(
        """<?php
          |
          |abstract class base {
          |	function show() {
          |		echo "base\n";
          |	}
          |}
          |
          |class derived extends base {
          |}
          |
          |$t = new derived();
          |$t->show();
          |
          |$t = new base();
          |$t->show();
          |
          |echo "Done\n"; // shouldn't be displayed
          |?>""".stripMargin
      ).result must haveOutput(
        """base
          |
          |Fatal error: Cannot instantiate abstract class base in /classes/ClassExampleSpec.inlinePhp on line 15
          |""".stripMargin
      )
    }

    "Classes general test" in {
      // classes/class_example
      script(
        """<?php
          |
          |/* pretty nifty object oriented code! */
          |
          |class user {
          |  public $first_name,$family_name,$address,$phone_num;
          |  function display()
          |  {
          |    echo "User information\n";
          |    echo "----------------\n\n";
          |    echo "First name:\t  ".$this->first_name."\n";
          |    echo "Family name:\t  ".$this->family_name."\n";
          |    echo "Address:\t  ".$this->address."\n";
          |    echo "Phone:\t\t  ".$this->phone_num."\n";
          |    echo "\n\n";
          |  }
          |  function initialize($first_name,$family_name,$address,$phone_num)
          |  {
          |    $this->first_name = $first_name;
          |    $this->family_name = $family_name;
          |    $this->address = $address;
          |    $this->phone_num = $phone_num;
          |  }
          |};
          |
          |
          |function test($u)
          |{  /* one can pass classes as arguments */
          |  $u->display();
          |  $t = $u;
          |  $t->address = "New address...";
          |  return $t;  /* and also return them as return values */
          |}
          |
          |$user1 = new user;
          |$user2 = new user;
          |
          |$user1->initialize("Zeev","Suraski","Ben Gourion 3, Kiryat Bialik, Israel","+972-4-8713139");
          |$user2->initialize("Andi","Gutmans","Haifa, Israel","+972-4-8231621");
          |$user1->display();
          |$user2->display();
          |
          |$tmp = test($user2);
          |$tmp->display();
          |
          |?>""".stripMargin
      ).result must haveOutput(
        """User information
          |----------------
          |
          |First name:	  Zeev
          |Family name:	  Suraski
          |Address:	  Ben Gourion 3, Kiryat Bialik, Israel
          |Phone:		  +972-4-8713139
          |
          |
          |User information
          |----------------
          |
          |First name:	  Andi
          |Family name:	  Gutmans
          |Address:	  Haifa, Israel
          |Phone:		  +972-4-8231621
          |
          |
          |User information
          |----------------
          |
          |First name:	  Andi
          |Family name:	  Gutmans
          |Address:	  Haifa, Israel
          |Phone:		  +972-4-8231621
          |
          |
          |User information
          |----------------
          |
          |First name:	  Andi
          |Family name:	  Gutmans
          |Address:	  New address...
          |Phone:		  +972-4-8231621
          |
          |
          |""".stripMargin
      )
    }

    "ZE2 A final class cannot be inherited" in {
      // classes/class_final
      script(
        """<?php
          |
          |final class base {
          |	function show() {
          |		echo "base\n";
          |	}
          |}
          |
          |$t = new base();
          |
          |class derived extends base {
          |}
          |
          |echo "Done\n"; // shouldn't be displayed
          |?>""".stripMargin
      ).result must (haveOutput(
        """
          |Fatal error: Class derived may not inherit from final class (base) in /classes/ClassExampleSpec.inlinePhp on line 11
          |""".stripMargin
      ) and haveThrown(classOf[FatalErrorJbjException]))
    }

    "Instantiate stdClass" in {
      // classes/class_stdclass
      script(
        """<?php
          |
          |$obj = new stdClass;
          |
          |echo get_class($obj)."\n";
          |
          |echo "Done\n";
          |?>""".stripMargin
      ).result must haveOutput(
        """stdClass
          |Done
          |""".stripMargin
      )
    }
  }
}
