/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.buildins.functions

import de.leanovate.jbj.runtime.annotations.ParameterMode
import de.leanovate.jbj.runtime.context.Context
import java.nio.file.{NoSuchFileException, StandardOpenOption, Files}
import de.leanovate.jbj.runtime.value._
import de.leanovate.jbj.runtime.annotations.GlobalFunction

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

  @GlobalFunction(parameterMode = ParameterMode.EXACTLY_WARN, warnResult = NullVal)
  def get_resource_type(param: PVal)(implicit ctx: Context): PVal = {
    param match {
      case resource: ResourceVal[_] if resource.isOpen => StringVal(resource.resourceType)
      case _: ResourceVal[_] => StringVal("Unknown")
      case _ =>
        ctx.log.warn(s"get_resource_type() expects parameter 1 to be resource, ${param.typeName(simple = true)} given")
        NullVal
    }
  }

  @GlobalFunction(parameterMode = ParameterMode.EXACTLY_WARN)
  def fopen(fileName: String, mode: String, useIncludePath: Option[Boolean], context: Option[PVal])(implicit ctx: Context): PVal = {
    val path = ctx.filesystem.getPath(fileName)
    try {
      mode match {
        case "r" =>
          StreamResourceVal(Files.newByteChannel(path, StandardOpenOption.READ))
        case "r+" =>
          StreamResourceVal(Files.newByteChannel(path, StandardOpenOption.READ, StandardOpenOption.WRITE))
        case "w" =>
          StreamResourceVal(Files.newByteChannel(path, StandardOpenOption.WRITE))
        case "w+" =>
          StreamResourceVal(Files.newByteChannel(path, StandardOpenOption.READ, StandardOpenOption.WRITE))
        case "a" =>
          StreamResourceVal(Files.newByteChannel(path, StandardOpenOption.APPEND))
        case "a+" =>
          StreamResourceVal(Files.newByteChannel(path, StandardOpenOption.APPEND, StandardOpenOption.READ))
        case "x" =>
          StreamResourceVal(Files.newByteChannel(path, StandardOpenOption.CREATE_NEW))
        case "x+" =>
          StreamResourceVal(Files.newByteChannel(path, StandardOpenOption.CREATE_NEW, StandardOpenOption.READ))
        case "c" =>
          StreamResourceVal(Files.newByteChannel(path, StandardOpenOption.CREATE))
        case "c+" =>
          StreamResourceVal(Files.newByteChannel(path, StandardOpenOption.CREATE, StandardOpenOption.READ))
      }
    } catch {
      case e: NoSuchFileException =>
        ctx.log.warn(s"fopen($fileName): failed to open stream: No such file or directory")
        BooleanVal.FALSE
    }
  }

  @GlobalFunction
  def fclose(handle: PVal): Boolean = {
    handle match {
      case StreamResourceVal(stream) if stream.isOpen =>
        stream.close()
        true
      case _ => false
    }
  }

  @GlobalFunction
  def is_resource(handle: PVal): Boolean = {
    handle match {
      case ResourceVal(_, _) => true
      case _ => false
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
