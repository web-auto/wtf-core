/**
 * Copyright (C) 2014 WTF org.
 */

package org.wtf.core.annotation.owner;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;


/**
 * Test Owner annotation.
 * 
 * @author venkatesan.sundramurthy@gmail.com (Venkatesan Sundramurthy)
 */
@Retention(value = RetentionPolicy.RUNTIME)
public @interface TestOwner {
  String email();
}
