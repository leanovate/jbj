package de.leanovate.jbj.converter

import de.leanovate.jbj.core.JbjEnvironmentBuilder
import java.io.ByteArrayOutputStream

object TT extends App {
  val jbj = JbjEnvironmentBuilder().build()
  implicit val context = jbj.newGlobalContext(System.out, None)

  testunits.hello_world3.exec

  context.cleanup()
}
