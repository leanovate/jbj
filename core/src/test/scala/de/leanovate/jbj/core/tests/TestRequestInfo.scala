/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                 (Tests based on PHP)   */

package de.leanovate.jbj.core.tests

import java.net.{URLDecoder, URI}
import java.util
import de.leanovate.jbj.api.http._
import org.apache.commons.fileupload.FileUpload
import scala.collection.JavaConversions._
import de.leanovate.jbj.api.http.MultipartFormRequestBody.FileData
import java.io.ByteArrayInputStream

class TestRequestInfo(method: RequestInfo.Method, uri: String, cookies: Seq[CookieInfo], body: RequestBody) extends RequestInfo {
  val rawQueryString = Option(new URI(uri).getQuery).getOrElse("")

  def getMethod = method

  def getUri = uri

  def getQuery = TestRequestInfo.parseFormData(rawQueryString)

  def getRawQuery = rawQueryString

  def getCookies = cookies

  def getBody = body
}

class TestFormRequestBody(formData: String) extends FormRequestBody {
  def getContentType = "application/form-url-encoded"

  def getFormData = TestRequestInfo.parseFormData(formData)

  def getContent = new ByteArrayInputStream(formData.getBytes("UTF-8"))
}

class TestMultipartFormRequestBody(contentType: String, content: String) extends MultipartFormRequestBody {
  val fileUpload = new FileUpload(TestFileItemFactory)
  val fileItems = fileUpload.parseParameterMap(TestRequestContext(contentType, content))
  val fileData = fileItems.values().flatten.filter(!_.isFormField).map {
    item =>
      val key = item.getHeaders.getHeader("Content-Disposition") match {
        case TestRequestInfo.formDataDisposition(key) => key
        case _ => item.getFieldName
      }
      new FileData {
        def getFilename = item.getName

        def getTempfilePath = "/tmp/something"

        def getKey = key

        def getContentType = item.getContentType

        def getSize = item.get().length
      }
  }.toList
  val formData = new java.util.LinkedHashMap[String, java.util.List[String]]

  fileItems.values().flatten.filter(_.isFormField).map {
    case item if item.getName == null =>
      item.getHeaders.getHeader("Content-Disposition") match {
        case TestRequestInfo.formDataDisposition(key) =>
          val values = Option(formData.get(key)).getOrElse {
            val newValues = new util.ArrayList[String]()
            formData.put(key, newValues)
            newValues
          }
          values.add(item.getString)
      }
  }

  def getFormData = formData

  def getFileData = fileData

  def getContentType = contentType

  def getContent = new ByteArrayInputStream(content.getBytes("UTF-8"))
}

object TestRequestInfo {
  val formDataDisposition = """form-data;[ ]*name=["]?([^"]*)["]?.*""".r

  def get(uri: String, cookies: Seq[CookieInfo]): RequestInfo = {
    new TestRequestInfo(RequestInfo.Method.GET, uri, cookies, null)
  }

  def post(uri: String, formData: String, cookies: Seq[CookieInfo]): RequestInfo = {
    new TestRequestInfo(RequestInfo.Method.POST, uri, cookies, new TestFormRequestBody(formData))
  }

  def post(uri: String, multipartContentType: String, multipartContent: String, cookies: Seq[CookieInfo]) = {
    new TestRequestInfo(RequestInfo.Method.POST, uri, cookies, new TestMultipartFormRequestBody(multipartContentType, multipartContent))
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