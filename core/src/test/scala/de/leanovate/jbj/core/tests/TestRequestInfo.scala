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
import org.apache.commons.fileupload.{FileUploadBase, FileUpload}
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

class TestRequestBody(contentType: String, content: String) extends RequestBody {
  def getContentType = contentType

  def getContent = new ByteArrayInputStream(content.getBytes("UTF-8"))
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
        case TestRequestInfo.formDataDispositionSQuote(name, _) => name.replace("\\\\", "\\").replace("\\'", "'")
        case TestRequestInfo.formDataDispositionDQuote(name, _) => name.replace("\\\\", "\\").replace("\\\"", "\"")
        case TestRequestInfo.formDataNoQuotes(name) => name.replace("\\\\", "\\")
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
      val key = item.getHeaders.getHeader("Content-Disposition") match {
        case TestRequestInfo.formDataDispositionSQuote(name, _) => name.replace("\\\\", "\\").replace("\\'", "'")
        case TestRequestInfo.formDataDispositionDQuote(name, _) => name.replace("\\\\", "\\").replace("\\\"", "\"")
        case TestRequestInfo.formDataNoQuotes(name) => name.replace("\\\\", "\\")
      }
      val values = Option(formData.get(key)).getOrElse {
        val newValues = new util.ArrayList[String]()
        formData.put(key, newValues)
        newValues
      }
      values.add(item.getString)
  }

  def getFormData = formData

  def getFileData = fileData

  def getContentType = contentType

  def getContent = new ByteArrayInputStream(content.getBytes("UTF-8"))
}

object TestRequestInfo {
  val formDataNoQuotes = """form-data;[ ]*name=(.*)""".r
  val formDataDispositionSQuote = """form-data;[ ]*name=[']((\\'|[^'])*)['].*""".r
  val formDataDispositionDQuote = """form-data;[ ]*name=["]((\\"|[^"])*)["].*""".r

  def get(uri: String, cookies: Seq[CookieInfo]): RequestInfo = {
    new TestRequestInfo(RequestInfo.Method.GET, uri, cookies, null)
  }

  def post(uri: String, contentType: String, content: String, cookies: Seq[CookieInfo]): RequestInfo = {
    val body = contentType match {
      case "application/form-url-encoded" =>
        new TestFormRequestBody(content)
      case ct if FileUploadBase.isMultipartContent(TestRequestContext(contentType, content)) =>
        new TestMultipartFormRequestBody(contentType, content)
      case _ =>
        new TestRequestBody(contentType, content)
    }
    new TestRequestInfo(RequestInfo.Method.POST, uri, cookies, body)
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