/**
 * Copyright (C) 2014 WTF org.
 */

package org.wtf.core.annotation.nameplus;

import java.lang.reflect.Method;

import org.testng.ITestResult;
import org.wtf.core.annotation.AnnotationReader;


/**
 * Test timeout annotation reader.
 * 
 * @author venkatesan.sundramurthy@gmail.com (Venkatesan Sundramurthy)
 */
public class TestNamePlusAnnotationReader {

  public static String getMethodNamePlus(Method javaMethod, ITestResult result) {
    TestNamePlus annotation = (TestNamePlus) AnnotationReader.getValue(javaMethod,
                                                                      TestNamePlus.class);
    if (annotation == null) {
      return "";
    }

    String plusName = " [ ";
    for (int position : annotation.name()) {
      if (position == 0  || result.getParameters().length < position) {
        continue;
      }
      plusName += result.getParameters()[position - 1].toString() + " ";
    }
    return plusName + "]";
  }
}
