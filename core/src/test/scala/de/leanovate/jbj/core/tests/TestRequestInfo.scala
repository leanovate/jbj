/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                 (Tests based on PHP)   */

/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                 (Tests based on PHP)   */

package de.leanovate.jbj.core.tests

import de.leanovate.jbj.api.{FormRequestBody, RequestBody, RequestInfo}
import java.net.{URLDecoder, URI}
import java.util

class TestRequestInfo(method: RequestInfo.Method, uri: String, body: RequestBody) extends RequestInfo {
  val rawQueryString = Option(new URI(uri).getQuery).getOrElse("")

  def getMethod = method

  def getUri = uri

  def getQuery = TestRequestInfo.parseFormData(rawQueryString)

  def getRawQuery = rawQueryString

  def getBody = body
}

class TestFormRequestBody(formData: String) extends FormRequestBody {
  def getContentType = "application/form-url-encoded"

  def getFormData = TestRequestInfo.parseFormData(formData)
}

object TestRequestInfo {
  def get(uri: String): RequestInfo = {
    new TestRequestInfo(RequestInfo.Method.GET, uri, null)
  }

  def post(uri: String, formData: String): RequestInfo = {
    new TestRequestInfo(RequestInfo.Method.POST, uri, new TestFormRequestBody(formData))
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