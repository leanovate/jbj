/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.core.buildin

import de.leanovate.jbj.runtime.annotations.GlobalFunction
import java.util.Calendar
import de.leanovate.jbj.runtime.context.Context
import de.leanovate.jbj.runtime.NodePosition

object DateFunctions extends WrappedFunctions {
  @GlobalFunction
  def mktime(hour: Option[Int], minute: Option[Int], second: Option[Int],
             month: Option[Int], day: Option[Int], year: Option[Int],
             isDst: Option[Int])(ctx: Context, position: NodePosition): Long = {
    if (hour.isEmpty) {
      ctx.log.strict("mktime(): You should be using the time() function instead")
      time()
    } else {
      val calendar = Calendar.getInstance(ctx.settings.getTimeZone)
      calendar.setTimeInMillis(System.currentTimeMillis)
      hour.foreach(calendar.set(Calendar.HOUR_OF_DAY, _))
      minute.foreach(calendar.set(Calendar.MINUTE, _))
      second.foreach(calendar.set(Calendar.SECOND, _))
      month.foreach(m => calendar.set(Calendar.MONTH, m - 1))
      day.foreach(calendar.set(Calendar.DAY_OF_MONTH, _))
      year.foreach(calendar.set(Calendar.YEAR, _))
      calendar.getTimeInMillis / 1000L
    }
  }

  @GlobalFunction
  def time(): Long = {
    System.currentTimeMillis / 1000L
  }
}
