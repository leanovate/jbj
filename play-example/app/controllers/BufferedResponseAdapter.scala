package controllers

import de.leanovate.jbj.api.http.Response
import play.api.http.{HeaderNames, Status}
import scala.collection.mutable
import java.io.ByteArrayOutputStream
import play.api.mvc.{Results, Result}
import play.api.libs.iteratee.Enumerator

case class BufferedResponseAdapter() extends Response {
  var statusCode: Int = Status.OK
  var staticMessage: String = "OK"
  val headers = mutable.Map.empty[String, String]
  val out = new ByteArrayOutputStream()

  headers.put(HeaderNames.CONTENT_TYPE, "text/html")

  def setStatus(code: Int, message: String) {
    statusCode = code
    staticMessage = message
  }

  def setHeader(name: String, value: String) {
    headers.put(name, value)
  }

  def getOutputStream = out

  def toResult: Result = {
    Results.Status(statusCode)(out.toByteArray).withHeaders(headers.toSeq: _*)
  }
}
