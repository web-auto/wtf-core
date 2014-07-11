/**
 * Copyright (C) 2014 WTF org.
 */

package org.wtf.core.feature.emailer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.testng.IResultMap;
import org.testng.ITestResult;
import org.wtf.core.WTFAttribute;
import org.wtf.core.WTFTestConfig;
import org.wtf.core.annotation.owner.TestOwnerAnnotationReader;
import org.wtf.core.feature.args.WTFTestArgs;
import org.wtf.core.listener.BaseListener;

import org.apache.commons.lang.StringUtils;


public class TestNGEmailSender {

  public static String getTestReport(ITestResult result) {
    String value = "<p>" + BaseListener.getTestMethodNameinPackageStyle(result) + "</p>";
    return value;
  }

  public synchronized static void sendConsolidatedFailureEmail(
      Map<String, List<ITestResult>> ownersOfPassedTests,
      Map<String, List<ITestResult>> ownersOfFailedTests) {

    if (!WTFTestConfig.reportConsolidatedFailureEnabled()) {
      return;
    }

    if (WTFTestConfig.reportConsolidatedOnlyIfFailedEnabled() && ownersOfFailedTests.size() == 0) {
      return;
    }

    Set<String> ownerList = new HashSet <String>(new HashSet <String>(ownersOfPassedTests.keySet()));
    ownerList.addAll(new HashSet <String>(ownersOfFailedTests.keySet()));

    TreeSet <String> keyList = new TreeSet<String> (ownerList); 

    StringBuilder sb = new StringBuilder();
    sb.append("<style>");
    sb.append("table{background:#D3E4E5;border:1px solid gray;border-collapse:collapse;color:#fff;font:normal 10.5px verdana, arial, helvetica, sans-serif}");
    sb.append("caption{border:1px solid #5C443A;color:#5C443A;font-weight:700;text-align:left;text-transform:uppercase;padding:6px 4px 8px 0}");
    sb.append("td,th{color:#363636;padding:.4em}");
    sb.append("tr{border:1px dotted gray}");
    sb.append("thead th,tfoot th{background:#5C443A;color:#FFF;text-align:left;text-transform:uppercase;padding:3px 10px}");
    sb.append("tbody td a{color:#blue;}");
    sb.append("tbody td a:visited{color:gray;text-decoration:line-through}");
    sb.append("tbody td a:hover{text-decoration:underline}");
    sb.append("tbody th a{color:#363636;font-weight:400;text-decoration:none}");
    sb.append("tbody th a:hover{color:#363636}");
    sb.append("tbody th,tbody td{text-align:left;vertical-align:top}");
    sb.append("tfoot td{background:#5C443A;color:#FFF;padding-top:3px;font-weight:bold;}");
    sb.append(".odd{background:#fff}");
    sb.append("tbody tr:hover{background:#99BCBF;border:1px solid #03476F;color:#000}");
    sb.append(".num {text-align:center}");
    sb.append(".center{text-align:center;font-weight:bold;}");
    sb.append(".rRate, .st-red{text-align:center;color:red;font-weight:bold;}");
    sb.append(".gRate, .st-green{text-align:center;color:green;font-weight:bold;}");
    sb.append("br{display:block; margin-top:10px; line-height:22px;}");
    sb.append("}</style>");

    sb.append("<table><thead><tr><th>Owner</th><th>Site</th><th>Browser</th><th>Status</th><th>Total Ran</th><th>Passed</th><th>Failed</th><th title=\"For Green pass rate should be >= 70%\">Pass Rate</th><th>Failed Cases</th><th>Passed Cases</th></tr></thead><tbody>");

    Boolean odd = true;
    long gtRan = 0;
    long gtPassed = 0;
    long gtFailed = 0;

    Boolean emailDescriptionEnabled = WTFTestConfig.reportConsolidatedFailureWithDescriptionOnlyEnabled();

    for (String owner : keyList) {
      List <ITestResult> failedList = ownersOfFailedTests.get(owner) != null
          ? ownersOfFailedTests.get(owner)
          : new ArrayList<ITestResult>();

      List <ITestResult> passedList = ownersOfPassedTests.get(owner) != null 
          ? ownersOfPassedTests.get(owner)
          : new ArrayList<ITestResult>();

      int total = failedList.size() + passedList.size();
      gtRan += total;

      int failed = failedList.size();
      gtFailed += failed;

      int passed = passedList.size();
      gtPassed += passed;

      int passRate = (int) Math.floor(((double)passed / (double) total) * 100.00);

      String grRate = passRate >= 70 ? "gRate" : "rRate";

      String failedTests = "";
      String passedTests = "";

      List <String> passedTList =  new ArrayList<String>();
      List <String> failedTList =  new ArrayList<String>();

      if (failed > 0) {
        failedTList = emailDescriptionEnabled 
            ? getTestNames(failedList, ResultProp.TEST_DESCRIPTION)
            : getTestNames(failedList, ResultProp.TEST_CASE_NAME);
        failedTests = StringUtils.join(failedTList, "");
        failedTests = "<ul>" + failedTests + "</ul>";
      }

      if (passed > 0) {
        passedTList = emailDescriptionEnabled
            ? getTestNames(passedList, ResultProp.TEST_DESCRIPTION)
            : getTestNames(passedList, ResultProp.TEST_CASE_NAME);
            passedTests = StringUtils.join(passedTList, "");
        passedTests = "<ul>" + passedTests + "</ul>";
      }

      
      String status = failed > 0 ? "RED" : "GREEN";
      String statusClass = failed > 0 ? "st-red" : "st-green";

      String ownerName = owner.split("#")[0];
      String browserName = WTFTestConfig.isEmailBrowserNameHidden() ? "N/A" : owner.split("#")[1];

      String siteName = owner.split("#")[2];

      if (odd) {
        sb.append(String.format("<tr class=\"odd\"><td><a href=\"http://go/who/%s\">%s</a></td><td class=\"center\">%s</td><td class=\"center\">%s</td><td class=\"%s\">%s</td><td class=\"num\">%s</td><td class=\"gRate num\">%s</td><td class=\"rRate num\">%s</td><td class=\"%s num\">%s%%</td><td>%s</td><td>%s</td></tr>",
            ownerName, ownerName, siteName, browserName, statusClass, status, total, passed, failed, grRate, passRate, failedTests, passedTests));
        odd = false;
      } else {
        sb.append(String.format("<tr><td><a href=\"http://go/who/%s\">%s</a></td><td class=\"center\">%s</td><td class=\"center\">%s</td><td class=\"%s\">%s</td><td class=\"num\">%s</td><td class=\"gRate num\">%s</td><td class=\"rRate num\">%s</td><td class=\"%s num\">%s%%</td><td>%s</td><td>%s</td></tr>",
            ownerName, ownerName, siteName, browserName, statusClass, status, total, passed, failed, grRate, passRate, failedTests, passedTests));
        odd = true;
      }
    }
    
    int gtPassRate = (int) Math.floor(((double)gtPassed / (double) gtRan) * 100.00);
    String grRate = gtPassRate >= 70 ? "gRate" : "rRate";
    
    sb.append(String.format("</tbody><tfoot><tr><td>Total</td><td></td><td></td><td></td><td class=\"num\">%s</td><td class=\"gRate num\">%s</td><td class=\"rRate num\">%s</td><td class=\"%s num\">%s%%</td><td></td><td></td></tr></tfoot></table>", gtRan, gtPassed, gtFailed, grRate, gtPassRate));

    if (System.getProperties().containsKey("wtf.dash.url")) {
      sb.append(String.format("<br /><br /> For more detailed report vist our dashboard: <a href=\"%s\">Detailed Report..</a>",
                System.getProperties().getProperty("wtf.dash.url")));
    }

    String subject = String.format("%s [CONSOLIDATED] :: (%s) Test Failing, Pass Rate %s%%",
                                   WTFTestConfig.getSubject(), gtFailed, gtPassRate);

    Emailer.sendEmails(WTFTestArgs.commandLineArgs.emailHost,
        WTFTestArgs.commandLineArgs.emailFrom,
        WTFTestConfig.getGroupEmailId(),
        WTFTestConfig.getCC(),
        subject, sb.toString());
  }

  private static List <String> getTestNames(List <ITestResult> trList, ResultProp prop) {
    List <String> resultStringList = new ArrayList <String>();
    switch (prop) {
      default:
      case TEST_CASE_NAME:
        for (ITestResult tr: trList) {
          String className = tr.getTestClass().getName().replace("com.ebay.webdriver.", "..");
          resultStringList.add(getAnchoredDashClassLink(String.format("%s.%s", className, tr.getName()), tr));
        }
        return resultStringList;
      case TEST_DESCRIPTION:
        for (ITestResult tr: trList) {
          //resultStringList.add(getAnchoredDashClassLink(tr.getMethod().getDescription(), tr));
          resultStringList.add(getAnchoredDashClassLink(
              (String)tr.getAttribute(WTFAttribute.TEST_DESCRIPTION), tr));
        }
        return resultStringList;
    } 
  }

  private static String getAnchoredDashClassLink(String anchorText, ITestResult tr) {
    Boolean dashEnabled = WTFTestConfig.reportConsolidatedFailureEnabled();
    if (!dashEnabled) {
      return anchorText;
    }

    String runId = System.getProperties().getProperty("wtf.dash.url");
    runId = runId.replace("col?", "method?");
    String colName = tr.getMethod().getTestClass().getXmlTest().getName();

    String className = tr.getTestClass().getName();
    if (tr.getAttribute("DASH_CLASS_NAME") == null) {
      String [] classNames = className.split("\\.");
      className = classNames[classNames.length - 1];
    } else {
      className = tr.getAttribute("DASH_CLASS_NAME").toString();
    }

    String dashUrl = String.format("<li><a href=\"%s&colName=%s&className=%s\">%s</a></li>",
                                   runId, colName, className, anchorText);
    return dashUrl;
  }

  public static enum ResultProp {
    TEST_CASE_NAME, TEST_DESCRIPTION;
  }

  public synchronized static void sendFailureEmails(IResultMap map) {
    if (!WTFTestConfig.reportViaEmailEnabled()) {
      return;
    }

    Map<String, String> ownerMap = new HashMap<String, String>();

    for (ITestResult result : map.getAllResults()) {
      String email = TestOwnerAnnotationReader.getTestOwner(result.getMethod().getMethod());
      if (email == null) { continue; }

      if (!ownerMap.containsKey(email)) {
        String value = "<h4>Failed Tests:</h4>";
        value += getTestReport(result);
        ownerMap.put(email, value);
      } else {
        String value = ownerMap.get(email);
        ownerMap.remove(email);
        value += getTestReport(result);
        ownerMap.put(email, value);
      }
    }

    for (Map.Entry<String, String> entry : ownerMap.entrySet()) {
      int count = entry.getValue().split("</p>").length;

      String subject = String.format("%s :: (%s) Test Failing.",
                                     WTFTestConfig.getSubject(),
                                     count);
      
      StringBuilder sb = new StringBuilder();
      sb.append(entry.getValue());

      if (System.getProperties().containsKey("wtf.dash.url")) {
        sb.append(String.format("<br /><br /> For more detailed report vist our dashboard: <a href=\"%s\">Detailed Report..</a>",
                  System.getProperties().getProperty("wtf.dash.url")));
      }

      Emailer.sendEmails(WTFTestArgs.commandLineArgs.emailHost,
                         WTFTestArgs.commandLineArgs.emailFrom,
                         entry.getKey(),
                         WTFTestConfig.getCC(),
                         subject, sb.toString());
    }
  }
}
