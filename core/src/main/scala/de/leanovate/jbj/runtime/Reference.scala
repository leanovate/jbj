package de.leanovate.jbj.runtime

import de.leanovate.jbj.runtime.value.{PAny, PVal}

trait Reference {
  def asVar: PAny

  def assign(pAny: PAny): PAny

  def unset()
}
