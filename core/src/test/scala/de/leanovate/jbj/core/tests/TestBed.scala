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
        |interface ArrayProxyAccess extends ArrayAccess
        |{
        |	function proxyGet($element);
        |	function proxySet($element, $index, $value);
        |	function proxyUnset($element, $index);
        |}
        |
        |class ArrayProxy implements ArrayAccess
        |{
        |	private $object;
        |	private $element;
        |
        |	function __construct(ArrayProxyAccess $object, $element)
        |	{
        |		echo __METHOD__ . "($element)\n";
        |		if (!$object->offsetExists($element))
        |		{
        |			$object[$element] = array();
        |		}
        |		$this->object = $object;
        |		$this->element = $element;
        |	}
        |
        |	function offsetExists($index) {
        |		echo __METHOD__ . "($this->element, $index)\n";
        |		return array_key_exists($index, $this->object->proxyGet($this->element));
        |	}
        |
        |	function offsetGet($index) {
        |		echo __METHOD__ . "($this->element, $index)\n";
        |		$tmp = $this->object->proxyGet($this->element);
        |		return isset($tmp[$index]) ? $tmp[$index] : NULL;
        |	}
        |
        |	function offsetSet($index, $value) {
        |		echo __METHOD__ . "($this->element, $index, $value)\n";
        |		$this->object->proxySet($this->element, $index, $value);
        |	}
        |
        |	function offsetUnset($index) {
        |		echo __METHOD__ . "($this->element, $index)\n";
        |		$this->object->proxyUnset($this->element, $index);
        |	}
        |}
        |
        |class Peoples implements ArrayProxyAccess
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
        |		return new ArrayProxy($this, $index);
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
        |
        |	function proxyGet($element)
        |	{
        |		return $this->person[$element];
        |	}
        |
        |	function proxySet($element, $index, $value)
        |	{
        |		$this->person[$element][$index] = $value;
        |	}
        |
        |	function proxyUnset($element, $index)
        |	{
        |		unset($this->person[$element][$index]);
        |	}
        |}
        |
        |$people = new Peoples;
        |
        |var_dump($people->person[0]['name']);
        |$people->person[0]['name'] = $people->person[0]['name'] . 'Bar';
        |var_dump($people->person[0]['name']);
        |$people->person[0]['name'] .= 'Baz';
        |var_dump($people->person[0]['name']);
        |
        |echo "===ArrayOverloading===\n";
        |
        |$people = new Peoples;
        |
        |var_dump($people[0]);
        |var_dump($people[0]['name']);
        |$people[0]['name'] = 'FooBar';
        |var_dump($people[0]['name']);
        |$people[0]['name'] = $people->person[0]['name'] . 'Bar';
        |var_dump($people[0]['name']);
        |$people[0]['name'] .= 'Baz';
        |var_dump($people[0]['name']);
        |unset($people[0]['name']);
        |var_dump($people[0]);
        |var_dump($people[0]['name']);
        |$people[0]['name'] = 'BlaBla';
        |var_dump($people[0]['name']);
        |
        |?>
        |===DONE===""".stripMargin)
  }
}
