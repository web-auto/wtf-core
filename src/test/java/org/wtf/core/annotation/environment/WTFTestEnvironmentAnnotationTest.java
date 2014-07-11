package org.wtf.core.annotation.environment;

import static org.wtf.core.annotation.browser.WTFBrowser.HTML_UNIT;
import static org.wtf.core.annotation.environment.WTFTestEnvironment.QA;
import static org.wtf.core.annotation.environment.WTFTestEnvironment.PROD;

import org.testng.Assert;
import org.testng.annotations.Test;
import org.wtf.core.WTFEnv;
import org.wtf.core.WTFTest;
import org.wtf.core.annotation.browser.IncludeBrowser;
import org.wtf.core.annotation.environment.IncludeTestEnvironment;
import org.wtf.core.annotation.environment.ExcludeTestEnvironment;


/**
 * Test WTF Test Environment annotation.
 * @author vsundramurthy@ebay.com (Venkat Sundramurthy)
 */
@IncludeBrowser(browsers = { HTML_UNIT })
public class WTFTestEnvironmentAnnotationTest  extends WTFTest {

  @Test(dataProvider = "WEBDRIVER", description = "Default test environment is QA.")
  public void testDefaults(WTFEnv env) {
    WTFTestEnvironment [] includes = WTFTestEnvironmentAnnotationReader.getIncludes(env.getMethod());
    Assert.assertTrue(WTFTestEnvironmentAnnotationReader.has(includes, QA), "Default Test environment is not QA.");
  }

  @Test(dataProvider = "WEBDRIVER", description = "Test the test environment include annotation.")
  @IncludeTestEnvironment(environments = { QA })
  public void testIncludes(WTFEnv env) {
    WTFTestEnvironment [] includes = WTFTestEnvironmentAnnotationReader.getIncludes(env.getMethod());
    Assert.assertTrue(WTFTestEnvironmentAnnotationReader.has(includes, QA), "not found in includes.");
  }

  @Test(dataProvider = "WEBDRIVER", description = "Test the test environment exclude annotation.")
  @IncludeTestEnvironment(environments = { QA })
  @ExcludeTestEnvironment(environments = { PROD })
  public void testExcludes(WTFEnv env) {
    WTFTestEnvironment [] includes = WTFTestEnvironmentAnnotationReader.getIncludes(env.getMethod());
    WTFTestEnvironment [] excludes = WTFTestEnvironmentAnnotationReader.getExcludes(env.getMethod());
    Assert.assertFalse(WTFTestEnvironmentAnnotationReader.has(includes, PROD), "found in includes.");
    Assert.assertTrue(WTFTestEnvironmentAnnotationReader.has(excludes, PROD), "not found in excludes.");
  }
}
