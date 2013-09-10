/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                 (Tests based on PHP)   */

package de.leanovate.jbj.core.tests

import scala.util.parsing.input.Reader
import de.leanovate.jbj.core.parser.JbjTokens.Token
import de.leanovate.jbj.core.parser.{JbjParser, ParseContext, InitialLexer, TokenReader}
import de.leanovate.jbj.core.ast.Prog
import de.leanovate.jbj.core.JbjEnv
import de.leanovate.jbj.api.JbjSettings
import de.leanovate.jbj.runtime.env.CgiEnvironment

object TestBed {
  //Simplify testing
  def test(exprstr: String) = {
    var tokens: Reader[Token] = new TokenReader(exprstr, InitialLexer)

    println("Tokens")
    var count = 0
    while (!tokens.atEnd && count < 1000) {
      println(tokens.first)
      tokens = tokens.rest
      count += 1
    }

    val jbj = JbjEnv(TestLocator, errorStream = Some(System.err))
    val tokens2 = new TokenReader(exprstr, InitialLexer)
    val parser = new JbjParser(ParseContext("/classes/bla.php", jbj.settings))
    parser.phrase(parser.start)(tokens2) match {
      case parser.Success(tree: Prog, _) =>
        println("Tree")
        println(AstAsXmlNodeVisitor.dump(tree))

        implicit val context = jbj.newGlobalContext(System.out)

        CgiEnvironment.httpRequest(
          TestRequestInfo.post("/bla", "multipart/form-data; boundary=---------------------------20896060251896012921717172737",
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
              | """.stripMargin.replace("\n", "\r\n"), Seq.empty))

        context.settings.setErrorReporting(JbjSettings.E_ALL)
        try {
          tree.exec(context)
        } finally {
          context.cleanup()
        }
      case e: parser.NoSuccess =>
        println(e)
    }
  }

  //A main method for testing
  def main(args: Array[String]) {
    test(
      """<?php
        |class object implements ArrayAccess {
        |
        |	public $a = array('1st', 1, 2=>'3rd', '4th'=>4);
        |
        |	function offsetExists($index) {
        |		echo __METHOD__ . "($index)\n";
        |		return array_key_exists($index, $this->a);
        |	}
        |	function offsetGet($index) {
        |		echo __METHOD__ . "($index)\n";
        |		return $this->a[$index];
        |	}
        |	function offsetSet($index, $newval) {
        |		echo __METHOD__ . "($index,$newval)\n";
        |		return $this->a[$index] = $newval;
        |	}
        |	function offsetUnset($index) {
        |		echo __METHOD__ . "($index)\n";
        |		unset($this->a[$index]);
        |	}
        |}
        |
        |$obj = new Object;
        |
        |var_dump($obj->a);
        |
        |echo "===EMPTY===\n";
        |var_dump(empty($obj[0]));
        |var_dump(empty($obj[1]));
        |var_dump(empty($obj[2]));
        |var_dump(empty($obj['4th']));
        |var_dump(empty($obj['5th']));
        |var_dump(empty($obj[6]));
        |
        |echo "===isset===\n";
        |var_dump(isset($obj[0]));
        |var_dump(isset($obj[1]));
        |var_dump(isset($obj[2]));
        |var_dump(isset($obj['4th']));
        |var_dump(isset($obj['5th']));
        |var_dump(isset($obj[6]));
        |
        |echo "===offsetGet===\n";
        |var_dump($obj[0]);
        |var_dump($obj[1]);
        |var_dump($obj[2]);
        |var_dump($obj['4th']);
        |var_dump($obj['5th']);
        |var_dump($obj[6]);
        |
        |echo "===offsetSet===\n";
        |echo "WRITE 1\n";
        |$obj[1] = 'Changed 1';
        |var_dump($obj[1]);
        |echo "WRITE 2\n";
        |$obj['4th'] = 'Changed 4th';
        |var_dump($obj['4th']);
        |echo "WRITE 3\n";
        |$obj['5th'] = 'Added 5th';
        |var_dump($obj['5th']);
        |echo "WRITE 4\n";
        |$obj[6] = 'Added 6';
        |var_dump($obj[6]);
        |
        |var_dump($obj[0]);
        |var_dump($obj[2]);
        |
        |$x = $obj[6] = 'changed 6';
        |var_dump($obj[6]);
        |var_dump($x);
        |
        |echo "===unset===\n";
        |var_dump($obj->a);
        |unset($obj[2]);
        |unset($obj['4th']);
        |unset($obj[7]);
        |unset($obj['8th']);
        |var_dump($obj->a);
        |
        |?>
        |===DONE===""".stripMargin)
  }
}
