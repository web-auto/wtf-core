/**
 * Copyright (C) 2014 WTF org.
 */

package org.wtf.core.feature.args;

import static org.wtf.core.feature.logger.BaseLogger.LOG;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Level;

import org.wtf.core.annotation.browser.WTFBrowser;
import org.wtf.core.annotation.country.WTFCountry;
import org.wtf.core.feature.args.converter.WTFConverter;
import org.wtf.core.feature.args.converter.ServerEnvironment.Environment;

import com.beust.jcommander.JCommander;


/**
 * WTF command line arg processor.
 * 
 * @author venkatesan.sundramurthy@gmail.com (Venkatesan Sundramurthy)
 */
public class WTFTestArgs {

  public static WTFCommandLineArgs commandLineArgs = new WTFCommandLineArgs();

  public static void init() {
    JCommander jc = new JCommander(commandLineArgs);
    String [] testArgs = readTestArgsFromSystemProperties();
    if (testArgs.length > 0) {
      try {
        jc.parse(testArgs);
      } catch (Exception e) {
        LOG(Level.SEVERE, "Invalid test aragumets please refer the below arg usage.");
        LOG(Level.SEVERE, e.getMessage());
        jc.usage();
        LOG(Level.INFO, "Going down..");
        System.exit(0);
      }
    }

    commandLineArgs.wtfBrowsers = WTFConverter.browserConveter(commandLineArgs.browsers);
    commandLineArgs.wtfSites = WTFConverter.siteConverter(commandLineArgs.sites);
    commandLineArgs.wtfEnvironments = WTFConverter.environmentConverter(commandLineArgs.environment);
    commandLineArgs.wtfServers = WTFConverter.serverConverter(commandLineArgs.server);


    // Remove Duplicates.
    commandLineArgs.wtfBrowsers =
        new ArrayList <WTFBrowser>(new HashSet <WTFBrowser>(commandLineArgs.wtfBrowsers));
    commandLineArgs.wtfSites = new ArrayList<WTFCountry>(new HashSet<WTFCountry>(commandLineArgs.wtfSites));
    commandLineArgs.wtfEnvironments = new ArrayList<Environment>(new HashSet<Environment>(commandLineArgs.wtfEnvironments));
    System.setProperty("HIGHLIGHT_ELEMENT", commandLineArgs.highLightElement.toString());
  }

  public static String removeDoubleSpaces(String text) {
    return text.replaceAll("( )+", " ");
  }

  public static String [] readTestArgsFromSystemProperties() {
    String wtfTestArgFromSysProperty = System.getProperties().getProperty("wtf.args");

    // Debug
    //wtfTestArgFromSysProperty = "-site us,uk,de -browser ff -retry 1 -server us=>http://stage1.qa.ebay.com:8080,uk=>http://stage2.qa.ebay.com:8080";
    //wtfTestArgFromSysProperty = "-browser ff -site uk -server uk=>http://www.uk.stage4.stratus.qa.ebay.com -grid -retry 1";
    //wtfTestArgFromSysProperty = "-browserff-8-windows-serverus=>http://qa-wci106.qa.ebay.com:8080-grid-retry1-emailfailure-emailsubjectSRPCodeCoverageThruWDPreCheckInTests-emailccvsundramurthy,imrankhan,pjayarao,vinaykumar";

    // remove double spaces.
    wtfTestArgFromSysProperty = wtfTestArgFromSysProperty != null
        ? removeDoubleSpaces(wtfTestArgFromSysProperty)
        : wtfTestArgFromSysProperty;

    wtfTestArgFromSysProperty = 
        // if not null
        wtfTestArgFromSysProperty != null &&
        // and length > 1, to eliminate just a space or 1 char args.
        wtfTestArgFromSysProperty.length() > 1
            ? wtfTestArgFromSysProperty
            : null;

    LOG(Level.INFO, String.format("Detected input test arguments: %s", wtfTestArgFromSysProperty));
    if (wtfTestArgFromSysProperty == null) {
      LOG(Level.INFO, String.format("Using default test arguments: -browser htmlunit -country us -testenvironment qa"));
    }

    List<String> argList = new ArrayList <String>();
    argList = wtfTestArgFromSysProperty != null
    ? Arrays.asList(wtfTestArgFromSysProperty.split(" "))
    : argList;
        
    return argList.toArray(new String[argList.size()]);
  }
}
