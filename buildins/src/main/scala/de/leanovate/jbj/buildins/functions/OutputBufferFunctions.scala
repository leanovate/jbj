/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.buildins.functions

import de.leanovate.jbj.runtime.value._
import de.leanovate.jbj.runtime.context.Context
import de.leanovate.jbj.runtime.output.OutputTransformer
import de.leanovate.jbj.runtime.exception.FatalErrorJbjException
import de.leanovate.jbj.runtime.annotations.GlobalFunction
import scala.Some
import de.leanovate.jbj.runtime.value.IntegerVal
import de.leanovate.jbj.runtime.CallbackHelper

object OutputBufferFunctions {
  @GlobalFunction
  def ob_start(callback: Option[PVal], size: Option[Int], erase: Option[Boolean])(implicit ctx: Context): Boolean = {
    if (callback.isDefined) {
      if (CallbackHelper.isValidCallback(callback.get)) {
        val outputTransformer = new OutputTransformer {
          def name = CallbackHelper.callbackName(callback.get).getOrElse("default output handler")

          def transform(flags: Int, bytes: Array[Byte], offset: Int, length: Int) = {
            try {
              ctx.global.isOutputBufferingCallback = true
              CallbackHelper.callCallback(callback.get,
                new StringVal(bytes.slice(offset, offset + length)), IntegerVal(flags)).asVal.toStr.chars
            } finally {
              ctx.global.isOutputBufferingCallback = false
            }
          }
        }
        ctx.out.bufferStart(Some(outputTransformer), size.getOrElse(0))
      } else
        false
    } else
      ctx.out.bufferStart(None, size.getOrElse(0))
  }

  @GlobalFunction
  def ob_flush()(implicit ctx: Context) {
    ctx.out.bufferFlush()
  }

  @GlobalFunction
  def ob_clean()(implicit ctx: Context): Boolean = {
    if (!ctx.out.bufferClean()) {
      ctx.log.notice("ob_clean(): failed to delete buffer. No buffer to delete")
      false
    } else
      true
  }

  @GlobalFunction
  def ob_end_clean()(implicit ctx: Context): Boolean = {
    if (!ctx.out.bufferEndClean()) {
      ctx.log.notice("ob_end_clean(): failed to delete buffer. No buffer to delete")
      false
    } else {
      true
    }
  }

  @GlobalFunction
  def ob_end_flush()(implicit ctx: Context): Boolean = {
    if (!ctx.out.bufferEndFlush()) {
      ctx.log.notice("ob_end_clean(): failed to delete buffer. No buffer to delete")
      false
    } else {
      true
    }
  }

  @GlobalFunction
  def ob_get_level()(implicit ctx: Context): Int = {
    ctx.out.bufferLevel
  }

  @GlobalFunction
  def ob_get_contents()(implicit ctx: Context): PVal = {
    ctx.out.bufferContents.map(new StringVal(_)).getOrElse(BooleanVal.FALSE)
  }

  @GlobalFunction
  def ob_get_clean()(implicit ctx: Context): PVal = {
    ctx.out.bufferContents.map {
      contents =>
        ctx.out.bufferEndClean()
        StringVal(contents)
    }.getOrElse(BooleanVal.FALSE)
  }

  @GlobalFunction
  def ob_get_flush()(implicit ctx: Context): PVal = {
    if (ctx.global.isOutputBufferingCallback)
      throw new FatalErrorJbjException("ob_get_flush(): Cannot use output buffering in output buffering display handlers")
    val result = ob_get_contents()
    ob_end_flush()
    result
  }

  @GlobalFunction
  def ob_list_handlers()(implicit ctx: Context): PVal = {
    ArrayVal(
      ctx.out.bufferStack.reverse.map {
        outputHandler =>
          None -> StringVal(outputHandler.name.getOrElse("default output handler").getBytes(ctx.settings.getCharset))
      }: _*
    )
  }

  @GlobalFunction
  def ob_get_status(fullStatus: Option[Boolean])(implicit ctx: Context): PVal = {
    val stack = ctx.out.bufferStack

    if (stack.isEmpty) {
      ArrayVal()
    } else if (fullStatus.getOrElse(false)) {
      ArrayVal(
        stack.reverse.map {
          outputHandler =>
            None -> ArrayVal(
              Some(StringVal("name")) -> StringVal(outputHandler.name.getOrElse("default output handler")),
              Some(StringVal("type")) -> IntegerVal(outputHandler.bufferType),
              Some(StringVal("flags")) -> IntegerVal(outputHandler.bufferFlags),
              Some(StringVal("level")) -> IntegerVal(outputHandler.level),
              Some(StringVal("chunk_size")) -> IntegerVal(outputHandler.bufferChunkSize),
              Some(StringVal("buffer_size")) -> IntegerVal(outputHandler.bufferSize),
              Some(StringVal("buffer_used")) -> IntegerVal(outputHandler.bufferUsed)
            )

        }: _*)

    } else {
      val head = stack.head
      ArrayVal(
        Some(StringVal("name")) -> StringVal(head.name.getOrElse("default output handler")),
        Some(StringVal("type")) -> IntegerVal(head.bufferType),
        Some(StringVal("flags")) -> IntegerVal(head.bufferFlags),
        Some(StringVal("level")) -> IntegerVal(head.level),
        Some(StringVal("chunk_size")) -> IntegerVal(head.bufferChunkSize),
        Some(StringVal("buffer_size")) -> IntegerVal(head.bufferSize),
        Some(StringVal("buffer_used")) -> IntegerVal(head.bufferUsed)
      )
    }
  }
}
