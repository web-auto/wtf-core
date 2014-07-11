/**
 * Copyright (C) 2014 WTF org.
 */

package org.wtf.core.feature.retry;

import static org.wtf.core.feature.logger.BaseLogger.LOG;

import java.util.logging.Level;

import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;
import org.testng.internal.Utils;
import org.wtf.core.WTFTest;
import org.wtf.core.WTFTestConfig;
import org.wtf.core.annotation.browser.WTFBrowser;
import org.wtf.core.annotation.country.WTFCountry;
import org.wtf.core.feature.args.converter.ServerEnvironment.Environment;



/**
 * Retry analyzer to retry the failed tests.
 */
public class BaseTestRetryAnalyzer implements IRetryAnalyzer {

  private int count = 1;
  private int maxCount = 1;

  public Environment environment = Environment.QA;
  public WTFBrowser browser = WTFBrowser.FIREFOX;
  public WTFCountry country = WTFCountry.US;

  public BaseTestRetryAnalyzer() {
    if (!WTFTest.initOnce) {
      WTFTestConfig.initFlags();
    }
    String retryMaxCount = WTFTestConfig.getTestRetry();
    if (retryMaxCount != null) {
      maxCount = Integer.parseInt(retryMaxCount);
    }
  }

  /**
   * Print the test status while test retry.
   */
  public boolean retry(ITestResult result) {
    Throwable throwable = result.getThrowable();

    String testClassName = String.format("%s.%s",
        result.getMethod().getRealClass().toString(), result.getMethod().getMethodName()).
            replace("class com.ebay.webdriver.", "..");

    if (throwable != null) {
      LOG(Level.SEVERE,
          "STACK TRACE: " + testClassName + "\n" + Utils.stackTrace(throwable, false)[0]);
    }

    if(count <= maxCount) {
      result.setAttribute("RETRY", new Integer(count));

      LOG(Level.WARNING, "[RETRYING] " + testClassName + " FAILED, " +
          "Retrying " + count + " times");

      count += 1;
      return true;
    }
    return false;
  }

  public void setRetryEnabledDisabledStatus(ITestResult result) {
    if (maxCount > 0) {
      result.setAttribute("RETRY_ENABLED", true);
    }
    if(count > maxCount) {
      result.setAttribute("FINAL_RETRY", true);
    }
  }
}

