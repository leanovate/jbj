package de.leanovate.jbj.runtime.context

import scala.collection.mutable

trait HttpResponseContext {
  val httpResponseHeaders: mutable.Map[String, Seq[String]]
  var httpStatus: Int
  var httpStatusMessage: String

}
