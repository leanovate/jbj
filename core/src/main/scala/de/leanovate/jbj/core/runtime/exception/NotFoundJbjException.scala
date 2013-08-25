package de.leanovate.jbj.core.runtime.exception

import de.leanovate.jbj.api.JbjException

class NotFoundJbjException(fileName: String) extends JbjException("Not found: %s".format(fileName)) {

}
