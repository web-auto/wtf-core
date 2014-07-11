/**
 * Copyright (C) 2014 WTF org.
 */

package org.wtf.core.feature.tracking;

import java.util.HashMap;
import java.util.Map;

import org.openqa.selenium.WebDriver;


/**
 * Base eBay Tracking helper module.
 *
 * @author venkatesan.sundramurthy@gmail.com (Venkatesan Sundramurthy)
 */
public class EbayTracking {

  private static final String GLOBAL_TAGS_SEPARATOR = "GlobalTags:";
  private static final String SOJ_SEPARATOR = "SojournerDataKeyImpl:";
  private static final String CLASSIC_SOJ_SEPARATOR = "TrackingProperty:";
  private static final String FLAGS_TRACKING_SEPARATOR = "TrackingFlagImpl";

    private static final String FLAGS_TAGS_SEPARATOR = "flgs=";

  private static final String SOJ_A_TRACKING_START = "SOJ a";
  private static final String SOJ_A_TRACKING_END = FLAGS_TAGS_SEPARATOR;

  private static final String SOJ_K_TRACKING_START = "SOJ k";
  private static final String SOJ_K_TRACKING_END = "-->";
  private static final String SOJ_TRACKING_END = "-->";
  
  private static final String FLAGS_TRACKING_START = FLAGS_TAGS_SEPARATOR;
  private static final String FLAGS_TRACKING_END = "SOJ k";

  private String pageSource;
  private String sojString;
  private String sojKString;
  private String flagsString;
  private String url;
  private WebDriver driver;

  private Map<String, String> globalTagsMap;
  private Map<String, String> sojournerDataKeyImplMap;
  private Map<String, String> classicSojournerDataKeyImplMap;
  private Map<String, String> trackingFlagImpl;

  public EbayTracking(WebDriver driver, String url) {
    this.driver = driver;
    this.url = url;
    globalTagsMap = new HashMap<String, String>();
    sojournerDataKeyImplMap = new HashMap<String, String>();
    sojournerDataKeyImplMap = new HashMap<String, String>();
    classicSojournerDataKeyImplMap = new HashMap<String, String>();
  }

  public EbayTracking(WebDriver driver) {
    this(driver, null);
  }

  private void fetchData() {
    if (url != null) {
      driver.get(url);
    }
    pageSource = driver.getPageSource();

  }

  private void extractSojString() {
    int start = pageSource.indexOf(SOJ_A_TRACKING_START);
    if (start != -1) {
      int end = pageSource.indexOf(SOJ_TRACKING_END, start);
      if (end != -1) {
        sojString = pageSource.substring(start, end).trim();
      } else { //missing separator so go to the end; TODO better solution is to go to the next 
               //...separator instead of end
        //sojString = pageSource.substring(start).trim();
        
        start = pageSource.indexOf(SOJ_K_TRACKING_START);
        if (start != 1) {
          end = pageSource.indexOf(SOJ_K_TRACKING_END, start);
          if (end != -1) {
            sojString = pageSource.substring(start, end).trim();
          }
        }
      }
    }
  }

  private void extractSojKString() {
    int start = pageSource.indexOf(SOJ_K_TRACKING_START);
    if (start != -1) {
      int end = pageSource.indexOf(SOJ_K_TRACKING_END, start);
      sojKString = pageSource.substring(start, end).trim();
    }
  }

  private void extractFlagsString() {
    int start = pageSource.indexOf(FLAGS_TRACKING_START);
    if (start != -1) {
      int end = pageSource.indexOf(FLAGS_TRACKING_END, start);
      flagsString = pageSource.substring(start, end).trim();
    }
  }

  private Map<String, String> processFlagsTags(String separatorType) {
    Map<String, String> tagsMap = new HashMap<String, String>();
    String lines[] = flagsString.split("\\r?\\n");
    for (String line : lines) {
      if (line.indexOf(separatorType) != -1) {
        String[] keyVal = line.split("-");
        String value = keyVal[0].trim();
        String key = keyVal[1].split(":")[0].trim();
        tagsMap.put(key, value);
      }
    }
    return tagsMap;
  }

  public static Map<String, String> processSojTags(String data, String separatorType) {
    Map<String, String> tagsMap = new HashMap<String, String>();
    if (data == null) { return tagsMap; }

    String lines[] = data.split("\\r?\\n");
    for (String line : lines) {
      if (line.indexOf(separatorType) != -1) {
        String[] keyVal = line.split(separatorType)[1].split("=");
        keyVal[0] = keyVal[0].replace("!", "");
        if (keyVal.length < 2) {
          tagsMap.put(keyVal[0], " ");
        } else {
          tagsMap.put(keyVal[0], keyVal[1]);
        }
      }
    }
    return tagsMap;
  }

  private void processGlobalTags() {
    globalTagsMap.putAll(processSojTags(sojString, GLOBAL_TAGS_SEPARATOR));
    globalTagsMap.putAll(processSojTags(sojKString, GLOBAL_TAGS_SEPARATOR));    
  }

  private void processSojournerDataKeyImpl() {
    sojournerDataKeyImplMap.putAll(processSojTags(sojString, SOJ_SEPARATOR));
    sojournerDataKeyImplMap.putAll(processSojTags(sojKString, GLOBAL_TAGS_SEPARATOR));
  }

  private void processClassicSojournerDataKeyImpl() {
    classicSojournerDataKeyImplMap.putAll(processSojTags(sojString, CLASSIC_SOJ_SEPARATOR));
    classicSojournerDataKeyImplMap.putAll(processSojTags(sojKString, GLOBAL_TAGS_SEPARATOR));
  }

  private void processTrackingFlagImpl() {
    trackingFlagImpl = processFlagsTags(FLAGS_TRACKING_SEPARATOR);
  }

  public EbayTracking processSojTags() {
    fetchData();
    extractSojString();
    extractSojKString();
    processGlobalTags();
    processSojournerDataKeyImpl();
    processClassicSojournerDataKeyImpl();
    return this;
  }

  public EbayTracking processFlagsTags() {
    fetchData();
    extractFlagsString();
    processTrackingFlagImpl();
    return this;
  }

  public String getSojournerDataKeyImplValue(String key) {
    key = key.replace("!", "");
    return sojournerDataKeyImplMap.get(key);
  }

  public String getClassicSojournerDataKeyImplValue(String key) {
    key = key.replace("!", "");
    return classicSojournerDataKeyImplMap.get(key);
  }

  public String getGlobalTagsValue(String key) {
    key = key.replace("!", "");
    return globalTagsMap.get(key);
  }

  public String getTrackingFlagImplValue(String key) {
    return trackingFlagImpl.get(key);
  }
  
}
