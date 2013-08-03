package de.leanovate.jbj.runtime.env

import de.leanovate.jbj.runtime.{IntArrayKey, ValueRef, StringArrayKey, Context}
import java.net.{URLDecoder, URI}
import de.leanovate.jbj.runtime.value.{StringVal, ArrayVal}
import de.leanovate.jbj.ast.NoNodePosition

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
      Some(StringVal("argv")) -> serverArgv,
      Some(StringVal("argc")) -> serverArgv.count,
      Some(StringVal("REQUEST_METHOD")) -> StringVal("GET"),
      Some(StringVal("QUERY_STRING")) -> StringVal(queryString.getOrElse(""))
    )))

    ctx.defineVariable("_GET", ValueRef(decodeFormData(queryString.getOrElse(""))(ctx)))
  }

  def httpPostForm(uriStr: String, formData: String, ctx: Context) {
    val uri = new URI(uriStr)
    val queryString = Option(uri.getQuery)
    val serverArgv = ArrayVal(URLDecoder.decode(queryString.getOrElse(""), "UTF-8").split(" ").map {
      str => None -> StringVal(str)
    }: _*)
    ctx.defineVariable("_SERVER", ValueRef(ArrayVal(
      Some(StringVal("argv")) -> serverArgv,
      Some(StringVal("argc")) -> serverArgv.count,
      Some(StringVal("REQUEST_METHOD")) -> StringVal("POST"),
      Some(StringVal("QUERY_STRING")) -> StringVal(queryString.getOrElse(""))
    )))

    ctx.defineVariable("_POST", ValueRef(decodeFormData(formData)(ctx)))
  }

  private def decodeFormData(formData: String)(implicit ctx: Context) = {
    val result = ArrayVal()
    var count = 0L
    formData.split("&").foreach {
      param =>
        val eqIdx = param.indexOf('=')

        if (eqIdx > 0) {
          val key = URLDecoder.decode(param.substring(0, eqIdx), "UTF-8")
          val value = URLDecoder.decode(param.substring(eqIdx + 1), "UTF-8")
          val arrayIdx = key.indexOf('[')

          if (arrayIdx < 0) {
            result.setAt(Some(StringArrayKey(key)), StringVal(value))
          } else {
            val arrayKey = key.substring(0, arrayIdx)
            var array = result.getAt(StringArrayKey(arrayKey))

            if (array.isUndefined) {
              array = ArrayVal()
              result.setAt(Some(StringArrayKey(arrayKey)), array)
            }
            val arrayIdx2 = key.indexOf(']', arrayIdx)
            key.substring(arrayIdx + 1, arrayIdx2) match {
              case "" =>
                array.setAt(Some(IntArrayKey(count)), StringVal(value))
                count += 1
              case numberPattern(str) =>
                array.setAt(Some(IntArrayKey(str.toLong)), StringVal(value))
              case str =>
                array.setAt(Some(StringArrayKey(str)), StringVal(value))
            }
          }
        }
    }
    result
  }
}
