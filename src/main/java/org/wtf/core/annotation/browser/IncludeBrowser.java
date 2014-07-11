/**
 * Copyright (C) 2014 WTF org.
 */

package org.wtf.core.annotation.browser;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import org.wtf.core.annotation.browser.WTFBrowser;



/**
 * Include browsers annotation.
 * 
 * @author venkatesan.sundramurthy@gmail.com (Venkatesan Sundramurthy)
 */
@Retention(value = RetentionPolicy.RUNTIME)
public @interface IncludeBrowser {
  WTFBrowser[] browsers();
}
