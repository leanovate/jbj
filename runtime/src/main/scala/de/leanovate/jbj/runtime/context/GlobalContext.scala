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
import scala.collection.immutable.Stack
import de.leanovate.jbj.runtime.exception.CompileErrorException
import de.leanovate.jbj.runtime.value._
import java.util.concurrent.atomic.AtomicLong
import scala.collection.JavaConversions._
import de.leanovate.jbj.runtime.types._
import de.leanovate.jbj.runtime.output.OutputBuffer
import de.leanovate.jbj.api.http.JbjSettings
import de.leanovate.jbj.runtime.value.ObjectPropertyKey.Key

case class GlobalContext(jbj: JbjRuntimeEnv, out: OutputBuffer, err: Option[PrintStream], settings: JbjSettings)
  extends Context {
  private var _inShutdown = false

  private var _currentNamespace = NamespaceName(relative = false, prefixed = false)
  private var _namespaceAliases = Map.empty[String, NamespaceName]

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
  val lambdaCounter = new AtomicLong(0)

  val GLOBALS = ArrayVal()
  val _SERVER = ArrayVal()

  GLOBALS.retain()
  GLOBALS.setAt("GLOBALS", GLOBALS)(this)
  GLOBALS.setAt("_SERVER", _SERVER)(this)

  def include(file: String)(implicit ctx: Context): Option[(JbjScript, Boolean)] = jbj.parse(file) match {
    case Some(Left(script)) =>
      Some(script, includedFiles.add(script.fileName))
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

  def findInterface(name: NamespaceName, autoload: Boolean, includePredefined: Boolean = true): Option[PInterface] = {
    val result = interfaces.get(name.lowercase).map(Some.apply).getOrElse {
      if (includePredefined)
        jbj.predefinedInterfaces.get(name.lowercase)
      else
        None
    }.map(interfaceInitializer)

    if (autoload) {
      result.map(Some.apply).getOrElse(tryAutoload(name, findInterface(_, _, includePredefined)))
    } else {
      result
    }
  }

  def declaredClasses: Seq[PClass] = jbj.predefinedClasses.values.toSeq ++ classes.values.toSeq

  def findClass(name: NamespaceName, autoload: Boolean, includePredefined: Boolean = true): Option[PClass] = {
    val result = classes.get(name.lowercase).map(Some.apply).getOrElse {
      if (includePredefined)
        jbj.predefinedClasses.get(name.lowercase)
      else
        None
    }.map(classInitializer)

    if (autoload) {
      result.map(Some.apply).getOrElse(tryAutoload(name, findClass(_, _, includePredefined)))
    } else {
      result
    }
  }

  def findInterfaceOrClass(name: NamespaceName, autoload: Boolean, includePredefined: Boolean = true): Option[Either[PInterface, PClass]] = {
    val result = findInterface(name, autoload = false, includePredefined).map {
      interface => Some(Left(interface))
    }.getOrElse {
      findClass(name, autoload = false, includePredefined).map(Right(_))
    }

    if (autoload) {
      result.map(Some.apply).getOrElse(tryAutoload(name, findInterfaceOrClass(_, _, includePredefined)))
    } else {
      result
    }
  }

  private def tryAutoload[T](name: NamespaceName, retry: (NamespaceName, Boolean) => Option[T]): Option[T] = {
    implicit val ctx = this

    findFunction(NamespaceName(relative = true, prefixed = false, "__autoload")).flatMap {
      case autoloadFunc if !autoloading.contains(name.toString.toLowerCase) =>
        autoloading.add(name.toString.toLowerCase)
        autoloadFunc.call(PAnyParam(StringVal(name.toString)) :: Nil)
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

  def findConstant(name: NamespaceName): Option[PVal] = {
    constants.get(CaseSensitiveConstantKey(name.path)).map(Some.apply).getOrElse {
      constants.get(CaseInsensitiveConstantKey(name.lowercase)).map(Some.apply).getOrElse {
        jbj.preedfinedConstants.get(name.lastPath.toLowerCase)
      }
    }
    //      .map(Some.apply).getOrElse {
    //      constants.get(CaseSensitiveConstantKey(Seq(name.lastPath))).map(Some.apply).getOrElse {
    //        constants.get(CaseInsensitiveConstantKey(Seq(name.lastPath.toLowerCase)))
    //      }
    //    }
  }

  def defineConstant(name: NamespaceName, value: PVal, caseInsensitive: Boolean) {
    if (name.toString.contains("::")) {
      log.warn("Class constants cannot be defined or redefined")
    } else {
      if (caseInsensitive)
        constants.put(CaseInsensitiveConstantKey(name.lowercase), value)
      else
        constants.put(CaseSensitiveConstantKey(name.path), value)
    }
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
    jbj.predefinedFunctions.get(name.lowercase).map(Some.apply).
      getOrElse(functions.get(name.lowercase)).map(Some.apply).
      getOrElse(jbj.predefinedFunctions.get(Seq(name.lastPath.toLowerCase))).map(Some.apply).
      getOrElse(functions.get(Seq(name.lastPath.toLowerCase)))

  def defineFunction(function: PFunction) {
    functions.put(function.name.lowercase, function)
  }

  def staticContext(identifier: String): StaticContext =
    staticContexts.getOrElseUpdate(identifier, new GenericStaticContext)

  def staticClassObject(pClass: PClass): ObjectVal = {
    val identifier = "Class_" + pClass.toString
    val classStaticObject = staticClassObjects.getOrElseUpdate(identifier, {
      val obj = new StdObjectVal(PStdClass, -1, new ExtendedLinkedHashMap[Key])
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
        CallbackHelper.callCallback(callback, shutdownParameters: _*)(this)
    }
    currentPosition = NoNodePosition
    _inShutdown = true
    staticContexts.values.foreach(_.cleanup()(this))
    staticClassObjects.values.foreach(_.cleanup()(this))
    GLOBALS.cleanup()(this)

    out.closeAll()
  }

  def currentNamespace: NamespaceName = _currentNamespace

  def currentNamespace_=(name: NamespaceName) {
    _currentNamespace = name
  }

  def namespaceAliases: Map[String, NamespaceName] = _namespaceAliases

  def namespaceAliases_=(aliases: Map[String, NamespaceName]) {
    _namespaceAliases = aliases
  }

  def resetCurrentNamepsace() {
    _currentNamespace = NamespaceName(relative = false, prefixed = false)
    _namespaceAliases = Map.empty
  }

  private def classInitializer(pClass: PClass): PClass = {
    pClass
  }

  private def interfaceInitializer(pInterface: PInterface): PInterface = {
    pInterface
  }
}
