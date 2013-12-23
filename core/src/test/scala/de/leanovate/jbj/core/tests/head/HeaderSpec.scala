/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                 (Tests based on PHP)   */

package de.leanovate.jbj.core.tests.head

import de.leanovate.jbj.core.tests.TestJbjExecutor
import org.specs2.mutable.SpecificationWithJUnit
import de.leanovate.jbj.runtime.exception.ParseJbjException

class HeaderSpec extends SpecificationWithJUnit with TestJbjExecutor {
  "header()" should {
    "set a header" in {
      script(
        """<?php
          |header("Content-type: application/pdf");
          |?>""".stripMargin
      ).result.httpResponseContext.httpResponseHeaders mustEqual Map("Content-type" -> Seq("application/pdf"))
    }

    "add a header" in {
      script(
        """<?php
          |header("Content-type: application/pdf");
          |header("Content-type: application/text", false);
          |?>""".stripMargin
      ).result.httpResponseContext.httpResponseHeaders mustEqual Map("Content-type" -> Seq("application/text", "application/pdf"))
    }

    "default replace a header" in {
      script(
        """<?php
          |header("Content-type: application/pdf");
          |header("Content-type: application/text");
          |?>""".stripMargin
      ).result.httpResponseContext.httpResponseHeaders mustEqual Map("Content-type" -> Seq("application/text"))
    }

    "replace a header" in {
      script(
        """<?php
          |header("Content-type: application/pdf");
          |header("Content-type: application/text", true);
          |?>""".stripMargin
      ).result.httpResponseContext.httpResponseHeaders mustEqual Map("Content-type" -> Seq("application/text"))
    }

    "be called before any actual output is sent" in  {
      // from http://www.php.net/manual/en/function.header.php
      script(
        """<?php
          |/* This will give an error. Note the output
          | * above, which is before the header() call */
          |header("Location: http://www.example.com/");
          |exit;
          |?>""".stripMargin
      ).result must haveThrown(classOf[ParseJbjException])
    }.pendingUntilFixed

    "set the response code" in {
      val httpResponse = script(
        """<?php
          |header("Last-Modified: Sat, 01 Jul 2006 01:50:55 UTC", true, 304);
          |?>""".stripMargin
      ).result.httpResponseContext
      httpResponse.httpStatus mustEqual 304
    }

    "send the http response code" in {
      val httpResponse = script(
        """<?php
          |header("HTTP/1.0 404 Not Found");
          |?>""".stripMargin
      ).result.httpResponseContext
      httpResponse.httpStatus mustEqual 404
      httpResponse.httpStatusMessage mustEqual "Not Found"
    }

    "send a redirect default with 302" in {
      val httpResponse = script(
        """<?php
          |header('Location:http://www.example.com/');
          |?>""".stripMargin
      ).result.httpResponseContext
      httpResponse.httpStatus mustEqual 302
      httpResponse.httpResponseHeaders mustEqual Map("Location" -> Seq("http://www.example.com/"))
    }

    "send a redirect" in {
      val httpResponse = script(
        """<?php
          |http_response_code(301);
          |header("Location: http://www.example.com/");
          |?>""".stripMargin
      ).result.httpResponseContext
      httpResponse.httpStatus mustEqual 301
      httpResponse.httpResponseHeaders mustEqual Map("Location" -> Seq("http://www.example.com/"))
    }

  }
}
