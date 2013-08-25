package controllers

import play.api.mvc.{AnyContentAsFormUrlEncoded, AnyContent, Request}
import de.leanovate.jbj.api.{FormRequestBody, RequestInfo}
import java.util
import scala.collection.JavaConversions._

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
}
