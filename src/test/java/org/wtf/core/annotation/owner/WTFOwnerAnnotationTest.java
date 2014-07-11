package org.wtf.core.annotation.owner;

import static org.wtf.core.annotation.browser.WTFBrowser.HTML_UNIT;

import org.testng.Assert;
import org.testng.annotations.Test;
import org.wtf.core.WTFEnv;
import org.wtf.core.WTFTest;
import org.wtf.core.annotation.browser.IncludeBrowser;


/**
 * Test @TestOwner annonotation.
 * @author vsundramurthy@ebay.com (Venkat Sundramurthy)
 */
@IncludeBrowser(browsers = { HTML_UNIT })
@TestOwner(email = "vsundramurthy")
public class WTFOwnerAnnotationTest  extends WTFTest {

  @Test(dataProvider = "WEBDRIVER", description = "Test the test owner annotation.")
  public void testIncludes(WTFEnv env) {
    Assert.assertEquals(TestOwnerAnnotationReader.getTestOwner(env.getMethod()), "vsundramurthy",
        "Owner is not as annotated.");
  }
}
