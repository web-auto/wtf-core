package org.wtf.core.annotation.environment;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Include test environment annotation.
 * 
 * @author venkatesan.sundramurthy@gmail.com (Venkatesan Sundramurthy)
 */
@Retention(value = RetentionPolicy.RUNTIME)
public @interface IncludeTestEnvironment {
  WTFTestEnvironment[] environments();
}
