package org.wtf.core.annotation.country;

import static org.wtf.core.annotation.browser.WTFBrowser.HTML_UNIT;
import static org.wtf.core.annotation.country.WTFCountry.US;
import static org.wtf.core.annotation.country.WTFCountry.UK;

import org.testng.Assert;
import org.testng.annotations.Test;
import org.wtf.core.WTFEnv;
import org.wtf.core.WTFTest;
import org.wtf.core.annotation.browser.IncludeBrowser;
import org.wtf.core.annotation.country.IncludeCountry;
import org.wtf.core.annotation.country.WTFCountryAnnotationReader;
import org.wtf.core.annotation.country.WTFCountry;


/**
 * Test @TestSite annonotation.
 * @author vsundramurthy@ebay.com (Venkat Sundramurthy)
 */
@IncludeBrowser(browsers = { HTML_UNIT })
public class WTFCountryAnnotationTest  extends WTFTest {

  @Test(dataProvider = "WEBDRIVER", description = "Default country is US.")
  public void testDefaults(WTFEnv env) {
    WTFCountry[] includes = WTFCountryAnnotationReader.getIncludes(env.getMethod());
    Assert.assertTrue(WTFCountryAnnotationReader.has(includes, WTFCountry.US),
        "Default country is not US.");
  }

  @Test(dataProvider = "WEBDRIVER", description = "Test country include annotation.")
  @IncludeCountry(countries = { US })
  public void testIncludes(WTFEnv env) {
    WTFCountry[] includes = WTFCountryAnnotationReader.getIncludes(env.getMethod());

    Assert.assertTrue(WTFCountryAnnotationReader.has(includes, US), "not found in includes.");
  }

  @Test(dataProvider = "WEBDRIVER", description = "Test country exclude annotation.")
  @IncludeCountry(countries = { US })
  @ExcludeCountry(countries = { UK })
  public void testExcludes(WTFEnv env) {
    WTFCountry[] includes = WTFCountryAnnotationReader.getIncludes(env.getMethod());
    WTFCountry[] excludes = WTFCountryAnnotationReader.getExcludes(env.getMethod());

    Assert.assertFalse(WTFCountryAnnotationReader.has(includes, UK), "found in includes.");
    Assert.assertTrue(WTFCountryAnnotationReader.has(excludes, UK), "not found in excludes.");
  }
}
