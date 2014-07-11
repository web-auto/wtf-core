/**
 * Copyright (C) 2014 WTF org.
 */

package org.wtf.core.annotation.browser;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.wtf.core.annotation.AnnotationReader;
import org.wtf.core.annotation.browser.WTFBrowser;


/**
 * Test timeout annotation reader.
 * 
 * @author venkatesan.sundramurthy@gmail.com (Venkatesan Sundramurthy)
 */
public class WTFBrowserAnnotationReader {

  public static WTFBrowser [] getIncludes(Method javaMethod) {
    IncludeBrowser annotation = 
        (IncludeBrowser) AnnotationReader.getValue(javaMethod, IncludeBrowser.class);
    return (annotation == null) ? WTFBrowser.values() : annotation.browsers();
  }

  public static WTFBrowser [] getExcludes(Method javaMethod) {
    ExcludeBrowser annotation =
        (ExcludeBrowser) AnnotationReader.getValue(javaMethod, ExcludeBrowser.class);

    if (annotation == null) {
      List<WTFBrowser> siteList = new ArrayList<WTFBrowser>();
      return siteList.toArray(new WTFBrowser[siteList.size()]);
    }

    return annotation.browsers();
  }

  public static boolean has(WTFBrowser [] list, WTFBrowser item) {
    List <WTFBrowser> enabledBrowserList = new ArrayList <WTFBrowser> (Arrays.asList(list));
    return Collections.binarySearch(enabledBrowserList, item) >= 0 ? true : false;
  }
}
