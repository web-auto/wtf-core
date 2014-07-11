/**
 * Copyright (C) 2014 WTF org.
 */

package org.wtf.core.feature.args.converter;


/**
 * Server Environments.
 * 
 * @author venkatesan.sundramurthy@gmail.com (Venkatesan Sundramurthy)
 */
public class ServerEnvironment {
  public static enum Environment {
    QA, PROD, PREPROD;

    public static Environment getEnv(String string) {
      if (string.equalsIgnoreCase("qa")) {
        return Environment.QA;
      } else if (string.equalsIgnoreCase("prod")) {
        return Environment.PROD;
      } else if (string.equalsIgnoreCase("preprod") || string.equalsIgnoreCase("latest")) {
          return Environment.PREPROD;
      } else {
        return Environment.QA;
      }
    }
  }
}
