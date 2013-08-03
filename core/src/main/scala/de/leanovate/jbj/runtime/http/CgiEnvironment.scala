package de.leanovate.jbj.runtime.http

import de.leanovate.jbj.runtime.{ValueRef, StringArrayKey, Context}
import java.net.{URLDecoder, URLEncoder, URI}
import de.leanovate.jbj.runtime.value.{IntegerVal, StringVal, ArrayVal}

object CgiEnvironment {
  def httpGet(uriStr: String, ctx: Context) {
    val uri = new URI(uriStr)
    val serverArgv = ArrayVal(URLDecoder.decode(uri.getQuery, "UTF-8").split(" ").map {
      str => None -> StringVal(str)
    }: _*)
    ctx.defineVariable("_SERVER", ValueRef(ArrayVal(
      Some(StringVal("argv")) -> serverArgv,
      Some(StringVal("argc")) -> serverArgv.count,
      Some(StringVal("REQUEST_METHOD")) -> StringVal("GET"),
      Some(StringVal("QUERY_STRING")) -> StringVal(uri.getQuery)
    )))
  }

  def main(args: Array[String]) {
    val uri = new URI("?ab+cd+ef+123+test")
    println(uri.getQuery)
    println(uri.getRawQuery)
  }
}
