/**
 * Copyright (C) 2014 WTF org.
 */

package org.wtf.core.annotation.country;


/**
 * WTF Country.
 * 
 * @author venkatesan.sundramurthy@gmail.com (Venkatesan Sundramurthy)
 */

  public enum WTFCountry {
    US("us"), UK("uk"), GB("gb"), DE("de"), AU("au"), CA("ca"), CAFR("cafr"), IT("it"),
    ES("es"), FR("fr"), AT("at"), IE("ie"), IN("in"), CH("ch");

    private String countryCode;

    private WTFCountry(String countRyCode) {
      this.countryCode = countRyCode;
    }

    public String getCountryCode() {
      return countryCode;
    }

    public static WTFCountry parse(String site) {
      for (WTFCountry country : WTFCountry.values()) {
        if (site.equalsIgnoreCase(country.countryCode)) {
          return country;
        }
      }
      return WTFCountry.US;
    }
  }
