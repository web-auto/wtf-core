/**
 * Copyright (C) 2014 WTF org.
 */

package org.wtf.core;

import static org.wtf.core.feature.logger.BaseLogger.LOG;

import java.util.NoSuchElementException;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import org.wtf.core.WTFWait;


/**
 * Custom webdriver wait class extends WebDriverWait.
 * 
 * @author venkatesan.sundramurthy@gmail.com (Venkatesan Sundramurthy)
 */
public class WTFWait  extends WebDriverWait{

  public static final long SLEEP_IN_BETWEEN_POLLS = 500L;

  public WTFWait(WebDriver driver, long timeOutInSeconds) {
    super(driver, timeOutInSeconds, WTFWait.SLEEP_IN_BETWEEN_POLLS);
  }

  protected void throwTimeoutException(java.lang.String message,
      java.lang.Exception lastException) {
    LOG(null, lastException.getMessage());
    throw new NoSuchElementException(lastException.getMessage());
  }
}
