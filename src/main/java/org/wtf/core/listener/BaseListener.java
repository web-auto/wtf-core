/**
 * Copyright (C) 2014 WTF org.
 */

package org.wtf.core.listener;

import static org.wtf.core.feature.logger.BaseLogger.LOG;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import org.apache.commons.lang.time.FastDateFormat;
import org.testng.IReporter;
import org.testng.IResultMap;
import org.testng.IRetryAnalyzer;
import org.testng.ISuite;
import org.testng.ISuiteResult;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;
import org.testng.internal.Utils;
import org.testng.xml.XmlSuite;
import org.wtf.core.WTFEnv;
import org.wtf.core.WTFTest;
import org.wtf.core.WTFTestConfig;
import org.wtf.core.annotation.browser.WTFBrowser;
import org.wtf.core.annotation.nameplus.TestNamePlusAnnotationReader;
import org.wtf.core.annotation.owner.TestOwnerAnnotationReader;
import org.wtf.core.feature.emailer.TestNGEmailSender;
import org.wtf.core.feature.retry.BaseTestRetryAnalyzer;
import org.wtf.core.feature.xmljunit.XmlJuintReport;


/**
 * A custom listener to print the test status live.
 * 
 * @author venkatesan.sundramurthy@gmail.com (Venkatesan Sundramurthy)
 */
public class BaseListener implements ITestListener, IReporter {

  public static Date testStartTime = new Date();
  public int totalTestCount;
  public int testCountDec;

  private static String runId = null;

  public synchronized void Init(ISuite suite) {
    if (runId == null) {
      String stepGuid = System.getProperty("stepguid");
      if (stepGuid != null && !stepGuid.isEmpty()) {
        BaseListener.runId = String.format("%s", stepGuid);
      } else {
        String tsGuid = System.getProperty("tsguid");
        if (tsGuid == null || stepGuid.isEmpty()) {
          tsGuid = suite.getName().replace(" ", "_");
        }
        //SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd_MMMMM_yyyy_hh_mm_aaa");

        BaseListener.runId =
            String.format("%s_%s", tsGuid,
                          FastDateFormat.getInstance("dd_MMMMM_yyyy_hh_mm_aaa").format(new Date()));
      }
    }
  }

  public static String getTestMethodNameinPackageStyle(ITestResult result) {
    String logTextPart = String.format("%s.%s",
        result.getMethod().getRealClass().toString(), result.getMethod().getMethodName());
    return logTextPart;
  }

  public static BaseTestRetryAnalyzer getIRA(ITestResult result) {
    IRetryAnalyzer ira = result.getMethod().getRetryAnalyzer();
    if (ira instanceof BaseTestRetryAnalyzer) {
      ira = (BaseTestRetryAnalyzer)ira;
    } else {
      ira = new BaseTestRetryAnalyzer();
    }
    return (BaseTestRetryAnalyzer) ira;
  }

  public static String getBrowserName(ITestResult result) {
    WTFBrowser browser = WTFTest.getCurrentBrowser(result.getTestContext(), result);
    return browser.toString();
  }

  public static String getSiteName(ITestResult result) {
    return getIRA(result).country.toString();
  }

  public void printStatus(int status, ITestResult result) {
    if (result.getAttribute("DRIVER_FAILED") != null) {
      return;
    }
    String logTextPart = getTestMethodNameinPackageStyle(result);
    String testRunTime = formatTestRunTime(result.getEndMillis() - result.getStartMillis());
    String browser = getBrowserName(result);
    String site = getSiteName(result);

    switch(status) {
    case ITestResult.SUCCESS: 
      LOG(Level.INFO, String.format("[PASSED] [%s] [%s] %s%s", browser, site, testRunTime, logTextPart));
      break;
    case ITestResult.FAILURE: 
      LOG(Level.INFO, String.format("[FAILED] [%s] [%s] %s%s", browser, site, testRunTime, logTextPart));
      break;
    case ITestResult.SKIP:
      LOG(Level.INFO, String.format("[SKIPPED] [%s] [%s ]%s%s", browser, site, testRunTime, logTextPart));
      break;
    case 4: break;
    case 16:
      int counter = totalTestCount - (totalTestCount - ++testCountDec);
      if (counter > totalTestCount) {
        counter = totalTestCount;
      }
      LOG(Level.INFO, String.format("[STARTING] [%s] [%s] %s", browser, site, logTextPart));
      break;
    case 32:
      LOG(Level.INFO, String.format("[PASSED_WITH_RETRY] [%s] [%s] %s%s", browser, site, testRunTime, logTextPart));
      break;
    case 100:
      LOG(Level.INFO, String.format("[GYRO LOGGING][%s] [%s] %s", browser, site, logTextPart));
      break;
    default: break;
    }
  }


  public void onFinish(ITestContext testContext){
    LOG(null, "");
    LOG(Level.INFO, String.format("Finished Executing Test Set \"%s\" On Suite \"%s\"",
        testContext.getName(), testContext.getSuite().getName()));
  }


  public void onStart(ITestContext testContext) {
    LOG(Level.INFO, String.format("Started Executing Test Set \"%s\" On Suite \"%s\"",
        testContext.getName(), testContext.getSuite().getName()));
    LOG(null, "");
    totalTestCount = -1;
    testCountDec = 0;
    totalTestCount = testContext.getAllTestMethods().length;
  }

  

  public void onStart(ISuite suite) {
    Init(suite);
  }


  public void onFinish(ISuite suite) {
  }


  public void onTestStart(ITestResult result) {
    printStatus(result.getStatus(), result);
  }


  public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
    printStatus(result.getStatus(), result);
  }


  public void onTestFailure(ITestResult result) {
    printStatus(result.getStatus(), result);
    storeLogs(result);
  }


  public void onTestSkipped(ITestResult result) {
    printStatus(result.getStatus(), result);
    storeLogs(result);
  }


  public void onTestSuccess(ITestResult result) {
    printStatus(result.getStatus(), result);
    if (result.getAttribute("DRIVER_FAILED") == null) {
      logResultsToPipeline(result);
    }
    storeLogs(result);
  }

  private void storeLogs(ITestResult testResult) {
    //TODO (Venkat)
    //WTFDashLogUtil.setTestLogsForWTFDash(WTFDashThreadPoolSafeLogger.flushLogs(), testResult);
  }

  private void logAllValidFailuresToPipline(java.util.List<ISuite> suites) {
    //Reporter.startRun(runId);
    for (ISuite suite : suites) {
      for (ISuiteResult result : suite.getResults().values()) {
        for (ITestResult failedResult : result.getTestContext().getFailedTests().getAllResults()){
          if (failedResult.getAttribute("RETRY") != null) {
            continue;
          }
          String browser = getBrowserName(failedResult);
          String site = getSiteName(failedResult);
          LOG(Level.SEVERE, String.format("[STACK TARCE] for [%s] [%s]%s \n %s",
                                          browser,
                                          site,
                                          getTestMethodNameinPackageStyle(failedResult),
                                          getDetailedStackTrace(failedResult)));
          logResultsToPipeline(failedResult);
        }
      }
    }
  }

  private synchronized void logResultsToPipeline(ITestResult result){
  }

  public boolean methodParameterEqual(ITestResult result1, ITestResult result2) {
    if (result1.getParameters().length != result2.getParameters().length) {
      return false;
    }

    Object [] paramListA = result1.getParameters();
    Object [] paramListB = result2.getParameters();
    for (int index = 0; index < result1.getParameters().length; index++) {
      if (paramListA[index] instanceof WTFEnv) {
        continue;
      }
      if (!paramListA[index].equals(paramListB[index])) {
        return false;
      }
    }
    return true;
  }

  private boolean hasFailedBefore(IResultMap failureList, ITestResult passedResult) {
    for (ITestResult result : failureList.getAllResults()) {
      String failedTest =
          result.getMethod().getRealClass().toString()+ "." + result.getMethod().getMethodName();
      String passedTest = passedResult.getMethod().getRealClass().toString() + "." +
          passedResult.getMethod().getMethodName();
      
      if (failedTest.equals(passedTest) && methodParameterEqual(result, passedResult)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Prints the test results in order. 
   */
  private long processTestResults(ISuiteResult suiteResult, IResultMap map, String status) {
    String statusBackup = status;
    List<String> methodList = new LinkedList<String>();

    for (ITestResult result : map.getAllResults()) {

      status = statusBackup;
      if (result.getAttribute("DRIVER_FAILED") != null) {
        map.removeResult(result.getMethod());
        continue;
      }

      if (result.getAttribute("RETRY") != null && status.equals("FAILED_AFTER_RETRY")) {
        map.removeResult(result.getMethod());
        continue;
      }

      String retryText = "";
      if (result.getAttribute("RETRY") != null) {
        retryText = String.format(" [Retry %s]",
            Integer.toString((Integer)result.getAttribute("RETRY")));
      }

      if (status.equals("PASSED") && hasFailedBefore(suiteResult.getTestContext().getFailedTests(),
          result) && Integer.parseInt(WTFTestConfig.getTestRetry()) > 0) {
        status = "PASSED_AFTER_RETRY";
      }

      if (status.equals("FAILED_AFTER_RETRY") &&
          WTFTestConfig.getTestRetry().equals("0")) {
        status = "FAILED";
      }

      String testClassName =
          result.getMethod().getRealClass().toString().replace("class com.", "com.");
      String namePlus =
          TestNamePlusAnnotationReader.getMethodNamePlus(result.getMethod().getMethod(), result);

      String browser = getBrowserName(result);
      String site = getSiteName(result);

      methodList.add(String.format("[%s] [%s] [%s] %s.%s%s%s%s",
                     status, browser, site, testClassName, result.getMethod().getMethodName(),
                     namePlus, retryText,
                     formatTestRunTime(result.getEndMillis() - result.getStartMillis())));
    }
    Collections.sort(methodList);
    for (String result : methodList) {
      LOG(Level.INFO, result);
    }
    return methodList.size();
  }

  public String formatTestRunTime(long testTimeInMillis) {
    String dateFormatted = "";
    long minutes = TimeUnit.MILLISECONDS.toMinutes(testTimeInMillis);
    long seconds = TimeUnit.MILLISECONDS.toSeconds(testTimeInMillis) - 
        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(testTimeInMillis));
    NumberFormat formatter = new DecimalFormat("00");
    if (minutes > 0 ) {
      dateFormatted = String.format(" [%s:%s Sec] ", formatter.format(minutes),
          formatter.format(seconds));
    } else if (seconds > 0) {
      dateFormatted = String.format(" [%s Sec] ", formatter.format(seconds));
    }
    return dateFormatted;
  }

  public String getTimeElapsed(long millis) {
    long hour = TimeUnit.MILLISECONDS.toHours(millis);
    long mins = TimeUnit.MILLISECONDS.toMinutes(millis) -
                TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis));
    long secs = TimeUnit.MILLISECONDS.toSeconds(millis) - 
                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis));
    String elapsedTime = "";

    if (hour > 0) {
      elapsedTime += String.format("%s Hours ", hour);
    }

    if (mins > 0) {
      elapsedTime += String.format("%s Minutes ", mins);
    }

    if (secs > 0) {
      elapsedTime += String.format("%s Seconds ", secs);
    }

    return elapsedTime;
  }

  public void generateReport(java.util.List<XmlSuite> xmlSuites, java.util.List<ISuite> suites,
      java.lang.String outputDirectory) {
    logAllValidFailuresToPipline(suites);
    long passedCount = 0;
    long failedCount = 0;
    long SkippedCount = 0;
    Date testEndTime = new Date();

    LOG(null, "");
    LOG(Level.INFO, "TEST RESULTS:");
    LOG(Level.INFO, "============\n");

    for (ISuite suite : suites) {
      for (ISuiteResult result : suite.getResults().values()) {
        LOG(Level.INFO, String.format("Test Set: %s\n", result.getTestContext().getName()));
        passedCount += 
            processTestResults(result, result.getTestContext().getPassedTests(), "PASSED");

        failedCount += processTestResults(result, result.getTestContext().getFailedTests(),
            "FAILED_AFTER_RETRY");
        TestNGEmailSender.sendFailureEmails(result.getTestContext().getFailedTests());

        SkippedCount +=
            processTestResults(result, result.getTestContext().getSkippedTests(), "SKIPPED");

        LOG(null, "");
        XmlJuintReport.generateXmlReport(result.getTestContext(), outputDirectory);
      }
    }

    long total = passedCount + failedCount + SkippedCount;
    LOG(Level.INFO, String.format("Total Test:%s, Passed:%s, Failed:%s, Skipped:%s\n",
                                  total, passedCount, failedCount, SkippedCount));

    //LOG(Level.INFO, "Test Started : " + new SimpleDateFormat("HH:mm ss-SSS").format(testStartTime));
    //LOG(Level.INFO, "Test Finished: " + new SimpleDateFormat("HH:mm ss-SSS").format(testEndTime));

    LOG(Level.INFO, String.format("Test Started : %s",
                                  FastDateFormat.getInstance("HH:mm ss-SSS").format(testStartTime)));
    LOG(Level.INFO, String.format("Test Finished: %s",
                                  FastDateFormat.getInstance("HH:mm ss-SSS").format(testEndTime)));

    long millis = testEndTime.getTime() - testStartTime.getTime(); 
    LOG(Level.INFO, "Test Run Time: " + getTimeElapsed(millis));

    LOG(null, "");

    if (WTFTestConfig.reportFailureByOwnerEnabled()) {
      generateFailureReportByOwner(xmlSuites, suites, outputDirectory);
      generateConsolidatedReportByOwner(xmlSuites, suites, outputDirectory);
    }
  }

  public void generateFailureReportByOwner(java.util.List<XmlSuite> xmlSuites,
      java.util.List<ISuite> suites, java.lang.String outputDirectory) {

    Map<String, List<String>> ownersOfFailedTests = new HashMap<String, List<String>>();

    for (ISuite suite : suites) {
      for (ISuiteResult result : suite.getResults().values()) {
        
        for (ITestResult testResult : result.getTestContext().getFailedTests().getAllResults()) {

            String owner =
                TestOwnerAnnotationReader.getTestOwner(testResult.getMethod().getMethod());
            List<String> ownerResults = ownersOfFailedTests.get(owner);
            if (null == ownerResults) {
              ownerResults = new ArrayList<String>();
              ownersOfFailedTests.put(owner, ownerResults);
            }

            String namePlus =
                TestNamePlusAnnotationReader.getMethodNamePlus(testResult.getMethod().getMethod(),
                                                              testResult);
            String browser = getBrowserName(testResult);
            String site = getSiteName(testResult);

            ownersOfFailedTests.get(owner).add(String.format("[%s] [%s] %s.%s%s", browser, site,
                                               testResult.getTestClass().getName(),
                                               testResult.getName(), namePlus));
        }
      }
    }

    if (ownersOfFailedTests.size() > 0) {
      LOG(Level.INFO, "TEST FAILURES BY OWNER:");
      LOG(Level.INFO, "======================\n");

      for (Map.Entry<String, List<String>> entry : ownersOfFailedTests.entrySet()) {
        String email = entry.getKey();
        List<String> failedTests = entry.getValue();
        Collections.sort(failedTests);
        
        LOG(Level.INFO, email + ":");
        for (String failedTest : failedTests) {
          LOG(Level.INFO, failedTest);
        }
        LOG(null, "");
      }
    }
  }

  public void generateConsolidatedReportByOwner(java.util.List<XmlSuite> xmlSuites,
      java.util.List<ISuite> suites, java.lang.String outputDirectory) {

    Map<String, List<ITestResult>> ownersOfFailedTests = new HashMap<String, List<ITestResult>>();
    Map<String, List<ITestResult>> ownersOfPassedTests = new HashMap<String, List<ITestResult>>();

    for (ISuite suite : suites) {
      for (ISuiteResult result : suite.getResults().values()) {
        ownersOfPassedTests = getTestByOwnerSiteBrowser(result.getTestContext().getPassedTests().getAllResults());
        ownersOfFailedTests = getTestByOwnerSiteBrowser(result.getTestContext().getFailedTests().getAllResults());
      }
    }

    // Send consolidated failures to the Team group email id.
    TestNGEmailSender.sendConsolidatedFailureEmail(ownersOfPassedTests, ownersOfFailedTests);
  }

  private static Map<String, List<ITestResult>> getTestByOwnerSiteBrowser(Set <ITestResult> results) {
    Map<String, List<ITestResult>> testOwners = new HashMap<String, List<ITestResult>>();
    for (ITestResult tr : results) {
      String owner = TestOwnerAnnotationReader.getTestOwner(tr.getMethod().getMethod());
      String browser = getBrowserName(tr);
      String site = getSiteName(tr);
      String uniqueString = owner + "#" + browser + "#" + site;

      List<ITestResult> res = testOwners.get(uniqueString);
      if (res == null) {
        testOwners.put(uniqueString, new ArrayList<ITestResult>());
      }
      testOwners.get(uniqueString).add(tr);
    }
    return testOwners;
  }

  public boolean hasGroup(ITestResult tr, String groupName) {
    for (String group : tr.getMethod().getGroups()) {
      if (group.equals(groupName)) {
        return true;
      }
    }
    return false;
  }

  protected String getSuiteName(ITestResult tr) {
    return WTFEnv
        .getWebdriverEnvironmentFromObjectList(tr.getParameters())
        .getTestContext()
        .getName(); 
  }

  public static String getDetailedStackTrace(ITestResult tr) {
    String failureTrace = "";
    Throwable throwable = tr.getThrowable();
    if (throwable != null) {
      failureTrace = Utils.stackTrace(throwable, false)[0];
    } else {
      failureTrace = "No Stack Trace Found.";
    }
    return failureTrace;
  }

  private String changeNullToEmpty(String value) {
    try {
      if(value.equals(null) || value == null) {
        value= "";
      }
    } catch(Exception e) { }
    return value;
  }
}
