package org.wtf.core.annotation.browser;

import static org.wtf.core.annotation.browser.WTFBrowser.HTML_UNIT;
import static org.wtf.core.annotation.browser.WTFBrowser.FIREFOX;

import org.wtf.core.WTFEnv;
import org.wtf.core.WTFTest;
import org.wtf.core.annotation.browser.WTFBrowser;
import org.wtf.core.annotation.browser.IncludeBrowser;
import org.wtf.core.annotation.browser.ExcludeBrowser;
import org.testng.Assert;
import org.testng.annotations.Test;


/**
 * Test WTF Browser annotations.
 * @author vsundramurthy@ebay.com (Venkat Sundramurthy)
 */
public class WTFBrowserAnnotationTest extends WTFTest {

  @Test(dataProvider = "WEBDRIVER", description = "Default browser HTML_UNIT.")
  public void testDefaults(WTFEnv env) {
    Assert.assertEquals(env.getBrowser(), WTFBrowser.HTML_UNIT, "Default browser is not HTML_UNIT.");
  }

  @Test(dataProvider = "WEBDRIVER", description = "Test browser include annotation.")
  @IncludeBrowser(browsers = { HTML_UNIT })
  public void testIncludes(WTFEnv env) {
    WTFBrowser[] includes = WTFBrowserAnnotationReader.getIncludes(env.getMethod());
    Assert.assertTrue(WTFBrowserAnnotationReader.has(includes, HTML_UNIT), "not found in includes.");
  }

  @Test(dataProvider = "WEBDRIVER", description = "Test browser exclude annotation.")
  @IncludeBrowser(browsers = { HTML_UNIT })
  @ExcludeBrowser(browsers = { FIREFOX })
  public void testExcludes(WTFEnv env) {
    WTFBrowser[] includes = WTFBrowserAnnotationReader.getIncludes(env.getMethod());
    WTFBrowser[] excludes = WTFBrowserAnnotationReader.getExcludes(env.getMethod());

    Assert.assertFalse(WTFBrowserAnnotationReader.has(includes, FIREFOX), "found in includes.");
    Assert.assertTrue(WTFBrowserAnnotationReader.has(excludes, FIREFOX), "not found in excludes.");
  }
}
