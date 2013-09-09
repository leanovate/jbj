/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                 (Tests based on PHP)   */

package de.leanovate.jbj.core.tests

import org.apache.commons.fileupload.RequestContext
import java.io.ByteArrayInputStream

case class TestRequestContext(contentType:String, content:String) extends RequestContext {
  def getCharacterEncoding = "UTF-8"

  def getContentType = contentType

  def getContentLength = content.getBytes("UTF-8").length

  def getInputStream = new ByteArrayInputStream(content.getBytes("UTF-8"))
}
