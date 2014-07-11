/**
 * Copyright (C) 2014 WTF org.
 */

package org.wtf.core;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import org.wtf.core.WTFEnv;

public class WTFObject extends Object {

  /**
   * Factory method for creating page object based on given wtf env.
   */
  public static <T> T get(Class <T> wtfObjectClass, WTFEnv env) {
    return get(wtfObjectClass, env.getDriver(), env.getWait());
  }

  /**
   * Factory method for creating page object based on class type passed in.
   */
  public static <T> T get(Class <T> wtfObjectClass, WebDriver driver, WebDriverWait wait) {
    try {
      return wtfObjectClass.getConstructor(WebDriver.class, WebDriverWait.class)
          .newInstance(driver, wait);
    } catch (IllegalArgumentException e) {
      e.printStackTrace();
      throw e;
    } catch (SecurityException e) {
      e.printStackTrace();
      throw e;
    } catch (Exception e) {
      e.printStackTrace();
      StackTraceElement[] currentStack = e.getCause().getStackTrace();
      e.setStackTrace(currentStack);
      throw new RuntimeException(e.getCause().getMessage(), e.getCause());
    }
  }
}
