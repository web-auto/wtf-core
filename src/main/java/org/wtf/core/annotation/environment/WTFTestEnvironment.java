/**
 * Copyright (C) 2014 WTF org.
 */

package org.wtf.core.annotation.environment;


/**
 * Sites annotation.
 * 
 * @author venkatesan.sundramurthy@gmail.com (Venkatesan Sundramurthy)
 */
public enum WTFTestEnvironment {
  QA, PRE_PROD, PROD;

  public static WTFTestEnvironment getEnvFromEnvString(String envString) {
    if (envString.equalsIgnoreCase("qa")) {
      return WTFTestEnvironment.QA;
    } else if (envString.equalsIgnoreCase("prod")) {
      return WTFTestEnvironment.PROD;
    }    if (envString.equalsIgnoreCase("preprod")) {
      return WTFTestEnvironment.PRE_PROD;
    } else {
      return WTFTestEnvironment.QA;
    }
  }
}
