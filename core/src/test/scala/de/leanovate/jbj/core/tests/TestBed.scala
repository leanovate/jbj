/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                 (Tests based on PHP)   */

package de.leanovate.jbj.core.tests

import scala.util.parsing.input.Reader
import de.leanovate.jbj.core.parser.JbjTokens.Token
import de.leanovate.jbj.core.parser._
import de.leanovate.jbj.core.ast.Prog
import de.leanovate.jbj.core.JbjEnv
import de.leanovate.jbj.api.JbjSettings
import de.leanovate.jbj.runtime.env.CgiEnvironment
import de.leanovate.jbj.core.parser.InitialLexer
import de.leanovate.jbj.core.parser.ParseContext
import de.leanovate.jbj.core.JbjEnv
import scala.Some

object TestBed {
  //Simplify testing
  def test(exprstr: String) = {
    var tokens: Reader[Token] = new TokenReader(exprstr, InitialLexerMode(shortOpenTag = true, aspTags = true).newLexer())

    println("Tokens")
    var count = 0
    while (!tokens.atEnd && count < 1000) {
      println(tokens.first)
      tokens = tokens.rest
      count += 1
    }

    val jbj = JbjEnv(TestLocator, errorStream = Some(System.err))
    val tokens2 = new TokenReader(exprstr, InitialLexerMode(shortOpenTag = true, aspTags = true).newLexer())
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
        |function test($s, $mode)
        |{
        |	return (($mode & PHP_OUTPUT_HANDLER_START)?"[":"") . $s . (($mode & PHP_OUTPUT_HANDLER_END)?"]\n":"");
        |}
        |function t1()
        |{
        |	ob_start("test");
        |	echo "Hello from t1 1 ";
        |        echo "Hello from t1 2 ";
        |        ob_end_flush();
        |}
        |function t2()
        |{
        |	ob_start("test");
        |	echo "Hello from t2 1 ";
        |        ob_flush();
        |        echo "Hello from t2 2 ";
        |        ob_end_flush();
        |}
        |function t3()
        |{
        |	ob_start("test");
        |        echo "Hello from t3 1 ";
        |        ob_clean();
        |        echo "Hello from t3 2 ";
        |        ob_end_flush();
        |}
        |
        |t1(); echo "\n";
        |t2(); echo "\n";
        |t3(); echo "\n";
        |?>""".stripMargin)
  }
}
