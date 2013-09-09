/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.runtime.env

import java.net.URLDecoder
import de.leanovate.jbj.runtime.value._
import scala.annotation.tailrec
import de.leanovate.jbj.runtime.context.Context
import scala.collection.JavaConversions._
import de.leanovate.jbj.api.http.{MultipartFormRequestBody, FormRequestBody, RequestInfo}
import de.leanovate.jbj.runtime.NoNodePosition
import scala.Some
import de.leanovate.jbj.runtime.value.IntegerVal

object CgiEnvironment {
  val numberPattern = "([0-9]+)".r

  implicit val position = NoNodePosition

  def httpRequest(request: RequestInfo)(implicit ctx: Context) {
    val serverArgv = ArrayVal(URLDecoder.decode(request.getRawQuery, "UTF-8").split(" ").map {
      str => None -> StringVal(str)
    }: _*)
    ctx.defineVariable("_SERVER", PVar(ArrayVal(
      Some(StringVal("PHP_SELF")) -> StringVal(request.getUri),
      Some(StringVal("argv")) -> serverArgv,
      Some(StringVal("argc")) -> serverArgv.count,
      Some(StringVal("REQUEST_METHOD")) -> StringVal(request.getMethod.toString),
      Some(StringVal("QUERY_STRING")) -> StringVal(request.getRawQuery)
    )))

    val queryKeyValues = request.getQuery.toSeq.flatMap {
      case (key, values) => values.map(key -> _)
    }
    val getRequestArray = decodeKeyValues(queryKeyValues)
    ctx.defineVariable("_GET", PVar(getRequestArray))
    ctx.defineVariable("_COOKIE", PVar(ArrayVal(request.getCookies.map {
      cookie =>
        Some(StringVal(cookie.getName)) -> StringVal(cookie.getValue)
    }: _*)))
    request.getMethod match {
      case RequestInfo.Method.GET =>
        ctx.defineVariable("_REQUEST", PVar(getRequestArray.copy))
      case RequestInfo.Method.POST =>
        var error = false
        if (ctx.settings.isAlwaysPopulateRawPostData) {
          val streams = LimitedByteStreams(ctx.settings.getPostMaxSize)

          streams.read(request.getBody.getContent) match {
            case Left(data) =>
              ctx.defineVariable("HTTP_RAW_POST_DATA", PVar(new StringVal(data)))
            case Right(size) =>
              ctx.log.warn("Unknown: POST Content-Length of %d bytes exceeds the limit of %d bytes".format(size, ctx.settings.getPostMaxSize))
              error = true
          }
        }
        if (error) {
          ctx.log.warn("Cannot modify header information - headers already sent")
          ctx.defineVariable("_POST", PVar(ArrayVal()))
          ctx.defineVariable("_REQUEST", PVar(getRequestArray.copy))
        } else {
          Option(request.getBody) match {
            case Some(formBody: FormRequestBody) =>
              val formKeyValues = formBody.getFormData.toSeq.flatMap {
                case (key, values) => values.map(key -> _)
              }
              val postRequestArray = decodeKeyValues(formKeyValues)
              ctx.defineVariable("_POST", PVar(postRequestArray))
              ctx.defineVariable("_REQUEST", PVar(postRequestArray.copy))
            case Some(multipart: MultipartFormRequestBody) =>
              val files = multipart.getFileData.map {
                fileData =>
                  Some(StringVal(fileData.getKey)) -> ArrayVal(
                    Some(StringVal("name")) -> StringVal(fileData.getFilename),
                    Some(StringVal("type")) -> StringVal(fileData.getContentType),
                    Some(StringVal("tmp_name")) -> StringVal(fileData.getTempfilePath),
                    Some(StringVal("error")) -> IntegerVal(0),
                    Some(StringVal("size")) -> IntegerVal(fileData.getSize)
                  )
              }
              val formKeyValues = multipart.getFormData.toSeq.flatMap {
                case (key, values) => values.map(key -> _)
              }
              val postRequestArray = decodeKeyValues(formKeyValues)
              ctx.defineVariable("_FILES", PVar(ArrayVal(files: _*)))
              ctx.defineVariable("_POST", PVar(postRequestArray))
              ctx.defineVariable("_REQUEST", PVar(postRequestArray.copy))
            case Some(body) =>
              if (!ctx.settings.isAlwaysPopulateRawPostData) {
                val streams = LimitedByteStreams(ctx.settings.getPostMaxSize)

                streams.read(body.getContent) match {
                  case Left(data) =>
                    ctx.defineVariable("HTTP_RAW_POST_DATA", PVar(new StringVal(data)))
                  case Right(size) =>
                    ctx.log.warn("Unknown: POST Content-Length of %d bytes exceeds the limit of %d bytes".format(size, ctx.settings.getPostMaxSize))
                }
              }
              ctx.defineVariable("_POST", PVar(ArrayVal()))
              ctx.defineVariable("_REQUEST", PVar(getRequestArray.copy))
            case _ =>
              ctx.defineVariable("_REQUEST", PVar(getRequestArray.copy))
          }
        }
    }
  }

  def decodeKeyValues(keyValues: Seq[(String, String)])(implicit ctx: Context) = {
    val result = ArrayVal()
    keyValues.foreach {
      case (key, value) =>
        val indices = extractIndices(key).toList
        if (ctx.settings.getMaxInputNestingLevel < 0 || indices.size < ctx.settings.getMaxInputNestingLevel) {
          assign(result, indices, StringVal(value))
        } else {
          ctx.log.warn("Unknown: Input variable nesting level exceeded %d. To increase the limit change max_input_nesting_level in php.ini.".
            format(ctx.settings.getMaxInputNestingLevel))
        }
    }
    result
  }

  @tailrec
  private def assign(array: ArrayVal, indices: List[Option[PVal]], value: PVal)(implicit ctx: Context) {
    indices match {
      case Some(idx) :: Nil => array.setAt(idx, value)
      case None :: Nil => array.setAt(array.size, value)
      case Some(idx) :: tail =>
        val subArray = array.getAt(idx).getOrElse(ArrayVal()).asVal.toArray
        array.setAt(idx, subArray)
        assign(subArray, tail, value)
      case None :: tail =>
        val subArray = ArrayVal()
        array.setAt(array.size, subArray)
        assign(subArray, tail, value)
    }
  }

  private def extractIndices(key: String)(implicit ctx: Context): Iterator[Option[PVal]] = {
    val idxStart = key.indexOf('[')

    Iterator.single {
      if (idxStart >= 0) {
        Some(StringVal(key.substring(0, idxStart)))
      } else {
        Some(StringVal(key))
      }
    } ++ new Iterator[Option[PVal]] {
      var pos = idxStart

      def hasNext = pos >= 0 && pos < key.length && key.charAt(pos) == '['

      def next() = {
        val idxStart = key.indexOf('[', pos)
        val idxEnd = key.indexOf(']', idxStart)

        if (idxEnd >= 0)
          pos = idxEnd + 1
        else
          pos = key.length
        key.substring(idxStart + 1, idxEnd) match {
          case "" =>
            None
          case numberPattern(str) =>
            Some(IntegerVal(str.toLong))
          case str =>
            Some(StringVal(str))
        }
      }
    }
  }
}
