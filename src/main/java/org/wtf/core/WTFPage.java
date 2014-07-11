/**
 * Copyright (C) 2014 WTF org.
 */

package org.wtf.core;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import org.wtf.core.WTFObject;
import org.wtf.core.WTFPage;


/**
 * An abstract webdriver page object.
 * 
 * @author venkatesan.sundramurthy@gmail.com (Venkatesan Sundramurthy)
 */
public abstract class WTFPage extends WTFObject {

  private WebDriver driver;
  private WebDriverWait wait;

  public WTFPage(WebDriver driver, WebDriverWait wait) {
    this.driver = driver;
    this.wait = wait;
    waitForPageToLoad();
  }
  
  public WTFPage(WebDriver driver) {
    this.driver = driver;
    this.wait = new WebDriverWait(driver, 30);
    waitForPageToLoad();
  }

  /**
   * Returns the webdriver.
   */
  protected WebDriver getDriver() {
    return driver;
  }

  /**
   * Returns the webdriver wait.
   */
  protected WebDriverWait getWait() {
    return wait;
  }

  /**
   * Waits for the page to load.
   * This is a dummy place holder method.
   */
  public abstract WTFPage waitForPageToLoad();
}
