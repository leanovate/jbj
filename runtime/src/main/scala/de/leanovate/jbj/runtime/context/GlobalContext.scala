/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.runtime.context

import java.io.PrintStream
import scala.collection.mutable
import de.leanovate.jbj.runtime._
import de.leanovate.jbj.core.ast.{NoNodePosition, Prog, NodePosition, NamespaceName}
import scala.collection.immutable.Stack
import de.leanovate.jbj.runtime.exception.CompileErrorException
import de.leanovate.jbj.runtime.value._
import java.util.concurrent.atomic.AtomicLong
import de.leanovate.jbj.api.JbjSettings
import scala.collection.JavaConversions._
import de.leanovate.jbj.core.ast.expr.value.ScalarExpr
import de.leanovate.jbj.core.JbjRuntimeEnv
import scala.Some
import de.leanovate.jbj.runtime.output.OutputBuffer
import de.leanovate.jbj.core.buildin.{CallbackHelper, PStdClass}

case class GlobalContext(jbj: JbjRuntimeEnv, out: OutputBuffer, err: Option[PrintStream], settings: JbjSettings)
  extends Context {
  private var _inShutdown = false

  private val interfaces = mutable.Map.empty[Seq[String], PInterface]

  private val classes = mutable.Map.empty[Seq[String], PClass]

  private val constants = mutable.Map.empty[ConstantKey, PVal]

  private val functions = mutable.Map.empty[Seq[String], PFunction]

  private val staticContexts = mutable.Map.empty[String, StaticContext]

  private val staticClassObjects = mutable.Map.empty[String, ObjectVal]

  private val includedFiles = mutable.Set.empty[String]

  private val autoloading = mutable.Set.empty[String]

  var errorHandlerTypes: Int = JbjSettings.E_ALL.foldLeft(0) {
    (r, errorLevel) => r | errorLevel.getValue
  }

  var errorHandler: Option[PVal] = None

  var shutdownHandler: Option[PVal] = None

  var shutdownParameters: Seq[PVal] = Seq.empty

  def name = ""

  def global = this

  def static = staticContext("global")

  def stack: Stack[NodePosition] = Stack.empty[NodePosition]

  val instanceCounter = new AtomicLong(0)

  val GLOBALS = ArrayVal()

  GLOBALS.setAt("GLOBALS", GLOBALS)(this)

  def include(file: String)(implicit ctx: Context): Option[(Prog, Boolean)] = jbj.parse(file) match {
    case Some(Left(prog)) =>
      Some(prog, includedFiles.add(prog.fileName))
    case Some(Right(t)) => throw new CompileErrorException(t.getMessage)
    case None if file.startsWith("/") => None
    case None =>
      val idx = ctx.currentPosition.fileName.lastIndexOf('/')
      if (idx < 0)
        None
      else
        jbj.parse(ctx.currentPosition.fileName.substring(0, idx + 1) + file) match {
          case Some(Left(prog)) => Some(prog, includedFiles.add(prog.fileName))
          case Some(Right(t)) => throw new CompileErrorException(t.getMessage)
          case None => None
        }
  }

  def findInterface(name: NamespaceName, autoload: Boolean): Option[PInterface] = {
    val result = jbj.predefinedInterfaces.get(name.lowercase).map(Some.apply).getOrElse(interfaces.get(name.lowercase)).map(interfaceInitializer)

    if (autoload) {
      result.map(Some.apply).getOrElse(tryAutoload(name, findInterface))
    } else {
      result
    }
  }

  def findClass(name: NamespaceName, autoload: Boolean): Option[PClass] = {
    val result = jbj.predefinedClasses.get(name.lowercase).map(Some.apply).getOrElse(classes.get(name.lowercase)).map(classInitializer)

    if (autoload) {
      result.map(Some.apply).getOrElse(tryAutoload(name, findClass))
    } else {
      result
    }
  }

  def findInterfaceOrClass(name: NamespaceName, autoload: Boolean): Option[Either[PInterface, PClass]] = {
    val result = findInterface(name, autoload = false).map {
      interface => Some(Left(interface))
    }.getOrElse {
      findClass(name, autoload = false).map(Right(_))
    }

    if (autoload) {
      result.map(Some.apply).getOrElse(tryAutoload(name, findInterfaceOrClass))
    } else {
      result
    }
  }

  private def tryAutoload[T](name: NamespaceName, retry: (NamespaceName, Boolean) => Option[T]): Option[T] = {
    implicit val ctx = this

    findFunction(NamespaceName(relative = true, "__autoload")).flatMap {
      case autoloadFunc if !autoloading.contains(name.toString.toLowerCase) =>
        autoloading.add(name.toString.toLowerCase)
        autoloadFunc.call(ScalarExpr(StringVal(name.toString)) :: Nil)
        autoloading.remove(name.toString.toLowerCase)
        retry(name, false)
      case _ => None
    }
  }

  def defineClass(pClass: PClass) {
    classes.put(pClass.name.lowercase, pClass)
  }

  def defineInterface(pInterface: PInterface) {
    interfaces.put(pInterface.name.lowercase, pInterface)
  }

  def findConstant(name: String): Option[PVal] =
    jbj.preedfinedConstants.get(name.toUpperCase).map(Some.apply).getOrElse {
      constants.get(CaseSensitiveConstantKey(name)).map(Some.apply).getOrElse {
        constants.get(CaseInsensitiveConstantKey(name.toLowerCase))
      }
    }

  def defineConstant(name: String, value: PVal, caseInsensitive: Boolean) {
    if (caseInsensitive)
      constants.put(CaseInsensitiveConstantKey(name.toLowerCase), value)
    else
      constants.put(CaseSensitiveConstantKey(name), value)
  }

  override def findVariable(name: String) = GLOBALS.getAt(name)(this).map {
    case variable: PVar => variable
    case pVal: PVal =>
      val variable = PVar(pVal)
      GLOBALS.setAt(name, variable)(this)
      variable
  }

  override def defineVariable(name: String, variable: PVar) {
    GLOBALS.setAt(name, variable)(this)
  }

  override def undefineVariable(name: String) {
    GLOBALS.unsetAt(name)(this)
  }

  def findFunction(name: NamespaceName) =
    jbj.predefinedFunctions.get(name.lowercase).map(Some.apply).getOrElse(functions.get(name.lowercase))

  def defineFunction(function: PFunction) {
    functions.put(function.name.lowercase, function)
  }

  def staticContext(identifier: String): StaticContext =
    staticContexts.getOrElseUpdate(identifier, new GenericStaticContext)

  def staticClassObject(pClass: PClass): ObjectVal = {
    val identifier = "Class_" + pClass.toString
    val classStaticObject = staticClassObjects.getOrElseUpdate(identifier, {
      val obj = new StdObjectVal(PStdClass, -1, mutable.LinkedHashMap.empty)
      obj.retain()
      obj
    })
    pClass.initializeStatic(classStaticObject)(this)
    classStaticObject
  }

  def inShutdown = _inShutdown

  var isOutputBufferingCallback = false

  def cleanup() {
    shutdownHandler.foreach {
      callback =>
        CallbackHelper.callCallabck(callback, shutdownParameters: _*)(this)
    }
    currentPosition = NoNodePosition
    _inShutdown = true
    staticContexts.values.foreach(_.cleanup()(this))
    staticClassObjects.values.foreach(_.cleanup()(this))
    GLOBALS.cleanup()(this)

    out.closeAll()
  }

  private def classInitializer(pClass: PClass): PClass = {
    pClass
  }

  private def interfaceInitializer(pInterface: PInterface): PInterface = {
    pInterface
  }
}
