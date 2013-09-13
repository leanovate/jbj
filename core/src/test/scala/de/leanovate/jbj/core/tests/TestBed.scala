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
        |// NOTE: This will become part of SPL
        |
        |class ArrayReferenceProxy implements ArrayAccess
        |{
        |	private $object;
        |	private $element;
        |
        |	function __construct(ArrayAccess $object, array &$element)
        |	{
        |		echo __METHOD__ . "(Array)\n";
        |		$this->object = $object;
        |		$this->element = &$element;
        |   var_dump($element);
        |   var_dump($this);
        | }
        |
        | function __destruct() {
        |		echo __METHOD__ . "(Array)\n";
        | }
        |
        |	function offsetExists($index) {
        |		echo __METHOD__ . "($this->element, $index)\n";
        |		return array_key_exists($index, $this->element);
        |	}
        |
        |	function offsetGet($index) {
        |		echo __METHOD__ . "(Array, $index)\n";
        |		return isset($this->element[$index]) ? $this->element[$index] : NULL;
        |	}
        |
        |	function offsetSet($index, $value) {
        |		echo __METHOD__ . "(Array, $index, $value)\n";
        |		$this->element[$index] = $value;
        |	}
        |
        |	function offsetUnset($index) {
        |		echo __METHOD__ . "(Array, $index)\n";
        |		unset($this->element[$index]);
        |	}
        |}
        |
        |class Peoples implements ArrayAccess
        |{
        |	public $person;
        |
        |	function __construct()
        |	{
        |		$this->person = array(array('name'=>'Foo'));
        |	}
        |
        |	function offsetExists($index)
        |	{
        |		return array_key_exists($index, $this->person);
        |	}
        |
        |	function offsetGet($index)
        |	{
        |		return new ArrayReferenceProxy($this, $this->person[$index]);
        |	}
        |
        |	function offsetSet($index, $value)
        |	{
        |		$this->person[$index] = $value;
        |	}
        |
        |	function offsetUnset($index)
        |	{
        |		unset($this->person[$index]);
        |	}
        |}
        |
        |$people = new Peoples;
        |
        |var_dump($people[0]);
        |var_dump($people->person);
        |""".stripMargin)
  }
}
