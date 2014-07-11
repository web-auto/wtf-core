/**
 * Copyright (C) 2014 WTF org.
 */

package org.wtf.core;

import static org.wtf.core.WTFTest.getWebdriverFriendlyData;

import org.testng.ITestContext;


/**
 * Base data provider.
 * 
 * @author venkatesan.sundramurthy@gmail.com (Venkatesan Sundramurthy)
 */
public class BaseDataProvider {

  public static interface IDataProvider { }

  public static Object[][] getDataProviderFromEnum(IDataProvider dataProvider[], int driverCount,
      ITestContext context) {
    Object[][] testData = (Object[][]) new Object[dataProvider.length][1];
    int index = 0;
    for (IDataProvider dp : dataProvider) {
      testData[index++][0] = dp;
    }
    return getWebdriverFriendlyData(testData, driverCount, context);
  }
  
  public static Object[][] getDataProviderFromEnum(IDataProvider dataProvider1[],
      IDataProvider dataProvider2[], ITestContext context) {
    final int total = dataProvider1.length * dataProvider2.length;
    final Object[][] testData = (Object[][]) new Object[total][2];
    
    int index = 0;
    for (IDataProvider dp1 : dataProvider1) {
      for (IDataProvider dp2 : dataProvider2) {
        testData[index][0] = dp1;
        testData[index][1] = dp2;
        
        index++;
      }
    }
    assert (index == total);
    
    return getWebdriverFriendlyData(testData, context);
  }
  
  public static Object[][] getDataProviderFromEnum(IDataProvider idp, IDataProvider dataProvider2[],
      ITestContext context) {
    final int total = dataProvider2.length;
    final Object[][] testData = (Object[][]) new Object[total][2];

    int index = 0;
    for (IDataProvider dp2 : dataProvider2) {
        testData[index][0] = idp;
        testData[index][1] = dp2;
        index++;
    }
    assert (index == total);
    
    return getWebdriverFriendlyData(testData, context);
  }

  public static Object[][] getDataProviderFromEnum(IDataProvider dataProvider1[],
      IDataProvider dataProvider2[], int driverCount, ITestContext context) {
    Object[][] testData = (Object[][]) new Object[dataProvider1.length][2];
    int index = 0;
    for (IDataProvider dp : dataProvider1) {
      testData[index][0] = dp;
      testData[index][1] = dataProvider2[index];
      index++;
    }
    return getWebdriverFriendlyData(testData, driverCount, context);
  }

  public static Object[][] getDataProviderFromEnum(IDataProvider dataProvider[],
      ITestContext context) {
    return getDataProviderFromEnum(dataProvider, 1, context);
  }
}
