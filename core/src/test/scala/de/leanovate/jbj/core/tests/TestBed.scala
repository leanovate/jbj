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
        |class foo
        |{
        |	public $list = array();
        |
        |	function finalize() {
        |		print __CLASS__."::".__FUNCTION__."\n";
        |		$cl = &$this->list;
        |	}
        |
        |	function &method1() {
        |		print __CLASS__."::".__FUNCTION__."\n";
        |		return @$this->foo;
        |	}
        |
        |	function &method2() {
        |		print __CLASS__."::".__FUNCTION__."\n";
        |		return $this->foo;
        |	}
        |
        |	function method3() {
        |		print __CLASS__."::".__FUNCTION__."\n";
        |		return @$this->foo;
        |	}
        |}
        |
        |class bar
        |{
        |	function run1() {
        |		print __CLASS__."::".__FUNCTION__."\n";
        |		$this->instance = new foo();
        |		$this->instance->method1($this);
        |		$this->instance->method1($this);
        |	}
        |
        |	function run2() {
        |		print __CLASS__."::".__FUNCTION__."\n";
        |		$this->instance = new foo();
        |		$this->instance->method2($this);
        |		$this->instance->method2($this);
        |	}
        |
        |	function run3() {
        |		print __CLASS__."::".__FUNCTION__."\n";
        |		$this->instance = new foo();
        |		$this->instance->method3($this);
        |		$this->instance->method3($this);
        |	}
        |}
        |
        |function ouch(&$bar) {
        |	print __FUNCTION__."\n";
        |	@$a = $a;
        |	$bar->run1();
        |}
        |
        |function ok1(&$bar) {
        |	print __FUNCTION__."\n";
        |	$bar->run1();
        |}
        |
        |function ok2(&$bar) {
        |	print __FUNCTION__."\n";
        |	@$a = $a;
        |	$bar->run2();
        |}
        |
        |function ok3(&$bar) {
        |	print __FUNCTION__."\n";
        |	@$a = $a;
        |	$bar->run3();
        |}
        |
        |$bar = &new bar();
        |ok1($bar);
        |$bar->instance->finalize();
        |print "done!\n";
        |ok2($bar);
        |$bar->instance->finalize();
        |print "done!\n";
        |ok3($bar);
        |$bar->instance->finalize();
        |print "done!\n";
        |ouch($bar);
        |$bar->instance->finalize();
        |print "I'm alive!\n";
        |?>>""".stripMargin)
  }
}
