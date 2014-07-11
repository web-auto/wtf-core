/**
 * Copyright (C) 2014 WTF org.
 */

package org.wtf.core.annotation.country;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;


/**
 * Exclude country annotation.
 * 
 * @author venkatesan.sundramurthy@gmail.com (Venkatesan Sundramurthy)
 */
@Retention(value = RetentionPolicy.RUNTIME)
public @interface ExcludeCountry {	
  WTFCountry[] countries();
}
