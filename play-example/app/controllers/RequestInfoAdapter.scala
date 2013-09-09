package controllers

import play.api.mvc._
import java.util
import scala.collection.JavaConversions._
import de.leanovate.jbj.api.http.{CookieInfo, FormRequestBody, RequestInfo}
import play.api.mvc.AnyContentAsFormUrlEncoded

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

