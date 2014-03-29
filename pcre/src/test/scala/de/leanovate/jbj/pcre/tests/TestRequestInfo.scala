package de.leanovate.jbj.pcre.tests

import de.leanovate.jbj.api.http._
import java.net.{URLDecoder, URI}
import java.io.ByteArrayInputStream
import java.util
import scala.collection.JavaConversions._

class TestRequestInfo(method: RequestInfo.Method, uri: String, cookies: Seq[CookieInfo], body: RequestBody) extends RequestInfo {
  val rawQueryString = Option(new URI(uri).getQuery).getOrElse("")

  def getMethod = method

  def getUri = new URI(uri)

  def getQuery = TestRequestInfo.parseFormData(rawQueryString)

  def getRawQuery = rawQueryString

  def getCookies = cookies

  def getBody = body
}

class TestRequestBody(contentType: String, content: String) extends RequestBody {
  def getContentType = contentType

  def getContent = new ByteArrayInputStream(content.getBytes("UTF-8"))
}

class TestFormRequestBody(formData: String) extends FormRequestBody {
  def getContentType = "application/form-url-encoded"

  def getFormData = TestRequestInfo.parseFormData(formData)

  def getContent = new ByteArrayInputStream(formData.getBytes("UTF-8"))
}

object TestRequestInfo {
  val formDataNoQuotes = """form-data;[ ]*name=(.*)""".r
  val formDataDispositionSQuote = """form-data;[ ]*name=[']((\\'|[^'])*)['].*""".r
  val formDataDispositionDQuote = """form-data;[ ]*name=["]((\\"|[^"])*)["].*""".r

  def get(uri: String, cookies: Seq[CookieInfo]): RequestInfo = {
    new TestRequestInfo(RequestInfo.Method.GET, uri, cookies, null)
  }

  def parseFormData(formData: String) = {
    val result = new java.util.LinkedHashMap[String, java.util.List[String]]
    formData.split("&").foreach {
      param =>
        val eqIdx = param.indexOf('=')

        if (eqIdx > 0) {
          val key = URLDecoder.decode(param.substring(0, eqIdx), "UTF-8")
          val value = URLDecoder.decode(param.substring(eqIdx + 1), "UTF-8")

          val values = Option(result.get(key)).getOrElse {
            val newValues = new util.ArrayList[String]()
            result.put(key, newValues)
            newValues
          }
          values.add(value)
        }
    }
    result
  }
}
