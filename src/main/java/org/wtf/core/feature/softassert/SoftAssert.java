/**
 * Copyright (C) 2014 WTF org.
 */

package org.wtf.core.feature.softassert;

import static org.wtf.core.feature.logger.BaseLogger.LOG;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.testng.Assert;
import org.testng.internal.Utils;


public class SoftAssert {

  private List<Throwable> softAssertList = new ArrayList<Throwable>();

  private void addSoftAsserts(Throwable e) {
    softAssertList.add(e);
  }

  public void assertTrue(boolean condition, String msg) {
    try {
      Assert.assertTrue(condition, msg);
    } catch(Throwable e) {
      addSoftAsserts(e);
    }
  }

  public void assertFalse(boolean condition, String msg) {
    try {
      Assert.assertFalse(condition, msg);
    } catch(Throwable e) {
      addSoftAsserts(e);
    }
  }

  public void assertEquals(Object actual, Object expected, String msg) {
    try {
      Assert.assertEquals(actual, expected, msg);
    } catch(Throwable e) {
      addSoftAsserts(e);
    }
  }

  public int count() {
    return softAssertList.size();
  }

  public Throwable get(int index) {
    return softAssertList.get(index);
  }

  public void flushSoftAsserts() {
    LOG(Level.INFO, String.format("Found Soft Asserts(%s)", count()));
    for (Throwable throwable : softAssertList) {
      if (throwable != null) {
        LOG(Level.SEVERE, Utils.stackTrace(throwable, false)[0]);
      }
    }
    LOG(Level.INFO, "End of Soft Asserts..");
  }
}
