package de.leanovate.jbj.runtime.exception

import de.leanovate.jbj.runtime.value.PVal
import de.leanovate.jbj.api.http.JbjException

class WarnWithResultJbjException(message: String, val result: PVal) extends JbjException(message) {

}
