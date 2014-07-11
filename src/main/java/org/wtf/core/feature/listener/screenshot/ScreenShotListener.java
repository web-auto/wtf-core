/**
 * Copyright (C) 2014 WTF org.
 */

package org.wtf.core.feature.listener.screenshot;

import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.internal.IResultListener;

public class ScreenShotListener implements IResultListener {

  public void onFinish(ITestContext arg0) {}

  public void onStart(ITestContext arg0) {}

  public void onTestFailedButWithinSuccessPercentage(ITestResult arg0) {}

  public void onTestFailure(ITestResult arg0) {}

  public void onTestSkipped(ITestResult arg0) {}

  public void onTestStart(ITestResult arg0) {}

  public void onTestSuccess(ITestResult arg0) {}

  public void onConfigurationFailure(ITestResult arg0) {}

  public void onConfigurationSkip(ITestResult arg0) {}

  public void onConfigurationSuccess(ITestResult arg0) {}
}
