/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                 (Tests based on PHP)   */

package de.leanovate.jbj.core.tests

import org.apache.commons.fileupload.{FileItemHeaders, FileItemFactory, FileItem}
import java.io.{ByteArrayOutputStream, ByteArrayInputStream, File}
import org.apache.commons.fileupload.util.FileItemHeadersImpl

object TestFileItemFactory extends FileItemFactory {

  case class TestFileItem(var fieldName: String, contentType: String, var formField: Boolean, fileName: String) extends FileItem {
    var content: Array[Byte] = Array.empty
    var headers: FileItemHeaders = new FileItemHeadersImpl

    def getInputStream = new ByteArrayInputStream(content)

    def getContentType = contentType

    def getName = fileName

    def isInMemory = true

    def getSize = content.length

    def get() = content

    def getString(encoding: String) = new String(content, encoding)

    def getString = new String(content, "UTF-8")

    def write(file: File) {
      throw new RuntimeException("Do not write files in test")
    }

    def delete() {}

    def getFieldName = fieldName

    def setFieldName(name: String) {
      fieldName = name
    }

    def isFormField = formField

    def setFormField(state: Boolean) {
      formField = state
    }

    def getOutputStream = new ByteArrayOutputStream() {
      override def close() {
        content = toByteArray
      }
    }

    def getHeaders = headers

    def setHeaders(headers: FileItemHeaders) {
      this.headers = headers
    }
  }

  def createItem(fieldName: String, contentType: String, isFormField: Boolean, fileName: String) =
    TestFileItem(fileName, contentType, isFormField, fileName)
}
