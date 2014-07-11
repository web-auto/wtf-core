package org.wtf.core.annotation.environment;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Exclude test environment annotation.
 * 
 * @author venkatesan.sundramurthy@gmail.com (Venkatesan Sundramurthy)
 */
@Retention(value = RetentionPolicy.RUNTIME)
public @interface ExcludeTestEnvironment {
  WTFTestEnvironment[] environments();
}
