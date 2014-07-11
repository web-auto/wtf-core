/**
 * Copyright (C) 2014 WTF org.
 */

package org.wtf.util;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;

public abstract class JQueryDriverUtil {

  private static Object runJS(String script, WebDriver driver) {
    return ((JavascriptExecutor) driver).executeScript(script);
  }

  public static void hover(String jQueryLocator, WebDriver driver) {
    String query = String.format("jQuery(document).ready(function() {jQuery('#gh-eb-My').click(function() {alert('Cliecked');});});", jQueryLocator);
    runJS(query, driver);
  }
}
