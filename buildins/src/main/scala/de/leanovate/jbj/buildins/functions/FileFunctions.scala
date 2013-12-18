/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.buildins.functions

import de.leanovate.jbj.runtime.annotations.GlobalFunction
import de.leanovate.jbj.runtime.context.Context
import java.nio.file.Files

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
