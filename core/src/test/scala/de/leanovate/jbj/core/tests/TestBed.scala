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
import de.leanovate.jbj.core.{JbjEnvironmentBuilder, JbjEnv}
import de.leanovate.jbj.runtime.env.CgiEnvironment
import de.leanovate.jbj.core.parser.InitialLexer
import de.leanovate.jbj.core.parser.ParseContext
import scala.Some
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
        |class C {
        |	public $a = "Original a";
        |	public $b = "Original b";
        |	public $c = "Original c";
        |	protected $d = "Original d";
        |	private $e = "Original e";
        |
        |	function doForEachC() {
        |		echo "in C::doForEachC\n";
        |		foreach ($this as $k=>&$v) {
        |			var_dump($v);
        |			$v="changed.$k";
        |		}
        |	}
        |
        |	static function doForEach($obj) {
        |		echo "in C::doForEach\n";
        |		foreach ($obj as $k=>&$v) {
        |			var_dump($v);
        |			$v="changed.$k";
        |		}
        |	}
        |
        |	function doForEachOnThis() {
        |		echo "in C::doForEachOnThis\n";
        |		foreach ($this as $k=>&$v) {
        |			var_dump($v);
        |			$v="changed.$k";
        |		}
        |	}
        |
        |}
        |
        |class D extends C {
        |
        |	private $f = "Original f";
        |	protected $g = "Original g";
        |
        |	static function doForEach($obj) {
        |		echo "in D::doForEach\n";
        |		foreach ($obj as $k=>&$v) {
        |			var_dump($v);
        |			$v="changed.$k";
        |		}
        |	}
        |
        |	function doForEachOnThis() {
        |		echo "in D::doForEachOnThis\n";
        |		foreach ($this as $k=>&$v) {
        |			var_dump($v);
        |			$v="changed.$k";
        |		}
        |	}
        |}
        |
        |class E extends D {
        |	public $a = "Overridden a";
        |	public $b = "Overridden b";
        |	public $c = "Overridden c";
        |	protected $d = "Overridden d";
        |	private $e = "Overridden e";
        |
        |	static function doForEach($obj) {
        |		echo "in E::doForEach\n";
        |		foreach ($obj as $k=>&$v) {
        |			var_dump($v);
        |			$v="changed.$k";
        |		}
        |	}
        |
        |	function doForEachOnThis() {
        |		echo "in E::doForEachOnThis\n";
        |		foreach ($this as $k=>&$v) {
        |			var_dump($v);
        |			$v="changed.$k";
        |		}
        |	}
        |}
        |
        |echo "\n\nIterate over various generations from within overridden methods:\n";
        |echo "\n--> Using instance of C:\n";
        |$myC = new C;
        |$myC->doForEachOnThis();
        |var_dump($myC);
        |echo "\n--> Using instance of D:\n";
        |$myD = new D;
        |$myD->doForEachOnThis();
        |var_dump($myD);
        |echo "\n--> Using instance of E:\n";
        |$myE = new E;
        |$myE->doForEachOnThis();
        |var_dump($myE);
        |
        |echo "\n\nIterate over various generations from within an inherited method:\n";
        |echo "\n--> Using instance of C:\n";
        |$myC = new C;
        |$myC->doForEachC();
        |var_dump($myC);
        |echo "\n--> Using instance of D:\n";
        |$myD = new D;
        |$myD->doForEachC();
        |var_dump($myD);
        |echo "\n--> Using instance of E:\n";
        |$myE = new E;
        |$myE->doForEachC();
        |var_dump($myE);
        |
        |echo "\n\nIterate over various generations from within an overridden static method:\n";
        |echo "\n--> Using instance of C:\n";
        |$myC = new C;
        |C::doForEach($myC);
        |var_dump($myC);
        |$myC = new C;
        |D::doForEach($myC);
        |var_dump($myC);
        |$myC = new C;
        |E::doForEach($myC);
        |var_dump($myC);
        |echo "\n--> Using instance of D:\n";
        |$myD = new D;
        |C::doForEach($myD);
        |var_dump($myD);
        |$myD = new D;
        |D::doForEach($myD);
        |var_dump($myD);
        |$myD = new D;
        |E::doForEach($myD);
        |var_dump($myD);
        |echo "\n--> Using instance of E:\n";
        |$myE = new E;
        |C::doForEach($myE);
        |var_dump($myE);
        |$myE = new E;
        |D::doForEach($myE);
        |var_dump($myE);
        |$myE = new E;
        |E::doForEach($myE);
        |var_dump($myE);
        |
        |
        |echo "\n\nIterate over various generations from outside the object:\n";
        |echo "\n--> Using instance of C:\n";
        |$myC = new C;
        |foreach ($myC as $k=>&$v) {
        |	var_dump($v);
        |	$v="changed.$k";
        |}
        |var_dump($myC);
        |echo "\n--> Using instance of D:\n";
        |$myD = new D;
        |foreach ($myD as $k=>&$v) {
        |	var_dump($v);
        |	$v="changed.$k";
        |}
        |var_dump($myD);
        |echo "\n--> Using instance of E:\n";
        |$myE = new E;
        |foreach ($myE as $k=>&$v) {
        |	var_dump($v);
        |	$v="changed.$k";
        |}
        |var_dump($myE);
        |?>""".stripMargin)
  }
}
