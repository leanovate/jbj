package de.leanovate.jbj.runtime.context

import scala.util.Random
import java.security.SecureRandom
import java.nio.file.{Path, Files}

class Session(globalCtx: GlobalContext) {
  var id = Session.generateSessionId()

  var sessionSavePath = globalCtx.settings.getSessionSavePath

  var sessionFile: Option[Path] = None

  def start() {
    val sessionFilePath = globalCtx.filesystem.getPath(sessionSavePath, "sess_" + id)

    sessionFile = Some(Files.createFile(sessionFilePath))
  }
}

object Session {
  val random = new Random(SecureRandom.getInstance("SHA1PRNG"))

  def generateSessionId(): String = random.alphanumeric.take(26).mkString
}
