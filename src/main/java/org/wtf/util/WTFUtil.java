/**
 * Copyright (C) 2014 WTF org.
 */

package org.wtf.util;

import static org.wtf.core.feature.logger.BaseLogger.LOG;

import java.awt.AWTException;
import java.awt.Robot;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.google.common.base.Function;


/**
 * An utility to handle webdriver browser DOM interactions with wait conditions.
 * 
 * @author venkatesan.sundramurthy@gmail.com (Venkatesan Sundramurthy)
 */
public abstract class WTFUtil {

  private static final String XPATH_HTML_TAG_CONTAINS = "//%s[contains(@%s, '%s')]";
  private static final String XPATH_HTML_TAG_HAS = "//%s[@%s = '%s']";
  private static final String XPATH_ID_CONTAINS = "//..[contains(@id, '%s')]";
  private static final String GET_PARENT_XPATH = "..";
  private static final String SRP_QEID_XPATH_PART = "//*[@autoid='%s']";
  private static final String SRP_QEID_XPATH_PART2 = "//*[@autoid='%s']//*";
  
  public static int WEBDRIVER_TIMEOUT = 60;

  /**
   * Html tags.
   */
  public static enum Html {
    BODY("body"), DIV("div"), INPUT("input"), CLASS("class"), HREF("href"), ANCHOR(
        "a"), OPTION("option");

    private String id;

    private Html(String id) {
      this.id = id;
    }

    public String getId() {
      return id;
    }
  }

  /**
   * Locator types.
   */
  public static enum LocatorType {
    ID, ID_CONTAINS, NAME, CLASS_NAME, LINK_TEXT, PARTIAL_LINK_TEXT, CSS_SELECTOR, TAG, XPATH, LINK, PARTIAL_LINK, DIV_CLASS_CONTAINS, INPUT_CLASS_CONTAINS, SRP_QEID, SRP_QEID2;
  }

  public WTFUtil() {
    // Util class. }
  }

  public static String fixXPath(String xpath) {
    return xpath.charAt(0) != '.' ? ".".concat(xpath) : xpath;
  }

  /**
   * Returns the By object for the given locator and locator type.
   */
  public static By getLocatorBy(String locator, LocatorType locatorType) {
    switch (locatorType) {
    case ID:
      return By.id(locator);
    case NAME:
      return By.name(locator);
    case CLASS_NAME:
      return By.className(locator);
    case LINK_TEXT:
      return By.linkText(locator);
    case PARTIAL_LINK_TEXT:
      return By.partialLinkText(locator);
    case CSS_SELECTOR:
      return By.cssSelector(locator);
    case TAG:
      return By.tagName(locator);
    case XPATH:
      return By.xpath(fixXPath(locator));
    case LINK:
      return By.xpath(String.format(XPATH_HTML_TAG_HAS, Html.ANCHOR.getId(),
          Html.HREF.getId(), locator));
    case PARTIAL_LINK:
      return By.xpath(String.format(XPATH_HTML_TAG_CONTAINS,
          Html.ANCHOR.getId(), Html.HREF.getId(), locator));
    case DIV_CLASS_CONTAINS:
      return By.xpath(String.format(XPATH_HTML_TAG_CONTAINS, Html.DIV.getId(),
          Html.CLASS.getId(), locator));
    case INPUT_CLASS_CONTAINS:
      return By.xpath(String.format(XPATH_HTML_TAG_CONTAINS,
          Html.INPUT.getId(), Html.CLASS.getId(), locator));
    case ID_CONTAINS:
      return By.xpath(String.format(XPATH_ID_CONTAINS, locator));
    case SRP_QEID:
      return By.xpath(String.format(SRP_QEID_XPATH_PART, locator));
    case SRP_QEID2:
        return By.xpath(String.format(SRP_QEID_XPATH_PART2, locator));
    default:
      throw new IllegalArgumentException();
    }
  }

  /**
   * Hovers an element using locator and locator type, retries until success or
   * timeout.
   */
  public static void hoverWhenReady(String locator,
      final LocatorType locatorType, WebDriver driver, WebDriverWait wait) {
    // HACK to make hover work
    // 1-click in search box
    // final String SEARCH_BOX_XPATH =
    // "//input[@name='_nkw' and not(@id='gh-eb-searchTxt')]"; //must be search
    // box but not the mini-search box
    // WebElement searchElement =
    // findElementWhenReady(SEARCH_BOX_XPATH,locatorType,driver,wait);
    // searchElement.click();
    // 2-hover in search box
    // new Actions(driver).moveToElement(searchElement).build().perform();

    // 3-do the hover that you actually want
    WebElement hoverElement = findElementWhenReady(locator, locatorType,
        driver, wait);
    new Actions(driver).moveToElement(hoverElement).build().perform();
    // doMouseActionUsingJS("mouseover", hoverElement, driver);
  }

  /**
   * Hovers an element using locator and locator type, retries until success or
   * timeout.
   */
  public static void hoverWhenReady(WebElement parent, String locator,
      final LocatorType locatorType, WebDriver driver, WebDriverWait wait) {
    // HACK to make hover work
    // 1-click in search box
   /* final String SEARCH_BOX_XPATH = "//input[@name='_nkw' and not(@id='gh-eb-searchTxt')]"; // must
                                                                                            // be
                                                                                            // search
                                                                                            // box
                                                                                            // but
                                                                                            // not
                                                                                            // the
                                                                                            // mini-search
                                                                                            // box
    WebElement searchElement = findElementWhenReady(SEARCH_BOX_XPATH,
        locatorType, driver, wait);
    searchElement.click();*/
    // 2-hover in search box
    // new Actions(driver).moveToElement(searchElement).build().perform();

    // 3-do the hover that you actually want
    WebElement hoverElement = findElementWhenReady(parent, locator,
        locatorType, driver, wait);
    new Actions(driver).moveToElement(hoverElement).build().perform();
    // doMouseActionUsingJS("mouseover", hoverElement, driver);
  }

  public static void doMouseActionUsingJS(String mouseEvent,
      WebElement hoverOnElement, WebDriver webDriver) {
    ((JavascriptExecutor) webDriver).executeScript(
        "var event = document.createEvent('MouseEvents');"
            + "event.initEvent('" + mouseEvent + "', true, true);"
            + "var element = arguments[0];" + "element.dispatchEvent(event);",
        hoverOnElement);
  }

  /**
   * Clicks an element using locator and locator type, retries until success or
   * timeout.
   */
  public static void clickWhenReady(String locator,
      final LocatorType locatorType, WebDriver driver, WebDriverWait wait) {
    findElementWhenReady(locator, locatorType, driver, wait).click();
  }

  /**
   * Clicks an element using parent element, retries until success or timeout.
   */
  public static void clickWhenReady(WebElement parent, String locator,
      final LocatorType locatorType, WebDriver driver, WebDriverWait wait) {
    findElementWhenReady(parent, locator, locatorType, driver, wait).click();
  }

  /**
   * Inputs on a element using locator and locator type, retries until success
   * or timeout.
   */
  public static void typeWhenReady(String locator, LocatorType locatorType,
      WebDriver driver, WebDriverWait wait, String text) {
    findElementWhenReady(locator, locatorType, driver, wait).sendKeys(text);
  }

  /**
   * Clears the text box element using locator and locator type, retries until
   * success or timeout.
   */
  public static void clearWhenReady(String locator, LocatorType locatorType,
      WebDriver driver, WebDriverWait wait) {
    findElementWhenReady(locator, locatorType, driver, wait).clear();
  }

  /**
   * Returns the text from the text box, retries until success or timeout.
   */
  public static String getTextWhenReady(String locator,
      LocatorType locatorType, WebDriver driver, WebDriverWait wait) {
    return findElementWhenReady(locator, locatorType, driver, wait).getText();
  }
  
  /**
   * Returns the text from the text box, retries until success or timeout.
   */
  public static String getTextWhenReady(By locator, WebDriver driver, WebDriverWait wait) {
    return findElementWhenReady(locator, driver, wait).getText();
  }

  /**
   * Gets the element text value using parent element, retries until success or
   * timeout.
   */
  public static String getTextWhenReady(WebElement parent, String locator,
      LocatorType locatorType, WebDriver driver, WebDriverWait wait) {
    return findElementWhenReady(parent, locator, locatorType, driver, wait)
        .getText();
  }

  /**
   * Returns the text from the text box, retries until success or timeout.
   */
  public static String getValueWhenReady(String locator,
      LocatorType locatorType, WebDriver driver, WebDriverWait wait) {
    return getSelectedValueWhenReady(locator, locatorType, driver, wait);
  }

  /**
   * Gets the element text value using parent element, retries until success or
   * timeout.
   */
  public static String getValueWhenReady(WebElement parent, String locator,
      LocatorType locatorType, WebDriver driver, WebDriverWait wait) {
    return getSelectedValueWhenReady(parent, locator, locatorType, driver, wait);
  }

  /**
   * Returns the selected value from the dropdown, retries until success or
   * timeout.
   */
  public static String getSelectedValueWhenReady(String locator,
      LocatorType locatorType, WebDriver driver, WebDriverWait wait) {
    return findElementWhenReady(locator, locatorType, driver, wait)
        .getAttribute("value");
  }

  /**
   * Returns the selected value from the dropdown, retries until success or
   * timeout.
   */
  public static String getSelectedValueWhenReady(WebElement parent,
      String locator, LocatorType locatorType, WebDriver driver,
      WebDriverWait wait) {
    return findElementWhenReady(parent, locator, locatorType, driver, wait)
        .getAttribute("value");
  }

  /**
   * Selects the given frame using the given locator and locator type, retries
   * until success or timeout.
   */
  public static void selectFrameWhenReady(String locator,
      LocatorType locatorType, WebDriver driver, WebDriverWait wait) {
    driver.switchTo().frame(
        findElementWhenReady(locator, locatorType, driver, wait));
  }

  /**
   * Waits for an element to load using locator and locator type, retries until
   * success or timeout.
   */
  public static void waitForElementToLoad(String locator,
      LocatorType locatorType, WebDriver driver, WebDriverWait wait) {
    findElementWhenReady(locator, locatorType, driver, wait);
  }
  
  /**
   * Waits for an element to load using locator and locator type, retries until
   * success or timeout.
   */
  public static void waitForElementToLoad(By locator, WebDriver driver, WebDriverWait wait) {
    findElementWhenReady(locator, driver, wait);
  }

  /**
   * Waits for the given element to load, retries until success or timeout.
   */
  public static void waitForElementToLoad(WebElement el, String locator,
      LocatorType locatorType, WebDriver driver, WebDriverWait wait) {
    findElementWhenReady(el, locator, locatorType, driver, wait);
  }

  /**
   * Waits for the given element to load, retries until success or timeout.
   */
  public static void waitForElementToLoad(WebElement el, WebDriver driver,
      WebDriverWait wait) {
    findElementWhenReady(el, driver, wait);
  }

  /**
   * Determines the element loaded and ready to use, retries until success or
   * timeout.
   */
  public static boolean isElementLoadedWhenReady(String locator,
      LocatorType locatorType, WebDriver driver, WebDriverWait wait) {
    return findElementWhenReady(locator, locatorType, driver, wait) != null ? true
        : false;
  }

  /**
   * Determines the element loaded and ready to use, retries until success or
   * timeout.
   */
  public static boolean isElementLoadedWhenReady(WebElement parent,
      String locator, LocatorType locatorType, WebDriver driver,
      WebDriverWait wait) {
    return findElementWhenReady(parent, locator, locatorType, driver, wait) != null ? true
        : false;
  }

  /**
   * Return the parent element of the given element located using the given
   * locator and locator type, retries until success or timeout.
   */
  public static WebElement getParentWhenReady(String locator,
      LocatorType locatorType, WebDriver driver, WebDriverWait wait) {
    WebElement el = findElementWhenReady(locator, locatorType, driver, wait);
    return findElementWhenReady(el, GET_PARENT_XPATH, LocatorType.XPATH,
        driver, wait);
  }

  /**
   * Return the parent element of the given element, retries until success or
   * timeout.
   */
  public static WebElement getParentWhenReady(WebElement parent,
      WebDriver driver, WebDriverWait wait) {
    return findElementWhenReady(parent, GET_PARENT_XPATH, LocatorType.XPATH,
        driver, wait);
  }

  public static WebElement findElement(String locator, LocatorType locatorType,
      WebDriver driver) {
    return findElement(getLocatorBy(locator, locatorType), driver);
  }
  
  public static List<WebElement> findElements(String locator,
      LocatorType locatorType, WebDriver driver) {
    return driver.findElements(getLocatorBy(locator, locatorType));
  }

  public static WebElement findElement(WebElement parent, String locator,
      LocatorType locatorType, WebDriver driver) {
    return parent.findElement(getLocatorBy(locator, locatorType));
  }

  public static List<WebElement> findElements(WebElement parent,
      String locator, LocatorType locatorType, WebDriver driver) {
    return parent.findElements(getLocatorBy(locator, locatorType));
  }

  /**
   * Draws a red border around the found element. Note: Use it for debugging
   * purpose as it slows down your tests run speed
   * 
   * @param by
   * @param driver
   * @return {Instance of} WebElement
   */
  // TODO(jevasudevan): Improve this function to support all Tier-1 browsers
  public static WebElement findElement(By by, WebDriver driver) {
    WebElement elem = driver.findElement(by);
    // draw a border around the found element
    if ("true".equalsIgnoreCase(System.getProperty("HIGHLIGHT_ELEMENT"))
        && driver instanceof JavascriptExecutor) {
      ((JavascriptExecutor) driver).executeScript(
          "arguments[0].style.border='3px solid red'", elem);
      try {
        Thread.sleep(100);
      } catch (InterruptedException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
    return elem;
  }

  /**
   * Returns the Select element for the given locator and locator type. type,
   * retries until success or timeout.
   */
  public static Select getSelectElementWhenReady(final String locator,
      final LocatorType locatorType, final WebDriver driver,
      final WebDriverWait wait) {
    WebElement element = wait.until(new Function<WebDriver, WebElement>() {
      public WebElement apply(WebDriver driver) {
        try {
          WebElement selectEl = findElement(locator, locatorType, driver);
          if (isElementDisplayedOrEnabled(selectEl, driver)) {
            Select select = new Select(selectEl);
            select.getAllSelectedOptions();
            select.getOptions();
            return selectEl;
          }
        } catch (Exception e) {
          LOG(Level.WARNING, String.format(
              "Locator: %s  LocatorType: %s didn't match any Select element.",
              locator, locatorType.toString()));
        }
        return null;
      }
    });
    return new Select(element);
  }

  /**
   * Selects the given option value from the drop down using select element
   * locator and locator type, retries until success or timeout.
   */
/*  public static void selectOptionUsingValueWhenReady(final String locator,
      final LocatorType locatorType, WebDriver driver, WebDriverWait wait,
      final String value) {
    wait.until(new Function<WebDriver, WebElement>() {
      public WebElement apply(WebDriver driver) {
        try {
          WebElement selectEl = findElement(locator, locatorType, driver);
          if (isElementDisplayedOrEnabled(selectEl, driver)) {
        	  Select  select = new Select (selectEl);
            select.selectByValue(value);
            return selectEl;
          }
        } catch (Exception e) {
          LOG(Level.WARNING, String.format(
              "Locator: %s  LocatorType: %s didn't match element using select "
                  + "option value: %s ::: %s", locator, locatorType.toString(), value, e));
        }
        return null;
      }
    });
  }*/

  
   public static void selectOptionUsingValueWhenReady(final String locator,
  final LocatorType locatorType, WebDriver driver, WebDriverWait wait,
  final String value) {
	   wait.until(new Function<WebDriver, WebElement>() {
  public WebElement apply(WebDriver driver) {
    try {
    	 List<WebElement> listEl = findElements(locator, locatorType, driver);
    	//WebElement selectEl = findElement(locator, locatorType, driver);
    	 if (isElementDisplayedOrEnabled(listEl, driver))
    	 {
    		 for(WebElement we : listEl){
    			 Select  select = new Select(we);
    			 
    			// if(we.getText().equals("Time: newly listed")){
    				 select.selectByValue(value);
    				 return we;
    			// }
    			
    		 }
    	 }
      }
     catch (Exception e) {
      LOG(Level.WARNING, String.format(
          "Locator: %s  LocatorType: %s didn't match element using select "
              + "option value: %s ::: %s", locator, locatorType.toString(), value, e));
    }
    return null;
  }
});
}
   
  /**
   * Selects the given option value from the drop down using select element
   * locator and locator type, retries until success or timeout.
   */
  public static void selectOptionUsingVisibleTextWhenReady(
      final String locator, final LocatorType locatorType,
      final WebDriver driver, final WebDriverWait wait, final String text) {
    wait.until(new Function<WebDriver, WebElement>() {
      public WebElement apply(WebDriver driver) {
        try {
          WebElement selectEl = findElement(locator, locatorType, driver);
          if (isElementDisplayedOrEnabled(selectEl, driver)) {
            new Select(selectEl).selectByVisibleText(text);
            return selectEl;
          }
        } catch (Exception e) {
          LOG(Level.WARNING,
              String.format(
                  "Locator: %s  LocatorType: %s didn't match element using select "
                      + "option visible text: %s", locator,
                  locatorType.toString(), text));
        }
        return null;
      }
    });
  }

  /**
   * Selects the given option index from the drop down using select element
   * locator and locator type, retries until success or timeout.
   */
  public static void selectOptionUsingIndexWhenReady(final String locator,
      final LocatorType locatorType, final WebDriver driver,
      final WebDriverWait wait, final int index) {
    wait.until(new Function<WebDriver, WebElement>() {
      public WebElement apply(WebDriver driver) {
        try {
          WebElement selectEl = findElement(locator, locatorType, driver);
          if (isElementDisplayedOrEnabled(selectEl, driver)) {
            Select select = new Select(selectEl);
            select.selectByIndex(index);
            return selectEl;
          }
        } catch (Exception e) {
          LOG(Level.WARNING, String.format(
              "Locator: %s  LocatorType: %s didn't match element using select "
                  + "option indes: %s", locator, locatorType.toString(), index));
        }
        return null;
      }
    });
  }

  /**
   * Waits for the given element to ready, retries until success or timeout.
   */
  public static WebElement findElementWhenReady(final WebElement el,
      WebDriver driver, WebDriverWait wait) {
    return wait.until(new Function<WebDriver, WebElement>() {
      public WebElement apply(WebDriver driver) {
        try {
          if (isElementDisplayedOrEnabled(el, driver)) {
            return el;
          }
        } catch (Exception e) {
          LOG(Level.WARNING,
              "Unable to find element inside the given parent element.");
        }
        return null;
      }
    });
  }

  /**
   * Finds an element using its parent, retries until success or timeout.
   */
  public static WebElement findElementWhenReady(final WebElement parent,
      final String locator, final LocatorType locatorType, WebDriver driver,
      WebDriverWait wait) {
    return wait.until(new Function<WebDriver, WebElement>() {
      public WebElement apply(WebDriver driver) {
        try {
          WebElement el = findElement(parent, locator, locatorType, driver);
          if (isElementDisplayedOrEnabled(el, driver)) {
            return el;
          }
        } catch (Exception e) {
          LOG(Level.WARNING, String.format(
              "Locator: %s  LocatorType: %s didn't match any element inside the "
                  + "given parent element.", locator, locatorType.toString()));
        }
        return null;
      }
    });
  }

  /**
   * Finds an element using locator and locator type, retries until success or
   * timeout.
   */
  public static WebElement findElementWhenReady(final String locator,
      final LocatorType locatorType, WebDriver driver, WebDriverWait wait) {
    return wait.until(new Function<WebDriver, WebElement>() {
      public WebElement apply(WebDriver driver) {
        try {
          WebElement el = findElement(locator, locatorType, driver);
          if (isElementDisplayedOrEnabled(el, driver)) {
            return el;
          }
        } catch (Exception e) {
          LOG(Level.WARNING, String.format(
              "Locator: '%s'  LocatorType: %s, didn't match any element.",
              locator, locatorType.toString()));
        }
        return null;
      }
    });
  }
  
  /**
   * Finds an element using locator and locator type, retries until success or
   * timeout.
   */
  public static WebElement findElementWhenReady(final By locator, 
		  WebDriver driver, WebDriverWait wait) {
    return wait.until(new Function<WebDriver, WebElement>() {
      public WebElement apply(WebDriver driver) {
        try {
          WebElement el = findElement(locator, driver);
          if (isElementDisplayedOrEnabled(el, driver)) {
            return el;
          }
        } catch (Exception e) {
          LOG(Level.WARNING, String.format(
              "Locator: '%s' didn't match any element.",
              locator.toString()));
        }
        return null;
      }
    });
  }

  /**
   * Find and returns a list of matching elements using locator and locator
   * type, retries until success or timeout.
   */
  public static List<WebElement> findElementsWhenReady(final String locator,
      final LocatorType locatorType, WebDriver driver, WebDriverWait wait) {
    return wait.until(new Function<WebDriver, List<WebElement>>() {
      public List<WebElement> apply(WebDriver driver) {
        try {
          List<WebElement> elList = findElements(locator, locatorType, driver);
          for (WebElement el : elList) {
            if (!isElementDisplayedOrEnabled(el, driver)) {
              return null;
            }
          }
          return elList;
        } catch (Exception e) {
          LOG(Level.WARNING, String.format(
              "Locator: %s  LocatorType: %s didn't match element(s).", locator,
              locatorType.toString()));
        }
        return null;
      }
    });
  }

  /**
   * Find and returns a list of matching elements using locator and locator
   * type, retries until success or timeout.
   */
  public static List<WebElement> findElementsWhenReady(final WebElement parent,
      final String locator, final LocatorType locatorType, WebDriver driver,
      WebDriverWait wait) {
    return wait.until(new Function<WebDriver, List<WebElement>>() {
      public List<WebElement> apply(WebDriver driver) {
        try {
          List<WebElement> elList = findElements(parent, locator, locatorType,
              driver);
          for (WebElement el : elList) {
            if (!isElementDisplayedOrEnabled(el, driver)) {
              return null;
            }
          }
          return elList;
        } catch (Exception e) {
          LOG(Level.WARNING, String.format(
              "Locator: %s   LocatorType: %s didn't match any elements(s) inside "
                  + "the given parent.", locator, locatorType.toString()));
        }
        return null;
      }
    });
  }

  /**
   * Find and returns the single matching element using locator and locator
   * type, retries until success or timeout.
   */
  public static WebElement clickAndVerifyElementWhenReady(final String locator,
      final LocatorType locatorType, final String locatorToVerify,
      final LocatorType locatorTypeToVerify, final WebDriver driver,
      final WebDriverWait wait) {
    return wait.until(new Function<WebDriver, WebElement>() {
      public WebElement apply(WebDriver driver) {
        try {
          WebElement el = findElement(locator, locatorType, driver);
          el.click();
          if (isElementDisplayedOrEnabled(el, driver)) {
            waitForElementToLoad(locatorToVerify, locatorTypeToVerify, driver,
                getCustomWait(driver, 5));
            return el;
          }
        } catch (Exception e) {
          LOG(Level.WARNING, String.format(
              "Click and verify didn't work for Locator: %s  LocatorType: %s "
                  + " LocatorToVerify: %s  locatorTypeToVerify: %s.", locator,
              locatorType.toString(), locatorToVerify,
              locatorTypeToVerify.toString()));
        }
        return null;
      }
    });
  }

  /**
   * Find and returns boolean after locating the pop up windows by title,
   * retries until success or timeout.
   */
  public static Boolean waitForPopUpWindow(final String windowsTitle,
      final WebDriver driver, final WebDriverWait wait) {
    return wait.until(new Function<WebDriver, Boolean>() {
      public Boolean apply(WebDriver driver) {
        Set<String> windowHandles = driver.getWindowHandles();
        for (String handle : windowHandles) {
          if (driver.switchTo().window(handle).getTitle()
              .equalsIgnoreCase(windowsTitle)) {
            return true;
          }
        }
        return false;
      }
    });
  }

  /**
   * Find and returns boolean after locating the pop up windows by title,
   * retries until success or timeout.
   */
  public static Boolean waitForPopUpWindow(final String locator,
      final LocatorType locatorType, final WebDriver driver,
      final WebDriverWait wait) {
    return wait.until(new Function<WebDriver, Boolean>() {
      public Boolean apply(WebDriver driver) {
        String parentHandle = driver.getWindowHandle();
        Set<String> windowHandles = driver.getWindowHandles();
        for (String handle : windowHandles) {
          if (!handle.equals(parentHandle)) {
            WebDriver popUpWindowDriver = driver.switchTo().window(handle);
            boolean result = isElementExist(locator, locatorType,
                popUpWindowDriver);
            driver.switchTo().window(parentHandle);
            return result;
          }
        }
        driver.switchTo().window(parentHandle);
        return false;
      }
    });
  }

  /**
   * Find and returns boolean after locating the pop up windows by title,
   * retries until success or timeout.
   */
  public static WebDriver waitAndSelectPopUpWindow(final String locator,
      final LocatorType locatorType, final WebDriver driver,
      final WebDriverWait wait) {
    return wait.until(new Function<WebDriver, WebDriver>() {
      public WebDriver apply(WebDriver driver) {
        String parentHandle = driver.getWindowHandle();
        Set<String> windowHandles = driver.getWindowHandles();
        for (String handle : windowHandles) {
          if (!handle.equals(parentHandle)) {
            WebDriver popUpWindowDriver = driver.switchTo().window(handle);
            isElementExist(locator, locatorType, popUpWindowDriver);
            driver.switchTo().window(parentHandle);
            return popUpWindowDriver;
          }
        }
        driver.switchTo().window(parentHandle);
        return null;
      }
    });
  }

  /**
   * Determines if the given element is present on DOM.
   */
  public static boolean isElementExist(String locator, LocatorType locatorType,
      WebDriver driver) {
    try {
      return isElementDisplayedOrEnabled(
          findElement(locator, locatorType, driver), driver);
    } catch (Exception e) {
      LOG(Level.WARNING, String.format(
          "Locator: %s LocatorType: %s not found, Returning FALSE.", locator,
          locatorType));
    }
    return false;
  }

  /**
   * Determines if the given element is present on DOM and retries until success
   * or timeout.
   */
  public static boolean isElementExistWhenReady(String locator,
      LocatorType locatorType, WebDriver driver, WebDriverWait wait) {
    try {
      findElementWhenReady(locator, locatorType, driver, wait);
      return true;
    } catch (Exception e) {
      LOG(Level.WARNING, String.format(
          "Locator: %s LocatorType: %s not found, Returning FALSE.", locator,
          locatorType));
    }
    return false;
  }

  /**
   * Determines if the given element is present on DOM.
   */
  public static boolean isElementExist(WebElement parent, String locator,
      LocatorType locatorType, WebDriver driver) {
    try {
      return isElementDisplayedOrEnabled(
          findElement(parent, locator, locatorType, driver), driver);
    } catch (Exception e) {
      LOG(Level.WARNING, String.format(
          "Locator: %s LocatorType: %s not found, Returning FALSE.", locator,
          locatorType));
    }
    return false;
  }

  /**
   * Determines if the given element is present on DOM.
   */
  public static boolean isElementExistWhenReady(WebElement parent,
      String locator, LocatorType locatorType, WebDriver driver,
      WebDriverWait wait) {
    try {
      findElementWhenReady(parent, locator, locatorType, driver, wait);
      return true;
    } catch (Exception e) {
      LOG(Level.WARNING, String.format(
          "Locator: %s LocatorType: %s not found, Returning FALSE.", locator,
          locatorType));
    }
    return false;
  }

  /**
   * Determines the given check box/radio button element is checked/selected
   * using locator and locator type
   */
  public static boolean isSelected(String locator, LocatorType locatorType,
      WebDriver driver) {
    return findElement(locator, locatorType, driver).isSelected();
  }

  /**
   * Determines the element ready state using rendered web element isDisplayed()
   * or element isEnabled() based on the driver type.
   */
  public static boolean isElementDisplayedOrEnabled(WebElement el,
      WebDriver driver) {
    if (driver instanceof HtmlUnitDriver) {
      if (el.isEnabled()) {
        return true;
      } else {
        LOG(Level.WARNING, "Element not enabled..");
        return false;
      }
    } else {
      if (el.isDisplayed()) {
        return true;
      } else {
        LOG(Level.WARNING, "Element not enabled or not yet rendered..");
        return false;
      }
    }
  }

  /**
   * Determines the elements are in ready state using rendered web element
   * isDisplayed() or element isEnabled() based on the driver type.
   */
  public static boolean isElementDisplayedOrEnabled(List<WebElement> els,
      WebDriver driver) {
    for (WebElement el : els) {
      if (!isElementDisplayedOrEnabled(el, driver)) {
        return false;
      }
    }
    return true;
  }

  /**
   * Returns the custom webdriver wait object for the given timeout value.
   */
  public static WebDriverWait getCustomWait(WebDriver driver, long timeout) {
    return new WebDriverWait(driver, timeout);
  }

  /**
   * Returns the map of URL query items.
   */
  public static Map<String, String> getUrlQueryMap(String query) {
    Map<String, String> map = new HashMap<String, String>();
    String[] urlParts = query.split("\\?");
    if (urlParts.length > 1) {
      String[] params = urlParts[1].split("&");
      for (String param : params) {
        String name = param.split("=")[0];
        String value = param.split("=")[1];
        map.put(name, value);
      }
    }
    return map;
  }

  /**
   * Performs page down. Note: This is an hack to load all AJAX content for that
   * page. TODO(vsundramurthy): Clean this method.
   */
  public static void pageDown(WebDriver driver) {
    findElement(getLocatorBy("html", LocatorType.TAG), driver).sendKeys(
        Keys.PAGE_DOWN);
    findElement(getLocatorBy("html", LocatorType.TAG), driver).sendKeys(
        Keys.PAGE_DOWN);
    findElement(getLocatorBy("html", LocatorType.TAG), driver).sendKeys(
        Keys.PAGE_DOWN);
  }

  /**
   * Moves the mouse pointer away from browser view, by changing it coordinates
   * to 0X 0Y.
   */
  public static void arrestMousePointer() {
    try {
      Robot robot = new Robot();
      robot.mouseMove(0, 0);
    } catch (AWTException e) {
    }
  }

  /**
   * Finds an element using locator and locator type, retries until success or
   * timeout.
   */
  private static WebElement smartWaitWhenReady(final String locator,
      final LocatorType locatorType, WebDriver driver, WebDriverWait wait) {
    return wait.until(new Function<WebDriver, WebElement>() {
      public WebElement apply(WebDriver driver) {
        try {
          WebElement el = findElement(locator, locatorType, driver);
          if (isElementDisplayedOrEnabled(el, driver)) {
            return el;
          }
        } catch (Exception e) {
        }
        return null;
      }
    });
  }

  public static void smartWait(long seconds, WebDriver driver) {
    try {
      /*
       * StackTraceElement[] stackTraceElements =
       * Thread.currentThread().getStackTrace(); StringBuilder logMessage = new
       * StringBuilder();
       * logMessage.append("***** WebdriverUtil::smartWait() called at\n"); for
       * (int counter = 0; (counter < stackTraceElements.length) && (counter <
       * 15); counter++) { StackTraceElement stackTraceElement =
       * stackTraceElements[counter];
       * logMessage.append(String.format("%s::%s(%s:%d)\n",
       * stackTraceElement.getClassName(), stackTraceElement.getMethodName(),
       * stackTraceElement.getFileName(), stackTraceElement.getLineNumber())); }
       * LOG(Level.INFO, logMessage.toString());
       */
      LOG(Level.INFO, String.format("Smart wait for %s seconds..", seconds));

      smartWaitWhenReady("SMART-WAIT-IN-ACTION", LocatorType.ID, driver,
          getCustomWait(driver, seconds));
    } catch (Exception e) {

    }
  }

  public static void maximizeWindow(WebDriver driver) {
    Set<String> handles = driver.getWindowHandles();
    String script = "if (window.screen){var win = window.open(window.location);win.moveTo(0,0);"
        + "win.resizeTo(window.screen.availWidth,window.screen.availHeight);};";
    ((JavascriptExecutor) driver).executeScript(script);
    Set<String> newHandles = driver.getWindowHandles();
    newHandles.removeAll(handles);
    driver.switchTo().window(newHandles.iterator().next());
  }

  public static void doMouseClickUsingJS(String xpath, WebDriver driver) {
    doMouseActionUsingJS(xpath, "click", driver);
  }

  public static void doMouseOverUsingJS(String xpath, WebDriver driver) {
    doMouseActionUsingJS(xpath, "mouseover", driver);
  }

  public static void doMouseOutUsingJS(String xpath, WebDriver driver) {
    doMouseActionUsingJS(xpath, "mouseout", driver);
  }

  public static WebElement waitForElementPresent(String xpath, WebDriver driver) {
    WebDriverWait wait = new WebDriverWait(driver, WEBDRIVER_TIMEOUT);
    WebElement element = wait.until(presenceOfElementLocated(getXpath(xpath)));
    return element;
  }

  public static void doMouseActionUsingJS(String xpath, String mouseEvent,
      WebDriver driver) {
    waitForElementPresent(xpath, driver);
    WebElement element = findElement(By.xpath(xpath), driver);
    ((JavascriptExecutor) driver).executeScript(
        "var evt = document.createEvent('MouseEvents');" + "evt.initEvent('"
            + mouseEvent + "', true, true);" + "var element = arguments[0];"
            + "element.dispatchEvent(evt);", element);
  }

  public static By getXpath(String xpath) {
    if (xpath.startsWith("/")) {
      return By.xpath(xpath);
    } else if (xpath.startsWith("name=")) {
      return By.name(xpath.substring(xpath.indexOf("=") + 1));
    } else {
      return By.id(xpath);
    }
  }

  public static Function<WebDriver, WebElement> presenceOfElementLocated(
      final By locator) {
    return new Function<WebDriver, WebElement>() {
      public WebElement apply(WebDriver driver) {
        return findElement(locator, driver);
      }
    };
  }


  // function takes username, password, a unique
  // id which it will check after the sign in to see that it has come back to
  // old page back again, element on current page which it should click
  // for getting signed in, driver and wait. This id should be unique to that page. Our sign in
  // page has two sign in buttons which it will try if one fails, since
  // currently we have two versions of the page. One old and other is the new
  // DS3 version. And anytime you open the signin you may end up falling into
  // any of the guids which means any of the sign in page.

  public static void navigateToSignedInVersionOfPage(String username, String password,
      String signInElementOnCurrentPage, String uniqieIdOnCurrentPage,
      WebDriver driver, WebDriverWait wait) {
    clickWhenReady(signInElementOnCurrentPage, LocatorType.XPATH, driver,
        wait);
    typeWhenReady("//input[@id='userid']", LocatorType.XPATH, driver,
        wait, username);
    typeWhenReady("//input[@id='pass']", LocatorType.XPATH, driver, wait,
        password);
    clickWhenReady("//input[contains(@id, 'sgnBt')]", LocatorType.XPATH,
        driver,wait);
    waitForElementToLoad(uniqieIdOnCurrentPage, LocatorType.ID, driver,
        wait);
  }
  
  public static void scrollToBottomOfPage(WebDriver driver) {
    JavascriptExecutor js = (JavascriptExecutor) driver;
    js.executeScript("window.scrollTo(0, document.body.scrollHeight)");
  }
  
  public static void scrollToTopOfPage(WebDriver driver) {
	    JavascriptExecutor js = (JavascriptExecutor) driver;
	    js.executeScript("window.scrollTo(0, 0)");
  }
}
