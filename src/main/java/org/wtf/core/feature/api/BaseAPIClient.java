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
    propertiesMap.put("X-EBAY-API-CERT-NAME", "AdminCertificate");
    propertiesMap.put("X-EBAY-API-CALL-NAME", "AddItem");
    propertiesMap.put("X-EBAY-API-SESSION-CERTIFICATE", "Admintest1;AdminApp;AdminCertificate");
    propertiesMap.put("X-EBAY-API-SITEID", "100");
    propertiesMap.put("X-EBAY-API-COMPATIBILITY-LEVEL", "739");
    propertiesMap.put("X-EBAY-API-DETAIL-LEVEL", "0");
    propertiesMap.put("X-EBAY-API-DEV-NAME", "Admintest1");
    propertiesMap.put("X-EBAY-API-FLAGS", "SDK1.60.2358.27060");
    propertiesMap.put("X-EBAY-API-APP-NAME", "AdminApp");
    propertiesMap.put("X-EBAY-API-RESPONSE-ENCODING", "XML");
    String responseXML = super.request(propertiesMap, requestXML);
    LOG(Level.INFO, String.format("API Response: %s", responseXML));
    return responseXML;
  }
}
