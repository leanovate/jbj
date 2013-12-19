/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.buildins.functions

import de.leanovate.jbj.runtime.annotations.{ParameterMode, GlobalFunction}
import de.leanovate.jbj.runtime.context.Context
import java.nio.file.Files
import de.leanovate.jbj.runtime.value.{NullVal, StringVal, ResourceVal, PVal}

object FileFunctions {
  @GlobalFunction
  def basename(fileName: String): String = {
    val idx = fileName.lastIndexOf('/')
    if (idx >= 0)
      fileName.substring(idx + 1)
    else
      ""
  }

  @GlobalFunction
  def dirname(fileName: String): String = {
    val idx = fileName.lastIndexOf('/')
    if (idx >= 0)
      fileName.substring(0, idx)
    else
      ""
  }

  @GlobalFunction(parameterMode = ParameterMode.EXACTLY_WARN)
  def get_resource_type(param: PVal)(implicit ctx: Context): PVal = {
    param match {
      case ResourceVal(resourceType, _) => StringVal(resourceType)
      case _ =>
        ctx.log.warn(s"get_resource_type() expects parameter 1 to be resource, ${param.typeName(true)} given")
        NullVal
    }
  }

  @GlobalFunction
  def file_exists(fileName: String)(implicit ctx: Context): Boolean = {
    Files.exists(ctx.filesystem.getPath(fileName))
  }

  @GlobalFunction
  def unlink(fileName: String)(implicit ctx: Context): Boolean = {
    val path = ctx.filesystem.getPath(fileName)
    if (Files.exists(path)) {
      Files.delete(path)
      true
    } else
      false
  }
}
