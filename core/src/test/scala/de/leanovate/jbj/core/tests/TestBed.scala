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
        |/* Whether it's scoped or not, a static closure cannot have
        | * a bound instance. It should also not be automatically converted
        | * to a non-static instance when attempting to bind one */
        |
        |$staticUnscoped = static function () { var_dump(isset(A::$priv)); var_dump(isset($this)); };
        |
        |class A {
        |	private static $priv = 7;
        |	static function getStaticClosure() {
        |		return static function() { var_dump(isset(A::$priv)); var_dump(isset($this)); };
        |	}
        |}
        |
        |$staticScoped = A::getStaticClosure();
        |
        |echo "Before binding", "\n";
        |$staticUnscoped(); echo "\n";
        |$staticScoped(); echo "\n";
        |
        |echo "After binding, null scope, no instance", "\n";
        |$d = $staticUnscoped->bindTo(null, null); $d(); echo "\n";
        |$d = $staticScoped->bindTo(null, null); $d(); echo "\n";
        |
        |echo "After binding, null scope, with instance", "\n";
        |$d = $staticUnscoped->bindTo(new A, null); $d(); echo "\n";
        |$d = $staticScoped->bindTo(new A, null); $d(); echo "\n";
        |
        |echo "After binding, with scope, no instance", "\n";
        |$d = $staticUnscoped->bindTo(null, 'A'); $d(); echo "\n";
        |$d = $staticScoped->bindTo(null, 'A'); $d(); echo "\n";
        |
        |echo "After binding, with scope, with instance", "\n";
        |$d = $staticUnscoped->bindTo(new A, 'A'); $d(); echo "\n";
        |$d = $staticScoped->bindTo(new A, 'A'); $d(); echo "\n";
        |
        |echo "Done.\n";
        |""".stripMargin)
  }
}
