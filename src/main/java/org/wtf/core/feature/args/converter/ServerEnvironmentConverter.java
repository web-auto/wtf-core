/**
 * Copyright (C) 2014 WTF org.
 */

package org.wtf.core.feature.args.converter;

import org.wtf.core.feature.args.converter.ServerEnvironment.Environment;

import com.beust.jcommander.IStringConverter;


/**
 * Converts server environment from sting to enum type.
 * 
 * @author venkatesan.sundramurthy@gmail.com (Venkatesan Sundramurthy)
 */
public class ServerEnvironmentConverter implements IStringConverter <Environment> {

  public Environment convert(String value) {
    return Environment.getEnv(value);
  }
}
