/**
 * Copyright (C) 2014 WTF org.
 */

package org.wtf.core.feature.args;

import java.util.ArrayList;
import java.util.List;

import org.wtf.core.WTFConst;
import org.wtf.core.annotation.browser.WTFBrowser;
import org.wtf.core.annotation.country.WTFCountry;
import org.wtf.core.feature.args.converter.ServerEnvironment.Environment;
import org.wtf.core.feature.args.converter.ServerUrl.Server;

import com.beust.jcommander.Parameter;


/**
 * WTF command line args declarations.
 * 
 * @author venkatesan.sundramurthy@gmail.com (Venkatesan Sundramurthy)
 * @author mehpandey@ebay.com
 */
public class WTFCommandLineArgs {

  @Parameter(names = "-browser",
             description = "Comma separated browse names ex: -browser ff,chrome,ie")
  public List <String> browsers = new ArrayList <String>();

  @Parameter(names = "-country",
             description = "Comma separated country codes ex: -site us,uk,de,au,ca,cafr,it,es,fr")
  public List <String> sites = new ArrayList <String>();

  @Parameter(names = "-server",
             description = "Comma separated servers to use ex: us=>http://www.us.mypage.ebay.com")
  public List <String> server = null;

  @Parameter(names = "-pool",
      description = "The pool to use for running the tests ex: -pool fp280")
  public String pool = null;

  @Parameter(names = "-testenvironement",
             description = "Comma separated test environement to use for running the tests ex: -env qa,prod,preprod")
  public List <String> environment = new ArrayList <String>();

  @Parameter(names = "-rerun", description = "The numbers of times the given tests to be re ran")
  public Integer rerun = 1;

  @Parameter(names = "-retry", description = "The number of times a failed test should be retried")
  public Integer retry = 0;

  @Parameter(names = "-grid", description = "Enable grid support")
  public Boolean grid = false;

  @Parameter(names = "-gridurl", description = "Primary grid url to use")
  public String gridUrl = "http://grid.stratus.qa.ebay.com:8080/wd/hub";

  @Parameter(names = "-backupgrid", description = "Enable backup grid support")
  public Boolean backupGrid = false;

  @Parameter(names = "-backupgridurl", description = "Backup grid url to use")
  public String backupGridUrl = "http://qa-ci002.qa.ebay.com:8080/wd/hub";

  @Parameter(names = "-emailfailure",
             description = "Enable emailing feaure for sending test failures to owners")
  public Boolean emailFailure = false;

  @Parameter(names = "-emailhost", description = "Email host to use for sending outbound mails")
  public String emailHost = "qa-ipmail01-d1.qa.ebay.com";

  @Parameter(names = "-emailcc", description = "Email address to copy while sending failure mails")
  public String emailCC = WTFConst.EMPTY_STRING;

  @Parameter(names = "-emailgroup", description = "Email address to send the consolidated test failures")
  public String emailGroup = null;

  @Parameter(names = "-emaildescriptiononly",
             description = "Send test cases description instaed of test case name")
  public Boolean emailDescriptiononly = false;

  @Parameter(names = "-emailfailedonly",
      description = "Send email report only in case of test failures")
  public Boolean emailFailedOnly = false;

  @Parameter(names = "-emailsubject",
             description = "Email subject to use as a prefix while sending failure mails")
  public String emailSubject = WTFConst.EMPTY_STRING;

  @Parameter(names = "-emailfrom", description = "From address to use while sending failure mails")
  public String emailFrom = "dl-ebay-serengeti-admin@ebay.com";

  @Parameter(names = "-emailhidebrowsername", description = "In email report hides the browse name")
  public Boolean emailHideBrowserName = false;
  
  @Parameter(names = "-useragent", description = "Set the user agent ")
  public String userAgent = null;

  @Parameter(names = "-printowner", description = "Print test failures by owners at the console")
  public Boolean printFailureByOwner = true;

  @Parameter(names = "-epproxy", description = "The EP proxy url to be hit before running any tests")
  public String epProxy = null;

  @Parameter(names = "-gyro", description = "Enable GYRO reporting")
  public Boolean gyro = false;

  @Parameter(names = "-gyrodomainname", description = "Set GYRO Domain name")
  public String gyroDomainName = null;

  @Parameter(names = "-gyrosuitename", description = "Set GYRO suite name")
  public String gyroSuiteName = null;

  @Parameter(names = "-suitename", description = "Set Suite Name")
  public String suiteName = null;

  @Parameter(names = "-screenshot", description = "Enable screenshots for failed tests")
  public Boolean screenshot = true;

  @Parameter(names = "-smartlog",
             description = "Enable smart log for printing logs reported using wtf's LOG() method")
  public Boolean smartLog = false;

  @Parameter(names = "-csslint", description = "Enable CSS lint feature")
  public Boolean cssLint = false;
  
  @Parameter(names = "-highlightelement", description = 
             "Highlights the element using red color border. Note: Use it for Debugging purpose" +
             "as it waits 1 second after each element highight and eventually affects the test speed")
  public Boolean highLightElement = false;

  @Parameter(names =  "-threadcount", description = "Number of threads to use when running tests in parallel")
  public Integer threadCount = 1;

  @Parameter(names = "-internalBrowserObjectNeverUseThisFlag", hidden = true)
  public List <WTFBrowser> wtfBrowsers = new ArrayList <WTFBrowser>();

  @Parameter(names = "-internalServerObjectNeverUseThisFlag", hidden = true)
  public List <Server> wtfServers = new ArrayList <Server>();

  @Parameter(names = "-internalSiteObjectNeverUseThisFlag", hidden = true)
  public List <WTFCountry> wtfSites = new ArrayList <WTFCountry>();

  @Parameter(names = "-internalEnvironmentObjectNeverUseThisFlag", hidden = true)
  public List <Environment> wtfEnvironments = new ArrayList <Environment>();

  @Parameter(names = "-splkeyword", description = "Special keyword you want to pass as parameter & use in your test accordingly")
  public String splkeyword = null;

  @Parameter(names = "-se", description = "Search Engine (Voyger or Cassini)")
  public String searchEngine = "VOYGER";
}
