/**
 * Copyright (C) 2014 WTF org.
 */

package org.wtf.core.annotation.timeout;

import java.lang.reflect.Method;
import org.wtf.core.annotation.AnnotationReader;


/**
 * Test timeout annotation reader.
 * 
 * @author venkatesan.sundramurthy@gmail.com (Venkatesan Sundramurthy)
 */
public class TestTimeoutAnnotationReader {

  public static Integer getTestTimeout(Method javaMethod) {
    TestTimeout annotation = (TestTimeout) AnnotationReader.getValue(javaMethod,
                                                                     TestTimeout.class);
    return annotation != null ? annotation.seconds() : null;
  }
}
