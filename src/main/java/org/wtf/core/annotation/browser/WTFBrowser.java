/**
 * Copyright (C) 2014 WTF org.
 */

package org.wtf.core.annotation.browser;

import org.openqa.selenium.Platform;


/**
 * WTF Browsers.
 * 
 * @author venkatesan.sundramurthy@gmail.com (Venkatesan Sundramurthy)
 */

public enum WTFBrowser {
  FIREFOX("ff", "firefox", null, null),
  FIREFOX_MAC("ffmac", "firefox", null, "MAC"),
  FIREFOX18("ff18", "firefox", "18.0", null),
  FIREFOX19("ff19", "firefox", "19.0", null),
  FIREFOX20("ff20", "firefox", "20.0", null),
  FIREFOX21("ff21", "firefox", "21.0", null),
  FIREFOX22("ff22", "firefox", "22.0", null),
  FIREFOX23("ff23", "firefox", "23.0", null),
  FIREFOX24("ff24", "firefox", "24.0", null),
  FIREFOX25("ff25", "firefox", "25.0", null),
  IE("ie", "internet explorer", null, null),
  IE7("ie7", "internet explorer", "7", null),
  IE8("ie8", "internet explorer", "8", null),
  IE9("ie9", "internet explorer", "9", null),
  IE10("ie10", "internet explorer", "10", null),
  CHROME("chrome", "chrome", null, null),
  CHROME_MAC("chromemac", "chrome", null, "MAC"),
  OPERA("opera", "opera", null, null),
  HTML_UNIT("htmlunit", "htmlunit", null, null),
  NATIVE_HTML_UNIT("nativehtmlunit", null, null, null),
  SAFARI("safari", "safari", null, null),
  SAFARI_MAC("safarimac", "safari", null, "MAC");

  public String browserName;
  public String browserNameOnGrid;
  public String version;
  public String os;

  private WTFBrowser(String name, String nameOnGrid, String version, String os) {
    this.browserName = name;
    this.browserNameOnGrid = nameOnGrid;
    this.version = version;
    this.os = os;
  }

  public String getBrowserName() {
    return String.format("%s%s%s",
                         this.name(),
                         this.version != null ? "-" + version : "",
                         this.os != null ? "-" + os : "");
  }

  public static WTFBrowser getBrowserNameFromList(String[] lists) {
    for (String item : lists) {
      for (WTFBrowser browser : WTFBrowser.values()) {
        if (browser.toString().equals(item)) {
          return browser;
        }
      }
    }
    return null;
  }

  public Platform getPlatform() {
    for (Platform pf : Platform.values()) {
      if (pf.name().equalsIgnoreCase(this.os)) {
        return pf;
      }
    }
    // TODO(vsundramurthy) Thrown exception for platform type not found.
    return null;
  }

  public static WTFBrowser parse(String browserName) {
    if (browserName == null) {
      return null;
    }

    String[] configParts = browserName.split("-");
    if (configParts.length >= 1) {
      for (WTFBrowser browser : WTFBrowser.values()) {
        if (browser.browserName.equalsIgnoreCase(configParts[0]) ||
            browser.browserNameOnGrid.equalsIgnoreCase(configParts[0])) {
          // parse version.
          if (configParts.length >= 2) { 
            browser.version = configParts[1];
          }
          // parse OS.
            if (configParts.length >= 3) {
              browser.os = configParts[2];
            }
            return browser;
          }
        }
      }
      return null;
    }
  }

