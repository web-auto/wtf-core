/**
 * Copyright (C) 2014 WTF org.
 */

package org.wtf.core.feature.multiplier;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.testng.IMethodInstance;
import org.testng.IMethodInterceptor;
import org.testng.ITestContext;
import org.testng.ITestNGMethod;
import org.wtf.core.WTFTest;
import org.wtf.core.WTFTestConfig;
import org.wtf.core.annotation.browser.WTFBrowserAnnotationReader;
import org.wtf.core.annotation.browser.WTFBrowser;
import org.wtf.core.annotation.country.WTFCountry;
import org.wtf.core.annotation.country.WTFCountryAnnotationReader;
import org.wtf.core.feature.args.converter.ServerEnvironment.Environment;
import org.wtf.core.feature.retry.BaseTestRetryAnalyzer;



/**
 * Intercept, multiplex and filter tests for the given Browser and Sites combination.
 * 
 * @author venkatesan.sundramurthy@gmail.com (Venkatesan Sundramurthy)
 */
public class TestMultiplier implements IMethodInterceptor {

  public List<IMethodInstance> intercept(List<IMethodInstance> methodInstances, ITestContext context) {

	List <IMethodInstance> allowedMethods = new ArrayList <IMethodInstance>();
	WTFBrowser browserTypeFromXml = WTFBrowser.parse(WTFTest.getParameter(context, "browser-type"));

    // County/Site list from user input.
    List <WTFCountry> requestedCountryList =
        new ArrayList <WTFCountry> (Arrays.asList(WTFTestConfig.getSites()));

    // Browser list from user input.
    List <WTFBrowser> requestedBrowserList = new ArrayList<WTFBrowser>();
    if (browserTypeFromXml != null) {    
    	requestedBrowserList.add(browserTypeFromXml);
    } else {
    	requestedBrowserList = new ArrayList <WTFBrowser> (Arrays.asList(WTFTestConfig.getBrowsers()));
    }

    // Browser list from user input.
    List <Environment> requestedEnvironmentList =
        new ArrayList <Environment> (Arrays.asList(WTFTestConfig.getEnvironments()));

    // Create all combinations of county and browser tests based on user input.
    //if (browserTypeFromXml == null)
    for (IMethodInstance methodInstance : methodInstances) {
      allowedMethods.addAll(duplicate(methodInstance.getMethod(),
                                      requestedCountryList,
                                      requestedBrowserList,
                                      requestedEnvironmentList));
    }

    List <IMethodInstance> allowedMethodsFinal = new ArrayList <IMethodInstance>();

    for (IMethodInstance methodInstance : allowedMethods) {
      // list of countries annotated on the test method.
      List <WTFCountry> enabledCountryList =
        new ArrayList <WTFCountry> (Arrays.asList(
            WTFCountryAnnotationReader.getIncludes(methodInstance.getMethod().getMethod())));
      Collections.sort(enabledCountryList);
      
      // list of excluded countries annotated on the test method.
      List <WTFCountry> excludedCountryList =
        new ArrayList <WTFCountry> (Arrays.asList(
            WTFCountryAnnotationReader.getExcludes(methodInstance.getMethod().getMethod())));
      Collections.sort(excludedCountryList);

      // If no country/site annotation found, set country US as the default one.
      /*if (enabledCountryList.size() == 0) {
        enabledCountryList.add(Country.US);
      }*/

      // list of browsers annotated on the test method.
      List <WTFBrowser> enabledBrowserList =
        new ArrayList <WTFBrowser> (Arrays.asList(
            WTFBrowserAnnotationReader.getIncludes(methodInstance.getMethod().getMethod())));
      Collections.sort(enabledBrowserList);

      // list of excluded browsers annotated on the test method.
      List <WTFBrowser> excludedBrowserList =
        new ArrayList <WTFBrowser> (Arrays.asList(
            WTFBrowserAnnotationReader.getExcludes(methodInstance.getMethod().getMethod())));
      Collections.sort(excludedBrowserList);
      
      if (Collections.binarySearch(enabledCountryList,
                                   ((BaseTestRetryAnalyzer)methodInstance
                                       .getMethod()
                                       .getRetryAnalyzer()).country) >= 0 && 
          Collections.binarySearch(excludedCountryList,
                                    ((BaseTestRetryAnalyzer)methodInstance
                                    	.getMethod()
                                         .getRetryAnalyzer()).country) < 0) {
        if (enabledBrowserList.size() > 0 || excludedBrowserList.size() > 0) {
          if (Collections.binarySearch(excludedBrowserList,
                  ((BaseTestRetryAnalyzer)methodInstance
                          .getMethod()
                          .getRetryAnalyzer()).browser) >= 0) {
                // skip adding into allowed metho list. 
        	  	//allowedMethodsFinal.add(methodInstance);
          } else if (Collections.binarySearch(enabledBrowserList,
                                       ((BaseTestRetryAnalyzer)methodInstance
                                           .getMethod()
                                           .getRetryAnalyzer()).browser) >= 0) {

            allowedMethodsFinal.add(methodInstance);
          } /*else if (Collections.binarySearch(enabledBrowserList, WTFBrowser.HTML_UNIT) >= 0) {
            ((BaseTestRetryAnalyzer)methodInstance
                .getMethod()
                .getRetryAnalyzer()).browser = WTFBrowser.HTML_UNIT;

            allowedMethodsFinal.add(methodInstance);
          }*/
        } else {
          allowedMethodsFinal.add(methodInstance);
        }
      }
    }

    return allowedMethodsFinal;
  }

  public List<IMethodInstance> duplicate(ITestNGMethod testNGMethod,
                                         List <WTFCountry> requestedCountryList,
                                         List <WTFBrowser> requestedBrowserList,
                                         List <Environment> requestedEnvironmentList) {
    List <IMethodInstance> allowedMethods = new ArrayList <IMethodInstance>();

    for (Environment environment : requestedEnvironmentList) {
      for (WTFCountry country : requestedCountryList) {
        for (WTFBrowser browser : requestedBrowserList) {
          final ITestNGMethod clonedTestNGMethod = testNGMethod.clone();
          final Object[] instances = testNGMethod.getTestClass().getInstances(true);
  
          BaseTestRetryAnalyzer bt = new BaseTestRetryAnalyzer();
          bt.environment = environment;
          bt.country = country;
          bt.browser = browser;
          clonedTestNGMethod.setRetryAnalyzer(bt);
  
          allowedMethods.add(new IMethodInstance() {
            public Object[] getInstances() {
              return instances;
            }
      
            public ITestNGMethod getMethod() {
              return clonedTestNGMethod;
            }
  
            public Object getInstance() {
              return instances;
            }
          });
        }
      }
    }
    return allowedMethods;
  }
}