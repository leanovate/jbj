package de.leanovate.jbj.core.tests

import de.leanovate.jbj.api.http.CookieInfo

case class TestCookieInfo(name: String, value: String, maxAge: Option[Int] = None, path: String = "/", domain: Option[String] = None, secure: Boolean = false) extends CookieInfo {
  def getName = name

  def getValue = value

  def getMaxAge = maxAge.map(new Integer(_)).orNull

  def getPath = path

  def getDomain = domain.orNull

  def isSecure = secure
}
