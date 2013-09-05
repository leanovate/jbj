/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.runtime.output

trait OutputHandler {
  /* standard passthru */
  val PHP_OUTPUT_HANDLER_WRITE = 0x00
  /* start */
  val PHP_OUTPUT_HANDLER_START = 0x01
  /* restart */
  val PHP_OUTPUT_HANDLER_CLEAN = 0x02
  /* pass along as much as possible */
  val PHP_OUTPUT_HANDLER_FLUSH = 0x04
  /* finalize */
  val PHP_OUTPUT_HANDLER_FINAL = 0x08
  val PHP_OUTPUT_HANDLER_CONT = PHP_OUTPUT_HANDLER_WRITE
  val PHP_OUTPUT_HANDLER_END = PHP_OUTPUT_HANDLER_FINAL

  /* handler types */
  val PHP_OUTPUT_HANDLER_INTERNAL = 0x0000
  val PHP_OUTPUT_HANDLER_USER = 0x0001

  /* handler ability flags */
  val PHP_OUTPUT_HANDLER_CLEANABLE = 0x0010
  val PHP_OUTPUT_HANDLER_FLUSHABLE = 0x0020
  val PHP_OUTPUT_HANDLER_REMOVABLE = 0x0040
  val PHP_OUTPUT_HANDLER_STDFLAGS = 0x0070

  /* handler status flags */
  val PHP_OUTPUT_HANDLER_STARTED = 0x1000
  val PHP_OUTPUT_HANDLER_DISABLED = 0x2000
  val PHP_OUTPUT_HANDLER_PROCESSED = 0x4000

  def name: Option[String]

  def level: Int

  def bufferUsed: Int

  def bufferSize: Int

  def bufferType: Int

  def bufferFlags: Int

  def bufferChunkSize: Int

  def endClean()

  def endFlush()

  def clean()

  def contents: Option[Array[Byte]]

  def suspend() {}

  def resume() {}
}
