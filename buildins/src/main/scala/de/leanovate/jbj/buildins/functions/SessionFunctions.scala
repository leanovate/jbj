package de.leanovate.jbj.buildins.functions

import de.leanovate.jbj.runtime.annotations.GlobalFunction
import de.leanovate.jbj.runtime.context.Context

object SessionFunctions {
  @GlobalFunction
  def session_id(idOpt: Option[String])(implicit ctx: Context): String = {
    idOpt.map {
      id =>
        ctx.global.session.id
    }.getOrElse {
      ctx.global.session.id
    }
  }

  @GlobalFunction
  def session_save_path(path: Option[String])(implicit ctx: Context): String = {
    path.foreach {
      path =>
        ctx.global.session.sessionSavePath = path
    }
    ctx.global.session.sessionSavePath
  }
}
