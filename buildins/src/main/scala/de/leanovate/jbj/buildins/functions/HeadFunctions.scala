package de.leanovate.jbj.buildins.functions

import de.leanovate.jbj.runtime.context.Context
import de.leanovate.jbj.runtime.annotations.GlobalFunction

object HeadFunctions {

  private[this] val httpHeader = """(?i)^HTTP/1.[01] (\d{3}) (.+)$""".r
  private[this] val redirect = """(?i)^Location:(.+)$""".r

  @GlobalFunction
  def http_response_code(newCode: Option[Int])(implicit context: Context): Int = {
    context.httpResponseContext.map { httpResponseContext =>
      newCode map { httpResponseContext.httpStatus = _ }
      httpResponseContext.httpStatus
    }.get
  }

  @GlobalFunction
  def header(input: String, replace: Option[Boolean], httpResponseCode: Option[Int])(implicit context: Context): Unit = {

    // TODO: do we have to write an error in header is use in a non-http context?
    context.httpResponseContext map { httpResponseContext =>

      httpResponseCode map { httpResponseContext.httpStatus = _ }

      input match {
        case httpHeader(status, message) =>
          httpResponseContext.httpStatus = status.toInt
          httpResponseContext.httpStatusMessage = message

        case redirect(location) =>
          // it returns a REDIRECT (302) status code to the browser unless the 201 or a 3xx status code has already been set.
          if (httpResponseContext.httpStatus != 201 && httpResponseContext.httpStatus / 100 != 3) {
            httpResponseContext.httpStatus = 302
          }
          httpResponseContext.httpResponseHeaders += "Location" -> Seq(location.trim)

        case _ => {
          val separatorPos = input.indexOf(':')
          val key = input.substring(0, separatorPos).trim
          val value = input.substring(separatorPos + 1).trim
          val headers = httpResponseContext.httpResponseHeaders
          val replaceHeader = replace.getOrElse(true)

          if (replaceHeader)
            headers += key -> Seq(value)
          else
            headers.get(key) match {
              case Some(values) => headers += key -> values.+:(value)
              case None => headers += key -> Seq(value)
            }
        }
      }
    }
  }

}
