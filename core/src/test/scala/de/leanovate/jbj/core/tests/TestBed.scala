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
import de.leanovate.jbj.runtime.context.HttpResponseContext
import com.github.marschall.memoryfilesystem.MemoryFileSystemBuilder
import java.nio.file.Files

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

    val filesystem = MemoryFileSystemBuilder.newLinux().build("test")
    Files.createDirectory(filesystem.getPath("/tmp"))
    Files.createDirectory(filesystem.getPath("/classes"))
    Files.createFile(filesystem.getPath("/classes", "bla.php"))
    val jbj = JbjEnvironmentBuilder().withScriptLocator(TestLocator).withErrStream(System.err).withFileSystem(filesystem).build()
    val tokens2 = new TokenReader(exprstr, InitialLexerMode(shortOpenTag = true, aspTags = true).newLexer())
    val parser = new JbjParser(ParseContext("/classes/bla.php", jbj.settings))
    parser.phrase(parser.start)(tokens2) match {
      case parser.Success(tree: Prog, _) =>
        println("Tree")
        println(AstAsXmlNodeVisitor.dump(tree))

        implicit val context = jbj.newGlobalContext(System.out, None)

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
        context.settings.setSessionAuthStart(true)
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
        |error_reporting(0);
        |
        |echo "Testing strtok: ";
        |
        |$str = "testing 1/2\\3";
        |$tok1 = strtok($str, " ");
        |$tok2 = strtok("/");
        |$tok3 = strtok("\\");
        |$tok4 = strtok(".");
        |if ($tok1 != "testing") {
        |	echo("failed 1\n");
        |} elseif ($tok2 != "1") {
        |	echo("failed 2\n");
        |} elseif ($tok3 != "2") {
        |	echo("failed 3\n");
        |} elseif ($tok4 != "3") {
        |	echo("failed 4\n");
        |} else {
        |	echo("passed\n");
        |}
        |
        |echo "Testing strstr: ";
        |$test = "This is a test";
        |$found1 = strstr($test, 32);
        |$found2 = strstr($test, "a ");
        |if ($found1 != " is a test") {
        |	echo("failed 1\n");
        |} elseif ($found2 != "a test") {
        |	echo("failed 2\n");
        |} else {
        |	echo("passed\n");
        |}
        |
        |echo "Testing strrchr: ";
        |$test = "fola fola blakken";
        |$found1 = strrchr($test, "b");
        |$found2 = strrchr($test, 102);
        |if ($found1 != "blakken") {
        |	echo("failed 1\n");
        |} elseif ($found2 != "fola blakken") {
        |	echo("failed 2\n");
        |}
        |else {
        |	echo("passed\n");
        |}
        |
        |echo "Testing strtoupper: ";
        |$test = "abCdEfg";
        |$upper = strtoupper($test);
        |if ($upper == "ABCDEFG") {
        |	echo("passed\n");
        |} else {
        |	echo("failed!\n");
        |}
        |
        |echo "Testing strtolower: ";
        |$test = "ABcDeFG";
        |$lower = strtolower($test);
        |if ($lower == "abcdefg") {
        |	echo("passed\n");
        |} else {
        |	echo("failed!\n");
        |}
        |
        |echo "Testing substr: ";
        |$tests = $ok = 0;
        |$string = "string12345";
        |$tests++; if (substr($string, 2, 10) == "ring12345") { $ok++; }
        |$tests++; if (substr($string, 4, 7) == "ng12345") { $ok++; }
        |$tests++; if (substr($string, 4) == "ng12345") { $ok++; }
        |$tests++; if (substr($string, 10, 2) == "5") { $ok++; }
        |$tests++; if (substr($string, 6, 0) == "") { $ok++; }
        |$tests++; if (substr($string, -2, 2) == "45") { $ok++; }
        |$tests++; if (substr($string, 1, -1) == "tring1234") { $ok++; }
        |$tests++; if (substr($string, -1, -2) == "") { $ok++; }
        |$tests++; if (substr($string, -3, -2) == "3") { $ok++; }
        |
        |if ($tests == $ok) {
        |	echo("passed\n");
        |} else {
        |	echo("failed!\n");
        |}
        |
        |$raw = ' !"#$%&\'()*+,-./0123456789:;<=>?'
        |     . '@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_'
        |     . '`abcdefghijklmnopqrstuvwxyz{|}~'
        |     . "\0";
        |
        |echo "Testing rawurlencode: ";
        |$encoded = rawurlencode($raw);
        |$correct = '%20%21%22%23%24%25%26%27%28%29%2A%2B%2C-.%2F0123456789%3A%3B%3C%3D%3E%3F'
        |         . '%40ABCDEFGHIJKLMNOPQRSTUVWXYZ%5B%5C%5D%5E_'
        |         . '%60abcdefghijklmnopqrstuvwxyz%7B%7C%7D~'
        |         . '%00';
        |if ($encoded == $correct) {
        |	echo("passed\n");
        |} else {
        |	echo("failed!\n");
        |}
        |
        |echo "Testing rawurldecode: ";
        |$decoded = rawurldecode($correct);
        |if ($decoded == $raw) {
        |	echo("passed\n");
        |} else {
        |	echo("failed!\n");
        |}
        |
        |echo "Testing urlencode: ";
        |$encoded = urlencode($raw);
        |$correct = '+%21%22%23%24%25%26%27%28%29%2A%2B%2C-.%2F0123456789%3A%3B%3C%3D%3E%3F'
        |         . '%40ABCDEFGHIJKLMNOPQRSTUVWXYZ%5B%5C%5D%5E_'
        |         . '%60abcdefghijklmnopqrstuvwxyz%7B%7C%7D%7E'
        |         . '%00';
        |if ($encoded == $correct) {
        |	echo("passed\n");
        |} else {
        |	echo("failed!\n");
        |}
        |
        |echo "Testing urldecode: ";
        |$decoded = urldecode($correct);
        |if ($decoded == $raw) {
        |	echo("passed\n");
        |} else {
        |	echo("failed!\n");
        |}
        |
        |echo "Testing quotemeta: ";
        |$raw = "a.\\+*?" . chr(91) . "^" . chr(93) . "b\$c";
        |$quoted = quotemeta($raw);
        |if ($quoted == "a\\.\\\\\\+\\*\\?\\[\\^\\]b\\\$c") {
        |	echo("passed\n");
        |} else {
        |	echo("failed!\n");
        |}
        |
        |echo "Testing ufirst: ";
        |$str = "fahrvergnuegen";
        |$uc = ucfirst($str);
        |if ($uc == "Fahrvergnuegen") {
        |	echo("passed\n");
        |} else {
        |	echo("failed!\n");
        |}
        |
        |echo "Testing strtr: ";
        |$str = "test abcdefgh";
        |$tr = strtr($str, "def", "456");
        |if ($tr == "t5st abc456gh") {
        |	echo("passed\n");
        |} else {
        |	echo("failed!\n");
        |}
        |
        |echo "Testing addslashes: ";
        |$str = "\"\\'";
        |$as = addslashes($str);
        |if ($as == "\\\"\\\\\\'") {
        |	echo("passed\n");
        |} else {
        |	echo("failed!\n");
        |}
        |
        |echo "Testing stripslashes: ";
        |$str = "\$\\'";
        |$ss = stripslashes($str);
        |if ($ss == "\$'") {
        |	echo("passed\n");
        |} else {
        |	echo("failed!\n");
        |}
        |
        |
        |echo "Testing uniqid: ";
        |$str = "prefix";
        |$ui1 = uniqid($str);
        |$ui2 = uniqid($str);
        |
        |$len = strncasecmp(PHP_OS, 'CYGWIN', 6) ? 19 : 29;
        |
        |if (strlen($ui1) == strlen($ui2) && strlen($ui1) == $len && $ui1 != $ui2) {
        |	echo("passed\n");
        |} else {
        |	echo("failed!\n");
        |}
        |
        |?>
        |""".stripMargin)
  }
}
