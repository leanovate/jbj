package de.leanovate.jbj.core

import de.leanovate.jbj.api.JbjEnvironment
import java.io.PrintStream

object JbjEnvironmentBuilder extends JbjEnvironment.Builder {
  def build() = JbjEnv(scriptLocator, settings, Seq.empty, Option(errorStream).map(new PrintStream(_, false, "UTF-8")))
}
