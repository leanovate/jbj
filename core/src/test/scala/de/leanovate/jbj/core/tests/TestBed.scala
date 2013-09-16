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
        |
        |function my_error_handler($errno, $errstr, $errfile, $errline) {
        |	var_dump($errstr);
        |}
        |
        |set_error_handler('my_error_handler');
        |
        |class test1
        |{
        |}
        |
        |class test2
        |{
        |    function __toString()
        |    {
        |    	echo __METHOD__ . "()\n";
        |        return "Converted\n";
        |    }
        |}
        |
        |class test3
        |{
        |    function __toString()
        |    {
        |    	echo __METHOD__ . "()\n";
        |        return 42;
        |    }
        |}
        |echo "====test1====\n";
        |$o = new test1;
        |print_r($o);
        |var_dump((string)$o);
        |var_dump($o);
        |
        |echo "====test2====\n";
        |$o = new test2;
        |print_r($o);
        |print $o;
        |var_dump($o);
        |echo "====test3====\n";
        |echo $o;
        |
        |echo "====test4====\n";
        |echo "string:".$o;
        |
        |echo "====test5====\n";
        |echo 1 . $o;
        |echo 1 , $o;
        |
        |echo "====test6====\n";
        |echo $o . $o;
        |echo $o , $o;
        |
        |echo "====test7====\n";
        |$ar = array();
        |$ar[$o->__toString()] = "ERROR";
        |echo $ar[$o];
        |
        |echo "====test8====\n";
        |var_dump(trim($o));
        |var_dump(trim((string)$o));
        |
        |echo "====test9====\n";
        |echo sprintf("%s", $o);
        |
        |echo "====test10====\n";
        |$o = new test3;
        |var_dump($o);
        |echo $o;
        |
        |?>
        |====DONE====""".stripMargin)
  }
}
