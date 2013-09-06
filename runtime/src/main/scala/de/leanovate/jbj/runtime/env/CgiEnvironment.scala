/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.runtime.env

import java.net.URLDecoder
import de.leanovate.jbj.runtime.value._
import de.leanovate.jbj.core.ast.NoNodePosition
import scala.annotation.tailrec
import de.leanovate.jbj.runtime.context.Context
import scala.Some
import de.leanovate.jbj.runtime.value.IntegerVal
import scala.collection.JavaConversions._
import de.leanovate.jbj.api.http.{FormRequestBody, RequestInfo}

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
    request.getMethod match {
      case RequestInfo.Method.GET =>
        ctx.defineVariable("_REQUEST", PVar(getRequestArray.copy))
      case RequestInfo.Method.POST =>
        Option(request.getBody) match {
          case Some(formBody: FormRequestBody) =>
            val formKeyValues = formBody.getFormData.toSeq.flatMap {
              case (key, values) => values.map(key -> _)
            }
            val postRequestArray = decodeKeyValues(formKeyValues)
            ctx.defineVariable("_POST", PVar(postRequestArray))
            ctx.defineVariable("_REQUEST", PVar(postRequestArray.copy))
          case _ =>
            ctx.defineVariable("_REQUEST", PVar(getRequestArray.copy))
        }
    }
  }

  def decodeKeyValues(keyValues: Seq[(String, String)])(implicit ctx: Context) = {
    val result = ArrayVal()
    keyValues.foreach {
      case (key, value) =>
        assign(result, extractIndices(key).toList, StringVal(value))
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
    Iterator.single {
      val idxStart = key.indexOf('[')

      if (idxStart >= 0) {
        Some(StringVal(key.substring(0, idxStart)))
      } else {
        Some(StringVal(key))
      }
    } ++ new Iterator[Option[PVal]] {
      var pos = 0

      def hasNext = key.indexOf('[', pos) >= 0

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
