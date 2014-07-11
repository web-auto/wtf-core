/**
 * Copyright (C) 2011 eBay Inc.
 */

package org.wtf.core.feature.logger;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * An OutputStream that writes contents to a Logger upon each call to flush()
 */
public class LoggingOutputStream extends ByteArrayOutputStream {
  
  private String lineSeparator;

  /**
   * Constructor
   * @param logger Logger to write to
   * @param level Level at which to write the log message
   */
  public LoggingOutputStream(Logger logger, Level level) {
      super();
      lineSeparator = System.getProperty("line.separator");
  }
  
  /**
   * upon flush() write the existing contents of the OutputStream to the logger as 
   * a log record.
   * @throws java.io.IOException in case of error
   */
  public void flush() throws IOException {

      String record;
      synchronized(this) {
          super.flush();
          record = this.toString();
          super.reset();
      }
      
      if (record.length() == 0 || record.equals(lineSeparator)) {
          // avoid empty records
          return;
      }
  }
}