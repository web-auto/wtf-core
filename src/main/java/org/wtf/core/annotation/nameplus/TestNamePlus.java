/**
 * Copyright (C) 2014 WTF org.
 */

package org.wtf.core.annotation.nameplus;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;


/**
 * Sites annotation.
 * 
 * @author venkatesan.sundramurthy@gmail.com (Venkatesan Sundramurthy)
 */
@Retention(value = RetentionPolicy.RUNTIME)
public @interface TestNamePlus {
  int[] name();
}

