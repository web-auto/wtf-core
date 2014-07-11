/**
 * Copyright (C) 2014 WTF org.
 */

package org.wtf.core.feature.xmljunit;

import java.util.Date;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Pattern;

import org.apache.commons.lang.time.FastDateFormat;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.collections.Maps;
import org.testng.internal.Utils;
import org.testng.reporters.XMLConstants;
import org.testng.reporters.XMLStringBuffer;


public class XmlJuintReport {
  private static final Pattern ENTITY= Pattern.compile("&[a-zA-Z]+;.*");
  private static final Pattern LESS= Pattern.compile("<");
  private static final Pattern GREATER= Pattern.compile(">");
  private static final Pattern SINGLE_QUOTE = Pattern.compile("'");
  private static final Pattern QUOTE = Pattern.compile("\"");
  private static final Map<String, Pattern> ATTR_ESCAPES= Maps.newHashMap();

  static {
    ATTR_ESCAPES.put("&lt;", LESS);
    ATTR_ESCAPES.put("&gt;", GREATER);
    ATTR_ESCAPES.put("&apos;", SINGLE_QUOTE);
    ATTR_ESCAPES.put("&quot;", QUOTE);
  }

  public static String encodeAttr(String attr) {
    String result = replaceAmpersand(attr, ENTITY);
    for (Map.Entry<String, Pattern> e: ATTR_ESCAPES.entrySet()) {
      result = e.getValue().matcher(result).replaceAll(e.getKey());
    }
    return result;
  }

  public static String replaceAmpersand(String str, Pattern pattern) {
    int start = 0;
    int idx = str.indexOf('&', start);
    if (idx == -1) {
      return str;
    }
    StringBuffer result = new StringBuffer();
    while (idx != -1) {
      result.append(str.substring(start, idx));
      if (pattern.matcher(str.substring(idx)).matches()) {
        // do nothing it is an entity;
        result.append("&");
      } else {
        result.append("&amp;");
      }
      start = idx + 1;
      idx = str.indexOf('&', start);
    }
    result.append(str.substring(start));
    return result.toString();
  }

  public static synchronized void generateXmlReport(ITestContext context, String outputDirectory) {
    XMLStringBuffer document= new XMLStringBuffer("");
    document.setXmlDetails("1.0", "UTF-8");
    Properties attrs = new Properties();
    attrs.setProperty(XMLConstants.ATTR_NAME, encodeAttr(context.getName())); // ENCODE

    long totalTests = context.getPassedTests().getAllResults().size() +  context.getFailedTests().getAllResults().size();

    attrs.setProperty(XMLConstants.ATTR_TESTS, String.format("%s", totalTests));
    
    attrs.setProperty(XMLConstants.ATTR_FAILURES,
        String.format("%s", context.getFailedTests().size()));
    attrs.setProperty(XMLConstants.ATTR_ERRORS, "0");
    attrs.setProperty(XMLConstants.ATTR_TIME, String.format("%s",
        ((context.getEndDate().getTime() - context.getStartDate().getTime()) / 1000.0)));

    document.push(XMLConstants.TESTSUITE, attrs);
    document.addEmptyElement(XMLConstants.PROPERTIES);

    for (ITestResult tr : context.getPassedTests().getAllResults()) {
      createElement(document, tr);
    }

    for (ITestResult tr : context.getFailedTests().getAllResults()) {
      createElement(document, tr);
    }

    document.pop();
    //Utils.writeUtf8File(outputDirectory, context.getName() + ".xml", document.toXML());
    /*
     * This change was made to support sridevi's dashboard, it requires the junit test result xml
     * file name in a specific name format
     */
    Utils.writeUtf8File(outputDirectory, "testng-results.xml", document.toXML());
  }

  public static void createElement(XMLStringBuffer doc, ITestResult tr) {
    Properties attrs = new Properties();
    long elapsedTimeMillis = tr.getEndMillis() - tr.getStartMillis();
    String name =
        tr.getMethod().isTest() ? tr.getName() : Utils.detailedMethodName(tr.getMethod(), false);
        
    //SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MMMMM hh:mm aaa");
    //String testRunTest = String.format("%s", simpleDateFormat.format(new Date()));

    String testRunTest = FastDateFormat.getInstance("dd-MMMMM hh:mm aaa").format(new Date());

    attrs.setProperty(XMLConstants.ATTR_NAME, String.format("%s  [%s]", name, testRunTest));
    attrs.setProperty(XMLConstants.ATTR_CLASSNAME, tr.getTestClass().getRealClass().getName());
    attrs.setProperty(XMLConstants.ATTR_TIME, "" + (((double) elapsedTimeMillis) / 1000));

    if((ITestResult.FAILURE == tr.getStatus()) || (ITestResult.SKIP == tr.getStatus())) {
      doc.push(XMLConstants.TESTCASE, attrs);

      if(ITestResult.FAILURE == tr.getStatus()) {
        createFailureElement(doc, tr);
      }
      else if(ITestResult.SKIP == tr.getStatus()) {
        createSkipElement(doc, tr);
      }
      doc.pop();
    }else {
      doc.addEmptyElement(XMLConstants.TESTCASE, attrs);
    }
  }

  public static void createFailureElement(XMLStringBuffer doc, ITestResult tr) {
    Properties attrs = new Properties();
    Throwable t = tr.getThrowable();
    if (t != null) {
      attrs.setProperty(XMLConstants.ATTR_TYPE, t.getClass().getName());
      String message= t.getMessage();
      if ((message != null) && (message.length() > 0)) {
        attrs.setProperty(XMLConstants.ATTR_MESSAGE, encodeAttr(message)); // ENCODE
      }
      doc.push(XMLConstants.FAILURE, attrs);
      doc.addCDATA(Utils.stackTrace(t, false)[0]);
      doc.pop();
    } else {
      doc.addEmptyElement(XMLConstants.FAILURE); // THIS IS AN ERROR
    }
  }

  public static void createSkipElement(XMLStringBuffer doc, ITestResult tr) {
    doc.addEmptyElement("skipped");
  }
}
