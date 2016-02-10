/**
 * Copyright (C) 2014 WTF org.
 */

package org.wtf.core.feature.api;

import static org.wtf.core.feature.logger.BaseLogger.LOG;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import org.wtf.core.BaseHttpClient;


/**
 * Base API client.
 * 
 * @author venkatesan.sundramurthy@gmail.com (Venkatesan Sundramurthy)
 */
public class BaseAPIClient extends BaseHttpClient {

  public BaseAPIClient() {
    super("http://eazye.qa.ebay.com/ws/api.dll");
  }
  public BaseAPIClient(String serverURL) {
    super(serverURL);
  }

  public String request(String requestXML) throws IOException {
    Map<String,String> propertiesMap = new HashMap<String, String>();
    propertiesMap.put("Content-Type", "application/xml ");
    String responseXML = super.request(propertiesMap, requestXML);
    LOG(Level.INFO, String.format("API Response: %s", responseXML));
    return responseXML;
  }
}
