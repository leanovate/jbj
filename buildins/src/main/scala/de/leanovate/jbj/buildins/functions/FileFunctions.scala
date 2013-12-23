/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.buildins.functions

import de.leanovate.jbj.runtime.annotations.ParameterMode
import de.leanovate.jbj.runtime.context.Context
import java.nio.file._
import de.leanovate.jbj.runtime.value._
import java.nio.channels.SeekableByteChannel
import java.nio.ByteBuffer
import de.leanovate.jbj.runtime.annotations.GlobalFunction
import de.leanovate.jbj.runtime.value.IntegerVal
import java.nio.file.attribute.BasicFileAttributes
import scala.collection.JavaConversions._

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

  @GlobalFunction(parameterMode = ParameterMode.STRICT_WARN)
  def fread(stream: SeekableByteChannel, length: Int): PVal = {
    if (stream.isOpen) {
      val buffer = ByteBuffer.allocate(length)
      val bytes = stream.read(buffer)
      val result = new Array[Byte](bytes)

      buffer.flip()
      buffer.get(result)
      StringVal(result)
    } else
      BooleanVal.FALSE
  }

  @GlobalFunction(parameterMode = ParameterMode.STRICT_WARN)
  def fwrite(stream: SeekableByteChannel, string: Array[Byte], length: Option[Int]): PVal = {
    if (stream.isOpen) {
      val buffer = ByteBuffer.wrap(string, 0, length.getOrElse(string.length))

      IntegerVal(stream.write(buffer))
    } else
      BooleanVal.FALSE
  }

  @GlobalFunction(parameterMode = ParameterMode.STRICT_WARN)
  def fclose(stream: SeekableByteChannel): Boolean = {
    if (stream.isOpen) {
      stream.close()
      true
    } else
      false
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
  def is_dir(fileName: String)(implicit ctx: Context): Boolean = {
    Files.isDirectory(ctx.filesystem.getPath(fileName))
  }

  @GlobalFunction
  def is_file(fileName: String)(implicit ctx: Context): Boolean = {
    Files.isRegularFile(ctx.filesystem.getPath(fileName))
  }

  @GlobalFunction
  def is_link(fileName: String)(implicit ctx: Context): Boolean = {
    Files.isSymbolicLink(ctx.filesystem.getPath(fileName))
  }

  @GlobalFunction
  def readlink(fileName: String)(implicit ctx: Context): String = {
    Files.readSymbolicLink(ctx.filesystem.getPath(fileName)).toString
  }

  @GlobalFunction
  def is_readable(fileName: String)(implicit ctx: Context): Boolean = {
    Files.isReadable(ctx.filesystem.getPath(fileName))
  }

  @GlobalFunction
  def is_writable(fileName: String)(implicit ctx: Context): Boolean = {
    Files.isWritable(ctx.filesystem.getPath(fileName))
  }

  @GlobalFunction
  def is_writeable(fileName: String)(implicit ctx: Context) = is_writable(fileName)

  @GlobalFunction
  def realpath(path: String)(implicit ctx: Context): String = {
    ctx.filesystem.getPath(path).toRealPath().toString
  }

  @GlobalFunction(parameterMode = ParameterMode.STRICT_WARN, warnResult = NullVal)
  def scandir(directory: String, sortingOrder: Option[Int], context: Option[PVal])(implicit ctx: Context): PVal = {
    try {
      val directoryPath = ctx.filesystem.getPath(directory)
      val result = Files.newDirectoryStream(directoryPath).iterator().map(f => directoryPath.relativize(f).toString).toSeq
      ArrayVal(result.map(None -> StringVal(_)): _ *)
    } catch {
      case e: NoSuchFileException =>
        ctx.log.warn(s"scandir($directory): failed to open dir: No such file or directory")
        BooleanVal.FALSE
    }
  }

  @GlobalFunction
  def tempnam(dir: String, prefix: String)(implicit ctx: Context): String = {
    Files.createTempFile(ctx.filesystem.getPath(dir), prefix, "").toString
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
