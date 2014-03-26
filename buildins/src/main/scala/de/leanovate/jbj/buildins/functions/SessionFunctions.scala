package de.leanovate.jbj.buildins.functions

import de.leanovate.jbj.runtime.annotations.GlobalFunction
import de.leanovate.jbj.runtime.context.Context
import de.leanovate.jbj.runtime.adapter.GlobalFunctions

trait SessionFunctions {
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

  @GlobalFunction
  def session_set_cookie_params(lifetime: Int, path: Option[String], domain: Option[String], secure: Option[Boolean],
                                httponly: Option[Boolean])(implicit ctx: Context) {
    ctx.settings.setSessionCookieLifetime(lifetime)
    path.foreach(ctx.settings.setSessionCookiePath)
    domain.foreach(ctx.settings.setSessionCookieDomain)
    secure.foreach(ctx.settings.setSessionCookieSecure)
    httponly.foreach(ctx.settings.setSessionCookieHttpOnly)
  }

  @GlobalFunction
  def session_start()(implicit ctx:Context):Boolean = ctx.global.session.start()
}

object SessionFunctions extends SessionFunctions {
  val functions = GlobalFunctions.generatePFunctions[SessionFunctions]
}