package de.leanovate.jbj.runtime.env

import de.leanovate.jbj.runtime._
import java.net.{URLDecoder, URI}
import de.leanovate.jbj.runtime.value.{ValueRef, Value, StringVal, ArrayVal}
import de.leanovate.jbj.ast.NoNodePosition
import de.leanovate.jbj.runtime.IntArrayKey
import de.leanovate.jbj.runtime.StringArrayKey
import scala.Some
import scala.annotation.tailrec
import scala.collection.generic.Growable

object CgiEnvironment {
  val numberPattern = "([0-9]+)".r

  implicit val position = NoNodePosition

  def httpGet(uriStr: String, ctx: Context) {
    val uri = new URI(uriStr)
    val queryString = Option(uri.getQuery)
    val serverArgv = ArrayVal(URLDecoder.decode(queryString.getOrElse(""), "UTF-8").split(" ").map {
      str => None -> StringVal(str)
    }: _*)
    ctx.defineVariable("_SERVER", ValueRef(ArrayVal(
      Some(StringVal("PHP_SELF")) -> StringVal(uriStr),
      Some(StringVal("argv")) -> serverArgv,
      Some(StringVal("argc")) -> serverArgv.count,
      Some(StringVal("REQUEST_METHOD")) -> StringVal("GET"),
      Some(StringVal("QUERY_STRING")) -> StringVal(queryString.getOrElse(""))
    )))

    val requestArray = decodeFormData(queryString.getOrElse(""))(ctx)
    ctx.defineVariable("_GET", ValueRef(requestArray.copy))
    ctx.defineVariable("_REQUEST", ValueRef(requestArray.copy))
  }

  def httpPostForm(uriStr: String, formData: String, ctx: Context) {
    val uri = new URI(uriStr)
    val queryString = Option(uri.getQuery)
    val serverArgv = ArrayVal(URLDecoder.decode(queryString.getOrElse(""), "UTF-8").split(" ").map {
      str => None -> StringVal(str)
    }: _*)
    ctx.defineVariable("_SERVER", ValueRef(ArrayVal(
      Some(StringVal("PHP_SELF")) -> StringVal(uriStr),
      Some(StringVal("argv")) -> serverArgv,
      Some(StringVal("argc")) -> serverArgv.count,
      Some(StringVal("REQUEST_METHOD")) -> StringVal("POST"),
      Some(StringVal("QUERY_STRING")) -> StringVal(queryString.getOrElse(""))
    )))

    val requestArray = decodeFormData(formData)(ctx)
    ctx.defineVariable("_POST", ValueRef(requestArray.copy))
    ctx.defineVariable("_REQUEST", ValueRef(requestArray.copy))
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
  private def assign(array: ArrayVal, indices: List[Option[ArrayKey]], value: Value)(implicit ctx: Context) {
    indices match {
      case Some(idx) :: Nil => array.setAt(Some(idx), value)
      case None :: Nil => array.setAt(Some(IntArrayKey(array.keyValues.size)), value)
      case Some(idx) :: tail =>
        val subArray = array.getAt(idx).getOrElse(ArrayVal()).toArray
        array.setAt(Some(idx), subArray)
        assign(subArray, tail, value)
      case None :: tail =>
        val subArray = ArrayVal()
        array.setAt(Some(IntArrayKey(array.keyValues.size)), subArray)
        assign(subArray, tail, value)
    }
  }

  private def extractIndices(key: String): Iterator[Option[ArrayKey]] = {
    Iterator.single {
      val idxStart = key.indexOf('[')

      if (idxStart >= 0) {
        Some(StringArrayKey(key.substring(0, idxStart)))
      } else {
        Some(StringArrayKey(key))
      }
    } ++ new Iterator[Option[ArrayKey]] {
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
            Some(IntArrayKey(str.toLong))
          case str =>
            Some(StringArrayKey(str))
        }
      }
    }
  }

  private def decodeFormData(formData: String)(implicit ctx: Context) = {
    decodeKeyValues(formData.split("&").flatMap {
      param =>
        val eqIdx = param.indexOf('=')

        if (eqIdx > 0) {
          val key = URLDecoder.decode(param.substring(0, eqIdx), "UTF-8")
          val value = URLDecoder.decode(param.substring(eqIdx + 1), "UTF-8")

          Seq(key -> value)
        } else {
          Seq.empty
        }
    })
  }
}
