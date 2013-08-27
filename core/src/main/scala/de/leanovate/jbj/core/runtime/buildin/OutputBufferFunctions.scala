package de.leanovate.jbj.core.runtime.buildin

import de.leanovate.jbj.core.runtime.value.PVal
import de.leanovate.jbj.core.runtime.context.Context
import de.leanovate.jbj.core.runtime.annotations.GlobalFunction

object OutputBufferFunctions extends WrappedFunctions {
  @GlobalFunction
  def ob_start(callback: Option[PVal], size: Option[Int], erase: Option[Boolean])(implicit ctx: Context): Boolean = {
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
  def ob_end_flush()(implicit ctx:Context): Boolean = {
    if (!ctx.out.bufferEndFlush()) {
      ctx.log.notice("ob_end_clean(): failed to delete buffer. No buffer to delete")
      false
    } else {
      true
    }
  }

  @GlobalFunction
  def ob_get_level()(implicit ctx:Context): Int = {
    ctx.out.bufferLevel
  }
}
