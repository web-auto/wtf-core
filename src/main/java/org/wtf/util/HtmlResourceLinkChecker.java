/**
 * Copyright (C) 2014 WTF org.
 */

package org.wtf.util;

import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;


/**
 * An utility to check the given regx URL pattern not found on the given webdriver page.
 * 
 * @author venkatesan.sundramurthy@gmail.com (Venkatesan Sundramurthy)
 */
public class HtmlResourceLinkChecker {

  private WebDriver driver;

  public static enum HtmlTagEnum {
    META, LINK, SCRIPT, A, INPUT, IMG, DIV, SPAN;

    public static HtmlTagEnum getTagEnumTypeFromTagName(String tagName) {
      for (HtmlTagEnum tagType: HtmlTagEnum.values()) {
        if (tagType.toString().equalsIgnoreCase(tagName)) {
          return tagType;
        }
      }
      return null;
    }

    public static List<String> getTagNameList() {
      List<String> tagList = new ArrayList<String>();
      for (HtmlTagEnum item: HtmlTagEnum.values()) {
        tagList.add(item.toString().toLowerCase());
      }
      return tagList;
    }
  }

  public HtmlResourceLinkChecker(WebDriver driver) {
    this.driver = driver;
  }

  @SuppressWarnings("unchecked")
  private List<WebElement> getElementList() {
    List<WebElement> validElementList = new ArrayList<WebElement>();
    for (String tagName : HtmlTagEnum.getTagNameList()) {
      String script = "return document.getElementsByTagName(\"" + tagName + "\")";
      Object result = ((JavascriptExecutor)driver).executeScript(script);
      if (result instanceof List<?>) {
        validElementList.addAll((List<WebElement>)result);
      }
    }
    return validElementList;
  }

  private List<String> findBadURL(WebElement el) {
    List<String> badUrlList = new ArrayList<String>();
    String tagname = el.getTagName();
    switch (HtmlTagEnum.getTagEnumTypeFromTagName(tagname)) {
      case META:
        break;
      case LINK:
        break;
      case SCRIPT:
        break;
      case A:
        break;
      case INPUT:
        break;
      case IMG:
        break;
      case DIV:
        break;
      case SPAN:
        break;
    }
    return badUrlList;
  }

  public boolean check() {
    for (WebElement el : getElementList()) {
      findBadURL(el);
    }
    return true;
  }
}
