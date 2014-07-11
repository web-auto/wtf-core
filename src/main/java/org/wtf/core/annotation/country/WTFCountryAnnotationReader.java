/**
 * Copyright (C) 2014 WTF org.
 */

package org.wtf.core.annotation.country;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.wtf.core.annotation.AnnotationReader;


/**
 * WTF Country annotation reader.
 * 
 * @author venkatesan.sundramurthy@gmail.com (Venkatesan Sundramurthy)
 */
public class WTFCountryAnnotationReader {

  public static WTFCountry[] getIncludes(Method javaMethod) {
    IncludeCountry annotation = 
        (IncludeCountry) AnnotationReader.getValue(javaMethod,IncludeCountry.class);
    return (annotation == null) ? WTFCountry.values() : annotation.countries();
  }

  public static WTFCountry[] getExcludes(Method javaMethod) {
    ExcludeCountry annotation =
        (ExcludeCountry) AnnotationReader.getValue(javaMethod,ExcludeCountry.class);

    if (annotation == null) {
      List<WTFCountry> siteList = new ArrayList<WTFCountry>();
      return siteList.toArray(new WTFCountry[siteList.size()]);
    }

    return annotation.countries();
  }

  public static boolean has(WTFCountry [] environments, WTFCountry environment) {
    List <WTFCountry> enabledBrowserList = new ArrayList <WTFCountry> (Arrays.asList(environments));
    return Collections.binarySearch(enabledBrowserList, environment) >= 0 ? true : false;
  }
}
