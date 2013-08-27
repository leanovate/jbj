package de.leanovate.jbj.core.runtime.buildin

import de.leanovate.jbj.core.runtime.value.{BooleanVal, StringVal, PVal}
import de.leanovate.jbj.core.runtime.context.Context
import de.leanovate.jbj.core.runtime.annotations.GlobalFunction
import de.leanovate.jbj.core.runtime.output.OutputTransformer
import de.leanovate.jbj.core.runtime.exception.FatalErrorJbjException

object OutputBufferFunctions extends WrappedFunctions {
  @GlobalFunction
  def ob_start(callback: Option[PVal], size: Option[Int], erase: Option[Boolean])(implicit ctx: Context): Boolean = {
    if (callback.isDefined) {
      if (CallbackHelper.isValidCallback(callback.get)) {
        val outputTransformer = new OutputTransformer {
          def transform(bytes: Array[Byte], offset: Int, length: Int) = {
            try {
              ctx.global.isOutputBufferingCallback = true
              CallbackHelper.callCallabck(callback.get, new StringVal(bytes)).asVal.toStr.chars
            } finally {
              ctx.global.isOutputBufferingCallback = false
            }
          }
        }
        ctx.out.bufferStart(Some(outputTransformer), size.getOrElse(ctx.settings.getOutputBuffering))
      } else
        false
    } else
      ctx.out.bufferStart(None, size.getOrElse(ctx.settings.getOutputBuffering))
  }

  @GlobalFunction
  def ob_flush()(implicit ctx: Context) {
    ctx.out.bufferFlush()
  }

  @GlobalFunction
  def ob_clean()(implicit ctx: Context) {
    ctx.out.bufferClean()
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
    val result = ob_get_contents()
    ob_end_clean()
    result
  }

  @GlobalFunction
  def ob_get_flush()(implicit ctx: Context): PVal = {
    if (ctx.global.isOutputBufferingCallback)
      throw new FatalErrorJbjException("ob_get_flush(): Cannot use output buffering in output buffering display handlers")
    val result = ob_get_contents()
    ob_end_flush()
    result
  }
}
