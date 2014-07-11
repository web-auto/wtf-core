/**
 * Copyright (C) 2014 WTF org.
 */

package org.wtf.core;

import static org.wtf.core.feature.logger.BaseLogger.LOG;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;

import org.wtf.core.annotation.browser.WTFBrowser;
import org.wtf.core.annotation.country.WTFCountry;
import org.wtf.core.feature.args.WTFTestArgs;
import org.wtf.core.feature.args.converter.ServerUrl;
import org.wtf.core.feature.args.converter.ServerEnvironment.Environment;
import org.wtf.core.feature.args.converter.ServerUrl.Server;
import org.wtf.core.WTFTestConfig;


/**
 * Webdriver test configuration for handling test flags.
 * 
 * @author venkatesan.sundramurthy@gmail.com (Venkatesan Sundramurthy)
 */
public class WTFTestConfig {
  public static Boolean initOnce = false;

  public static Properties sysProperties = null;
  public static final String NONE_TEXT = "none"; 
  public static final String PROD_ENVIRONMENT = "PROD";
  public static final String QA_ENVIRONMENT = "QA";
  public static final String DEFAULT_TEST_RETRY_COUNT = "0";
  public static final String DEFAULT_TEST_RERUN_COUNT = "1";
  public static final String TEST_SERVER_WAR_FILE = null;
  public static final String TEST_SERVER_PORT = "none";
  public static final String TEST_REPORT_EMAIL_HOST = "qa-ipmail01-d1.qa.ebay.com";
  public static final String TEST_REPORT_EMAIL_CC = "";
  public static final String TEST_REPORT_EMAIL_SUBJECT = "";
  public static final String TEST_REPORT_EMAIL_FROM = "wtf@wtf.org";

  public static final String DEFAULT_SITE = "US";
  public static final String DEFAULT_BROWSER = "firefox";

  // Test flag names
  public static final String flagServerEnvironment = "wtf.server.environment";
  public static final String flagServerURL = "wtf.server.url";
  public static final String flagServerHubURL = "wtf.server.hub.url";
  public static final String flagServerBackupHubURL = "wtf.server.backup.hub.url";
  public static final String flagServerTestRetry = "wtf.test.retry";
  public static final String flagBrowser = "wtf.browser";
  public static final String flagChromeDriver = "wtf.chrome.driver";
  public static final String flagTestReRun = "wtf.test.rerun";
  public static final String flagTestServerWar = "wtf.test.server.war";
  public static final String flagTestServerPort = "wtf.test.server.port";
  public static final String flagReportSuiteName = "wtf.report.suite.name";

  public static final String flagReportEmailEnable = "wtf.report.email.enable";
  public static final String flagReportEmailHost = "wtf.report.email.host";
  public static final String flagReportEmailCC = "wtf.report.email.cc";
  public static final String flagReportEmailFrom = "wtf.report.email.from";
  public static final String flagReportEmailSubject = "wtf.report.email.subject";
  
  public static final String flagReportDomainName = "wtf.report.domain.name";
  public static final String flagReportUsingBaseLogger = "wtf.report.using.baselogger";
  public static final String flagReportEnable = "wtf.reporter.enable";

  public static final String flagSites = "wtf.test.site";
  public static final String flagBrowsers = "wtf.test.browser";

  public static final String flagScreenShot = "wtf.test.screenshot";

  public static final String flagReportFailureByOwner = "wtf.report.failure.owner.enable";

  public static void updateSystemProperties(String propertyName, String propertyValue) {
    if (!System.getProperties().containsKey(propertyName)) {
      System.getProperties().setProperty(propertyName, propertyValue);
    }
  }

  /**
   * Returns the server Environment.
   */
  public static String getServerEnvironment() {
    return WTFTestArgs.commandLineArgs.environment.toString();
  }


  /**
   * Returns the server URL.
   */
  public static String getServerURL(String url) {
    if (WTFTestArgs.commandLineArgs.server == null) {
      return url;
    } else {
      return getServerURL(WTFCountry.US);
    }
  }
  
  public static String getUserAgent() {
    return WTFTestArgs.commandLineArgs.userAgent;
  }
  /**
   * Returns the server URL.
   */
  public static String getServerURL(WTFCountry site, String url) {
    if (WTFTestArgs.commandLineArgs.server == null) {
      return url;
    } else {
      return getServerURL(site);
    }
  }

  /**
   * Returns the server URL.
   */
  public static Boolean isServerOverride() {
    return WTFTestArgs.commandLineArgs.server == null ? false : true;
  }

  /**
   * Returns the server URL.
   */
  public static String getServerURL(WTFCountry site) {
    return ServerUrl.getServer(WTFTestArgs.commandLineArgs.wtfServers, site);
  }

  public static String getRemoteWebDriverHubURL() {
    return WTFTestArgs.commandLineArgs.grid? WTFTestArgs.commandLineArgs.gridUrl : null;
  }

  public static String getBackupRemoteWebDriverHubURL() {
    return WTFTestArgs.commandLineArgs.backupGrid? WTFTestArgs.commandLineArgs.backupGridUrl : null;
  }

  public static String getTestRetry() {
    return WTFTestArgs.commandLineArgs.retry.toString();
  }

  public static void setTestRetry(String testRetry) {
    WTFTestArgs.commandLineArgs.retry = Integer.parseInt(testRetry);
  }

  // legacy
  public static String getBrowser() {
    return null;
  }

  public static String getChromeDriver() {
    return "";
  }

  public static String getTestReRun() {
    return WTFTestArgs.commandLineArgs.rerun.toString();
  }

  public static String getSuiteName() {
    return WTFTestArgs.commandLineArgs.suiteName;
  }

  public static void setServerUrl(String url) {
    List <Server> servers = new ArrayList <Server>();
    servers.add(Server.US.setServer(url));
    WTFTestArgs.commandLineArgs.wtfServers = servers;
  }

  public static boolean isSmartLogEnabled() {
    return WTFTestArgs.commandLineArgs.smartLog;
  }

  public static boolean isEmailBrowserNameHidden() {
    return WTFTestArgs.commandLineArgs.emailHideBrowserName;
  }

  public static boolean reportViaEmailEnabled() {
    return WTFTestArgs.commandLineArgs.emailFailure;
  }

  public static String getCC() {
    return WTFTestArgs.commandLineArgs.emailCC;
  }

  public static String getSubject() {
    return WTFTestArgs.commandLineArgs.emailSubject;
  }

  public static boolean screenShotEnabled() {
    return WTFTestArgs.commandLineArgs.screenshot;
  }

  public static boolean reportFailureByOwnerEnabled() {
    return WTFTestArgs.commandLineArgs.printFailureByOwner != null ? true : false;
  }

  public static boolean reportConsolidatedFailureEnabled() {
    return WTFTestArgs.commandLineArgs.emailGroup != null ? true : false;
  }

  public static boolean reportConsolidatedOnlyIfFailedEnabled() {
    return WTFTestArgs.commandLineArgs.emailFailedOnly;
  }

  public static boolean reportConsolidatedFailureWithDescriptionOnlyEnabled() {
    return WTFTestArgs.commandLineArgs.emailDescriptiononly;
  }

  public static String getGroupEmailId() {
    return WTFTestArgs.commandLineArgs.emailGroup;
  }

  public static WTFCountry [] getSites() {
    if (WTFTestArgs.commandLineArgs.wtfSites.size() == 0) {
      /* Default Country is US */
      WTFTestArgs.commandLineArgs.wtfSites.add(WTFCountry.US);
    }
    return WTFTestArgs.commandLineArgs.wtfSites.toArray(new WTFCountry[WTFTestArgs.commandLineArgs.wtfSites.size()]);
  }

  public static WTFBrowser [] getBrowsers() {
    if (WTFTestArgs.commandLineArgs.wtfBrowsers.size() == 0) {
      /* Default Browser is HTML UNIT */
      WTFTestArgs.commandLineArgs.wtfBrowsers.add(WTFBrowser.HTML_UNIT);
    }
    return WTFTestArgs.commandLineArgs.wtfBrowsers.toArray(new WTFBrowser[WTFTestArgs.commandLineArgs.wtfBrowsers.size()]);
  }


  public static Environment [] getEnvironments() {
    if (WTFTestArgs.commandLineArgs.wtfEnvironments.size() == 0) {
      /* Default Test environment is QA */
      WTFTestArgs.commandLineArgs.wtfEnvironments.add(Environment.QA);
    }
    return WTFTestArgs.commandLineArgs.wtfEnvironments.toArray(new Environment[WTFTestArgs.commandLineArgs.wtfEnvironments.size()]);
  }

  public static Boolean isCSSLintEnabled() {
    return WTFTestArgs.commandLineArgs.cssLint;
  }

  public static void initFlags() {
    if (WTFTestConfig.initOnce) {
      return;
    }
    WTFTestConfig.initOnce = true;
    // put your init flags code here, this will get called before running any tests.
    LOG(Level.INFO, "Test Run Started..");

    WTFTestArgs.init();

    if (WTFTestArgs.commandLineArgs.grid) {
      LOG(Level.INFO, String.format("On Grid Mode. Using Grid: %s", WTFTestArgs.commandLineArgs.gridUrl));
    }
  }
}
