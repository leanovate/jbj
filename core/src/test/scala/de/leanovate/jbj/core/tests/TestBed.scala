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
import de.leanovate.jbj.core.JbjEnvironmentBuilder
import de.leanovate.jbj.runtime.env.CgiEnvironment
import de.leanovate.jbj.core.parser.ParseContext
import de.leanovate.jbj.api.http.JbjSettings

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

    val jbj = JbjEnvironmentBuilder().withScriptLocator(TestLocator).withErrStream(System.err).build()
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
        |
        |class foo {
        |	public $pp1 = 1;
        |	private $pp2 = 2;
        |	protected $pp3 = 3;
        |
        |	function bar() {
        |		var_dump(property_exists("foo","pp1"));
        |		var_dump(property_exists("foo","pp2"));
        |		var_dump(property_exists("foo","pp3"));
        |	}
        |}
        |
        |class bar extends foo {
        |	function test() {
        |		var_dump(property_exists("foo","pp1"));
        |		var_dump(property_exists("foo","pp2"));
        |		var_dump(property_exists("foo","pp3"));
        |	}
        |}
        |
        |var_dump(property_exists());
        |var_dump(property_exists(""));
        |var_dump(property_exists("foo","pp1"));
        |var_dump(property_exists("foo","pp2"));
        |var_dump(property_exists("foo","pp3"));
        |var_dump(property_exists("foo","nonexistent"));
        |var_dump(property_exists("fo","nonexistent"));
        |var_dump(property_exists("foo",""));
        |var_dump(property_exists("","test"));
        |var_dump(property_exists("",""));
        |
        |$foo = new foo;
        |
        |var_dump(property_exists($foo,"pp1"));
        |var_dump(property_exists($foo,"pp2"));
        |var_dump(property_exists($foo,"pp3"));
        |var_dump(property_exists($foo,"nonexistent"));
        |var_dump(property_exists($foo,""));
        |var_dump(property_exists(array(),"test"));
        |var_dump(property_exists(1,"test"));
        |var_dump(property_exists(true,"test"));
        |
        |$foo->bar();
        |echo ">>>>>>\n";
        |$bar = new bar;
        |$bar->test();
        |echo ">>>>>>\n";
        |
        |echo "Done\n";
        |?>
        |""".stripMargin)
  }
}
