/**
 * Copyright (C) 2014 WTF org.
 */

package org.wtf.core.annotation.environment;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.wtf.core.annotation.AnnotationReader;
import org.wtf.core.annotation.environment.WTFTestEnvironment;


/**
 * WTf Test Environment annotation reader.
 * 
 * @author venkatesan.sundramurthy@gmail.com (Venkatesan Sundramurthy)
 */
public class WTFTestEnvironmentAnnotationReader {

  public static WTFTestEnvironment [] getIncludes(Method javaMethod) {
    IncludeTestEnvironment annotation =
        (IncludeTestEnvironment) AnnotationReader.getValue(javaMethod, IncludeTestEnvironment.class);

    return (annotation == null) ? WTFTestEnvironment.values() : annotation.environments();
  }

  public static WTFTestEnvironment [] getExcludes(Method javaMethod) {
    ExcludeTestEnvironment annotation =
        (ExcludeTestEnvironment) AnnotationReader.getValue(javaMethod, ExcludeTestEnvironment.class);

    if (annotation == null) {
      List <WTFTestEnvironment> siteList = new ArrayList <WTFTestEnvironment>();
      return siteList.toArray(new WTFTestEnvironment[siteList.size()]);
    }
    return annotation.environments();
  }

  public static boolean has(WTFTestEnvironment [] environments,
      WTFTestEnvironment environment) {
    List <WTFTestEnvironment> enabledBrowserList =
        new ArrayList <WTFTestEnvironment> (Arrays.asList(environments));
    return Collections.binarySearch(enabledBrowserList, environment) >= 0 ? true : false;
  }
}
