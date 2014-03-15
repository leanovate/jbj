package de.leanovate.jbj.buildins

import org.specs2.mutable.SpecificationWithJUnit
import de.leanovate.jbj.buildins.functions.ArrayFunctions

class StandardExtensionSpec extends SpecificationWithJUnit {
  "BuilinsExtensions" should {
    "Contain function list" in {
      val functionNames = StandardExtension.functions.map(_.name.toString.toLowerCase).toSet

      functionNames must containAllOf(Seq("mktime", "time"))
      functionNames must containAllOf(Seq("count", "array_flip", "array_walk"))
      functionNames must containAllOf(Seq("class_exists", "interface_exists", "get_class"))
      functionNames must containAllOf(Seq("dirname"))
      functionNames must containAllOf(Seq("ob_start", "ob_flush"))
      functionNames must containAllOf(Seq("var_dump", "var_export", "print_r"))
      functionNames must containAllOf(Seq("error_reporting", "set_error_handler", "register_shutdown_function"))
      functionNames must containAllOf(Seq("bin2hex", "sprintf", "strlen"))
      functionNames must containAllOf(Seq("bin2hex", "sprintf", "strlen"))

    }
  }

}
