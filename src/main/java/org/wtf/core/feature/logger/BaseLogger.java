/**
 * Copyright (C) 2014 WTF org.
 */

package org.wtf.core.feature.logger;

import java.io.PrintStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import org.apache.commons.lang.time.FastDateFormat;
import org.wtf.core.WTFConst;


/**
 * Logger.
 * 
 * @author venkatesan.sundramurthy@gmail.com (Venkatesan Sundramurthy)
 */
public class BaseLogger {
  public static PrintStream stdout = null;

  private static Map<Long, StringBuilder> logsByThreadID = new HashMap<Long, StringBuilder>();
  
  public static void removeLog(long threadID) {
    logsByThreadID.remove(threadID);
  }
  
  public static String getLogsForThreadID(long threadID) {
    StringBuilder logs = logsByThreadID.get(threadID);
    if (null != logs) {
      return logs.toString();
    } else {
      return null;
    }
  }

  

  public static Map<Long, String> getLogs() {
    Map<Long, String> logs = new HashMap<Long, String>();
    for (Map.Entry<Long, StringBuilder> entry : logsByThreadID.entrySet()) {
      logs.put(entry.getKey(), entry.getValue().toString());
    }
    
    return logs;
  }

  public static void LOG(Level level, String message) {
    String levelType = level != null ? level.toString() : WTFConst.EMPTY_STRING;

    stdout = stdout == null ? System.out : stdout;

    // shortern the log level names.
    levelType = levelType == Level.WARNING.toString() ? WTFConst.LOG_LEVEL_WARN : levelType;
    levelType = levelType == Level.SEVERE.toString() ? WTFConst.LOG_LEVEL_ERROR : levelType;

    if (level != null) {
      String logMessage =
          String.format("%s %s  %s",
                        FastDateFormat.getInstance(
                            WTFConst.DATA_FORMAT_HH_MM_SS_SSS).format(new Date()),
                        levelType, message);

      stdout.println(logMessage);

      // Store only when this feature is enabled.
      if (true) {
        long threadID = Thread.currentThread().getId();
        StringBuilder log = logsByThreadID.get(threadID);
        if (null == log) {
          log = new StringBuilder();
          logsByThreadID.put(threadID, log);
        }
        log.append(logMessage);
        //  This is a StringBuilder, so we need to add the newlines ourselves.
        log.append(WTFConst.NEW_LINE);

        // Log a copy for WTF dash.
        // TODO (Venkat)
        //WTFDashThreadPoolSafeLogger.pushLogs(logMessage);
      }

    } else {
      stdout.println(WTFConst.EMPTY_STRING);
    }
  }
}
