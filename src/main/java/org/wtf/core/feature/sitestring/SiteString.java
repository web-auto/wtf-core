/**
 * Copyright (C) 2014 WTF org.
 */

package org.wtf.core.feature.sitestring;


import java.util.HashMap;
import java.util.Map;

import org.wtf.core.WTFEnv;
import org.wtf.core.annotation.country.WTFCountry;


/**
 * Stores and retrieves country specific strings.
 */
public class SiteString {

  private Map <WTFCountry, String> map;

  public SiteString() {
    map = new HashMap <WTFCountry, String>();
  }

  public SiteString(Map <WTFCountry, String> map) {
    this();
    this.map = map;
  }

  public String get(WTFCountry site) {
    return getDefaultValue(site);
  }

  public String get(WTFEnv env) {
    return getDefaultValue(env.getSite());
  }

  public void set(WTFCountry site, String text) {
    map.put(site, text);
  }

  private String getDefaultValue(WTFCountry site) {
    return map.containsKey(site) ? map.get(site) : map.get(WTFCountry.US);
  }

  public SiteString US(String text) {
    map.put(WTFCountry.US, text);
    return this;
  }

  public SiteString UK(String text) {
    map.put(WTFCountry.UK, text);
    return this;
  }

  public SiteString DE(String text) {
    map.put(WTFCountry.DE, text);
    return this;
  }
  
  public SiteString AU(String text) {
    map.put(WTFCountry.AU, text);
    return this;
  }

  public SiteString CA(String text) {
    map.put(WTFCountry.CA, text);
    return this;
  }

  public SiteString CAFR(String text) {
    map.put(WTFCountry.CAFR, text);
    return this;
  }

  public SiteString IT(String text) {
    map.put(WTFCountry.IT, text);
    return this;
  }

  public SiteString ES(String text) {
    map.put(WTFCountry.ES, text);
    return this;
  }

  public SiteString FR(String text) {
    map.put(WTFCountry.FR, text);
    return this;
  }

  public static SiteString SS() {
    return new SiteString();
  }
}
