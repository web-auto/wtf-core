/**
 * Copyright (C) 2014 WTF org.
 */

package org.wtf.core.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.BeforeTest;


/**
 * Test timeout anatation reader.
 * 
 * @author venkatesan.sundramurthy@gmail.com (Venkatesan Sundramurthy)
 */
public class AnnotationReader {

  public static Annotation getValue(Method javaMethod,
                                    Class <? extends Annotation > annotationClass) {
    Annotation annotation = javaMethod.getAnnotation(annotationClass);
    if (annotation == null) {
      boolean skip = false;
      // Filter out the usual Annotations.
      Annotation[] annots = javaMethod.getAnnotations();
      for (Annotation an : annots) {
        if (an.annotationType().equals(BeforeMethod.class) ||
            an.annotationType().equals(AfterMethod.class) ||
            an.annotationType().equals(BeforeSuite.class) ||
            an.annotationType().equals(AfterSuite.class) ||
            an.annotationType().equals(BeforeTest.class) ||
            an.annotationType().equals(AfterTest.class)) {
            skip = true;
            break;
        }
      }
      if (!skip) {
        annotation = javaMethod.getDeclaringClass().getAnnotation(annotationClass);
      }
    }
    return annotation;
  }
}
