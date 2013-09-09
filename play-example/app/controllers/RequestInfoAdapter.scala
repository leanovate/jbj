package controllers

import play.api.mvc._
import java.util
import scala.collection.JavaConversions._
import de.leanovate.jbj.api.http.{CookieInfo, FormRequestBody, RequestInfo}
import play.api.mvc.AnyContentAsFormUrlEncoded
import java.net.{URLEncoder, URLDecoder}
import java.io.ByteArrayInputStream

case class RequestInfoAdapter(request: Request[AnyContent]) extends RequestInfo {
  def getMethod = RequestInfo.Method.valueOf(request.method.toUpperCase)

  def getUri = request.uri

  def getQuery = {
    val result = new util.LinkedHashMap[String, util.List[String]]()

    request.queryString.foreach {
      case (key, values) =>
        result.put(key, values)
    }
    result
  }

  def getRawQuery = request.rawQueryString

  def getCookies = request.headers.get("set-cookie").map(Cookies.decode).getOrElse(Seq.empty).map(CookieInfoAdapter.apply)

  def getBody = request.body match {
    case AnyContentAsFormUrlEncoded(data) =>
      new FormRequestBody {
        def getContentType = request.contentType.getOrElse("")

        def getContent = new ByteArrayInputStream(data.map {
          case (key, values) =>
            values.map {
              value =>
                URLEncoder.encode(key, "UTF-8") + "=" + URLEncoder.encode(value, "UTF-8")
            }.mkString("&")
        }.mkString("&").getBytes("UTF-8"))

        def getFormData = {
          val result = new util.LinkedHashMap[String, util.List[String]]()

          data.foreach {
            case (key, values) =>
              result.put(key, values)
          }
          result
        }
      }
    case _ =>
      null
  }

  case class CookieInfoAdapter(cookie: Cookie) extends CookieInfo {
    def getName = cookie.name

    def getValue = cookie.value

    def getMaxAge = cookie.maxAge.map(new Integer(_)).orNull

    def getPath = cookie.path

    def getDomain = cookie.domain.orNull

    def isSecure = cookie.secure
  }

}

