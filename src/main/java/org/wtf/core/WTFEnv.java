/**
 * Copyright (C) 2014 WTF org.
 */

package org.wtf.core;

import java.lang.reflect.Method;
import java.util.LinkedList;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.wtf.core.annotation.browser.WTFBrowser;
import org.wtf.core.annotation.country.WTFCountry;
import org.wtf.core.feature.args.converter.ServerEnvironment.Environment;
import org.wtf.core.feature.softassert.SoftAssert;
import org.wtf.core.TearDown;
import org.wtf.core.WTFEnv;

import com.gargoylesoftware.htmlunit.WebClient;


/**
 * A test environment for webdriver
 * 
 * @author venkatesan.sundramurthy@gmail.com (Venkatesan Sundramurthy)
 */
public class WTFEnv {

  private WebDriver driver;
  private WebClient webClient;
  private WebDriverWait wait;
  private LinkedList<TearDown> teardown;
  public  SoftAssert softAssert;
  ITestContext testContext;
  ITestResult testResult;
  WTFCountry country;
  WTFBrowser browser;
  Environment environment;

  public WTFEnv(WebDriver driver, WebDriverWait wait, WTFCountry country,
      WTFBrowser browser, Environment environment, ITestContext context, ITestResult testResult) {
    this.teardown = new LinkedList<TearDown>();
    this.driver = driver;
    this.wait = wait;
    this.testContext = context;
    this.testResult = testResult;
    softAssert = new SoftAssert();
    this.country = country;
    this.browser = browser;
    this.environment = environment;
  }
  
  public WTFEnv(WebClient webClient, WTFCountry country, WTFBrowser browser, Environment environment, 
		  ITestContext context, 
		  ITestResult testResult) {
	this.teardown = new LinkedList<TearDown>();
	this.webClient = webClient;
	this.driver = null;
	this.wait = null;
	this.testContext = context;
	this.testResult = testResult;
	softAssert = new SoftAssert();
	this.country = country;
	this.browser = browser;
	this.environment = environment;
 }

  /**
   * Returns the webdriver.
   */
  public WebDriver getDriver() {
    return driver;
  }
  
  /**
   * Returns the web client.
   */
  public WebClient getWebClient() {
    return webClient;
  }

  /**
   * Returns the webdriver wait.
   */
  public WebDriverWait getWait() {
    return wait;
  }

  /**
   * Returns the testNG test context.
   */
  public ITestContext getTestContext() {
    return testContext;
  }

  /**
   * Returns the testNG test result for that test method.
   */
  public ITestResult getTestResult() {
    return testResult;
  }

  /**
   * Returns the java method of the current test.
   */
  public Method getMethod() {
    return testResult.getMethod().getConstructorOrMethod().getMethod();
  }

  /**
   * Returns the tear down list.
   */
  public LinkedList<TearDown> getTearDown() {
    return teardown;
  }

  /**
   * Adds the given tear down.
   */
  public void addTearDown(TearDown tearDown) {
    teardown.add(tearDown);
  }

  /**
   * Returns the country.
   */
  public WTFCountry getSite() {
    return country;
  }

  /**
   * Sets the country.
   */
  public void setSite(WTFCountry site) {
    this.country = site;
  }

  /**
   * Returns the browser.
   */
  public WTFBrowser getBrowser() {
    return browser;
  }

  /**
   * Sets the browser.
   */
  public void setBrowser(WTFBrowser browser) {
    this.browser = browser;
  }

  /**
   * Returns the environment.
   */
  public Environment getEnvironment() {
    return environment;
  }

  /**
   * Sets the environment.
   */
  public void setEnvironment(Environment environment) {
    this.environment = environment;
  }

  public static WTFEnv getWebdriverEnvironmentFromObjectList(Object [] objects) {
    for (Object object : objects) {
      if (object instanceof WTFEnv) {
        return (WTFEnv)object;
      }
    }
    return null;
  }
  
  public enum Bot{
	  GOOGLE("Mozilla/5.0 (compatible; Googlebot/2.1; +http://www.google.com/bot.html)"), 
	  BING("Mozilla/5.0 (compatible; bingbot/2.0; +http://www.bing.com/bingbot.htm)");
	  
	  private String userAgentString;
	  
	  Bot(String userAgentString){
		  this.userAgentString = userAgentString;
	  }
	  
	  public String getUserAgentString(){
		 return this.userAgentString;
	  }
  } 
  
  public WTFEnv setBotDriver(Bot userAgent){
	  //close old opened driver browser
	  getDriver().close();
	  
	  FirefoxProfile profile = new FirefoxProfile();
	  profile.setPreference("general.useragent.override", userAgent.getUserAgentString());
	  WebDriver driver = new FirefoxDriver(profile);
	  
	  this.driver = driver;
	  
	  return this;

  }
}
