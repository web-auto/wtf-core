/**
 * Copyright (C) 2014 WTF org.
 */

package org.wtf.core.annotation.owner;

import java.lang.reflect.Method;
import org.wtf.core.annotation.AnnotationReader;


/**
 * Test owner annotation reader.
 * 
 * @author venkatesan.sundramurthy@gmail.com (Venkatesan Sundramurthy)
 */
public class TestOwnerAnnotationReader {

  public static String getTestOwner(Method javaMethod) {
    TestOwner annotation = (TestOwner) AnnotationReader.getValue(javaMethod,
                                                                TestOwner.class);
    return annotation != null ? annotation.email() : null;
  }
}
