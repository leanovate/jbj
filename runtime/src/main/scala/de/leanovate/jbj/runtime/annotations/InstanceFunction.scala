package de.leanovate.jbj.runtime.annotations

import scala.annotation.StaticAnnotation

case class InstanceFunction(actualName: Option[String] = None) extends StaticAnnotation {

}