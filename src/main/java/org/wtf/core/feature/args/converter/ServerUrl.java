/**
 * Copyright (C) 2014 WTF org.
 */

package org.wtf.core.feature.args.converter;

import java.util.List;

import org.wtf.core.annotation.country.WTFCountry;


/**
 * Service URLs.
 * 
 * @author venkatesan.sundramurthy@gmail.com (Venkatesan Sundramurthy)
 */
public class ServerUrl {
  public static enum Server {
    US(WTFCountry.US, null),
    UK(WTFCountry.UK, null),
    DE(WTFCountry.DE, null),
    AU(WTFCountry.AU, null),
    CA(WTFCountry.CA, null),
    CAFR(WTFCountry.CAFR, null),
    IT(WTFCountry.IT, null),
    ES(WTFCountry.ES, null),
    FR(WTFCountry.FR, null),
    CH(WTFCountry.CH, null),
    IN(WTFCountry.IN, null);

    public WTFCountry site;
    public String url;

    private Server(WTFCountry site, String url) {
      this.site = site;
      this.url = url;
    }

    public Server setServer(String url) {
      this.url = url;
      return this;
    }

    public static Server getServer(WTFCountry site) {
      for (Server server : Server.values()) {
        if (server.site.equals(site)) {
          return server;
        }
      }
      return null;
    }
  }

  public static String getServer(List <Server> servers, WTFCountry site) {
    for (Server server : servers) {
      if (server.site.equals(site)) {
        return server.url;
      }
    }
    return null;
  }
}
