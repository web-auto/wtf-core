/**
 * Copyright (C) 2014 WTF org.
 */

package org.wtf.core;

import static java.util.logging.Level.SEVERE;
import static org.wtf.core.feature.logger.BaseLogger.LOG;

import java.io.File;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openqa.selenium.Capabilities;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.Platform;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.Augmenter;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.IHookCallBack;
import org.testng.IHookable;
import org.testng.IReporter;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;
import org.testng.TestNG;
import org.testng.TestRunner;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.wtf.core.annotation.browser.WTFBrowserAnnotationReader;
import org.wtf.core.annotation.browser.WTFBrowser;
import org.wtf.core.annotation.country.WTFCountry;
import org.wtf.core.feature.args.WTFTestArgs;
import org.wtf.core.feature.args.converter.ServerEnvironment.Environment;
import org.wtf.core.feature.listener.screenshot.ScreenShotListener;
import org.wtf.core.feature.logger.BaseLogger;
import org.wtf.core.feature.logger.LoggingOutputStream;
import org.wtf.core.feature.logger.StdOutErrLevel;
import org.wtf.core.feature.multiplier.TestMultiplier;
import org.wtf.core.feature.retry.BaseTestRetryAnalyzer;
import org.wtf.core.feature.timeout.WebDriverTestTimeout;
import org.wtf.core.listener.BaseListener;
import org.wtf.core.TearDown;
import org.wtf.core.WTFAttribute;
import org.wtf.core.WTFEnv;
import org.wtf.core.WTFTest;
import org.wtf.core.WTFTestConfig;
import org.wtf.core.WTFWait;

import com.gargoylesoftware.htmlunit.WebClient;
import com.opera.core.systems.OperaDriver;

/**
 * An abstract webdriver testcase.
 * 
 * @author venkatesan.sundramurthy@gmail.com (Venkatesan Sundramurthy)
 */
public abstract class WTFTest implements IHookable {

	public static int DRIVER_DEFAULT_WAIT_TIMEOUT = 10;
	public static final String EMPTY_STRING = "";

	public String hostname = null;
	public String ipAddress = "127.0.0.1";

	public static Boolean initOnce = false;

	private static String screenShotDirectory = "./target";

	public WTFTest() {
		super();
		if (!WTFTest.initOnce) {
			Init();
		}
		setHostAndIPAddress();
		screenShotDirectory = getScreenShotDirectory();
	}

	private void setHostAndIPAddress() {
		try {
			InetAddress addr = InetAddress.getLocalHost();
			// Get IP Address
			ipAddress = InetAddress.getLocalHost().getHostAddress();
			// Get hostname
			hostname = addr.getCanonicalHostName();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}

	private String getScreenShotDirectory() {
		File directory = new File(".");
		try {
			String pwd = directory.getCanonicalPath();
			screenShotDirectory = pwd;
		} catch (Exception e) {
		}
		File f = new File(new File(screenShotDirectory), "/target/screenshot");
		f.mkdir();
		screenShotDirectory = f.getPath();
		return screenShotDirectory;
	}

	protected void Init() {
		WTFTest.initOnce = true;
		WTFTestConfig.initFlags();

		if (WTFTestConfig.isSmartLogEnabled()) {
			PrintStream stdout = System.out;
			BaseLogger.stdout = stdout;

			Logger logger;
			LoggingOutputStream los;

			logger = Logger.getLogger("stdout");
			los = new LoggingOutputStream(logger, StdOutErrLevel.STDOUT);
			System.setOut(new PrintStream(los, true));
		}
	}

	/**
	 * Returns the default webdriver friendly data provider objects with 1
	 * driver environment.
	 */
	@DataProvider(name = "WEBDRIVER", parallel = true)
	public Object[][] webdriverProvider(ITestContext context) {
		return getWebdriverFriendlyData(null, context);
	}

	/**
	 * Returns the default webdriver friendly data provider objects with 2
	 * driver environment.
	 */
	@DataProvider(name = "WEBDRIVER2", parallel = true)
	public Object[][] webdriverProvider2(ITestContext context) {
		// TODO (Venkat) Make a factory class to generalize driver count.
		return getWebdriverFriendlyData(null, 2, context);
	}

	/**
	 * Returns the default webdriver friendly data provider objects with 3
	 * driver environment.
	 */
	@DataProvider(name = "WEBDRIVER3", parallel = true)
	public Object[][] webdriverProvider3(ITestContext context) {
		return getWebdriverFriendlyData(null, 3, context);
	}

	private static Object[][] duplicateWebdriverFriendlyDataForTestReRun(
			Object[][] data, int reRunCount) {
		int dataRowlength = data.length * reRunCount;
		int dataCollength = data[0].length;
		Object[][] friendlyData = new Object[dataRowlength][dataCollength];
		for (int i = 0; i < data.length; i++) {
			for (int j = 0; j < reRunCount; j++) {
				System.arraycopy(data[i], 0,
						friendlyData[(i * reRunCount) + j], 0, dataCollength);
			}
		}
		return friendlyData;
	}

	/**
	 * Restructures the given object[][] to accommodate a null webdriver
	 * environment and a test context at the end of each object[] array.
	 */
	public static Object[][] getWebdriverFriendlyData(Object[][] data,
			int envCount, ITestContext context) {
		int dataRowlength = 1;
		int dataCollength = envCount;
		boolean emptyData = true;
		if (data != null && data instanceof Object[][]) {
			dataRowlength = data.length;
			dataCollength = data[0].length + envCount;
			emptyData = false;
		}

		Object[][] friendlyData = new Object[dataRowlength][dataCollength];
		for (int i = 0; i < dataRowlength; i++) {
			if (!emptyData) {
				System.arraycopy(data[i], 0, friendlyData[i], 0, dataCollength
						- envCount);
			}
			for (int envIndex = 1; envIndex <= envCount; envIndex++) {
				try {
					friendlyData[i][dataCollength - envIndex] = new WTFEnv(
							null, null, null, null, null, context, null);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		int testReRun = Integer.parseInt(WTFTestConfig.getTestReRun());
		if (testReRun > 1) {
			return duplicateWebdriverFriendlyDataForTestReRun(friendlyData,
					testReRun);
		}
		return friendlyData;
	}

	/**
	 * Restructures the given object[][] to accommodate a null webdriver
	 * environment and a test context at the end of each object[] array.
	 */
	public static Object[][] getWebdriverFriendlyData(Object[][] data,
			ITestContext context) {
		return getWebdriverFriendlyData(data, 1, context);
	}

	/**
	 * Returns the browser type mentioned on suite XML file, else returns FF as
	 * default browser.
	 */
	@SuppressWarnings("deprecation")
  public static WTFBrowser getCurrentBrowser(ITestContext context,
			ITestResult testResult) {
		WTFBrowser browserToRun = null;

		// From test groups.
		for (String groupName : testResult.getMethod().getGroups()) {
			if (groupName.equals("BROWSER")) {
				browserToRun = WTFBrowser.getBrowserNameFromList(testResult
						.getMethod().getGroups());
				break;
			}
		}

		/*
		 * // From browser annotation. for (Browser groupName :
		 * TestBrowserAnnotationReader
		 * .getIncludedBrowsers(testResult.getMethod().getMethod())) { if
		 * (groupName.equals(Browser.HTML_UNIT)) { browserToRun =
		 * Browser.HTML_UNIT; } }
		 */

		// Load it from the XML suite test parameter.
		if (browserToRun == null) {
			browserToRun = WTFBrowser.parse(getParameter(context, "browser-type"));
		}

		// Load it from test flags / old style VM arg.
		if (browserToRun == null && WTFTestConfig.getBrowser() != null) {
			browserToRun = WTFBrowser.parse(WTFTestConfig.getBrowser());
		}

		// Load it from the test parameter / VM Args.
		if (browserToRun == null && WTFTestConfig.getBrowsers().length == 1
				&& WTFTestConfig.getBrowsers()[0] != WTFBrowser.FIREFOX) {
			browserToRun = WTFTestConfig.getBrowsers()[0];
		}

		// Load it from multiplexer.
		if (browserToRun == null
				&& (BaseTestRetryAnalyzer) testResult.getMethod()
						.getRetryAnalyzer() != null) {
			browserToRun = ((BaseTestRetryAnalyzer) testResult.getMethod()
					.getRetryAnalyzer()).browser;
			List<WTFBrowser> enabledBrowserList = new ArrayList<WTFBrowser>(
					Arrays.asList(WTFBrowserAnnotationReader.getIncludes(testResult.getMethod().getMethod())));
			if (enabledBrowserList.size() > 0
					&& Collections.binarySearch(enabledBrowserList,
							browserToRun) < 0) {
				browserToRun = WTFBrowser.HTML_UNIT;
			}
		}

		// Load FF as default browser.
		if (browserToRun == null) {
			browserToRun = WTFBrowser.FIREFOX;
		}
		return browserToRun;
	}

	/**
	 * Returns the browser type mentioned on suite XML file, else returns FF as
	 * default browser.
	 */
	public WTFCountry getCurrentSite(ITestContext context, ITestResult testResult) {
	  WTFCountry countryToRun = null;

		// Load it from the XML suite test parameter.
		if (countryToRun == null) {
			countryToRun = getParameter(context, "site") != null ? WTFCountry
					.parse(getParameter(context, "site").toLowerCase()) : null;
		}

		// Load it from the test parameter.
		if (countryToRun == null && WTFTestConfig.getSites().length == 1
				&& WTFTestConfig.getSites()[0] != WTFCountry.US) {
			countryToRun = WTFTestConfig.getSites()[0];
		}

		// Load it from multiplexer.
		if (countryToRun == null
				&& (BaseTestRetryAnalyzer) testResult.getMethod()
						.getRetryAnalyzer() != null) {
			countryToRun = ((BaseTestRetryAnalyzer) testResult.getMethod()
					.getRetryAnalyzer()).country;
		}

		// Load FF as default browser.
		if (countryToRun == null) {
			countryToRun = WTFCountry.US;
		}
		return countryToRun;
	}

	/**
	 * Returns the browser type mentioned on suite XML file, else returns FF as
	 * default browser.
	 */
	public Environment getCurrentEnvironment(ITestContext context,
			ITestResult testResult) {
		Environment environmentToRun = null;

		// Load it from the XML suite test parameter.
		/*
		 * if (environmentToRun == null) { environmentToRun =
		 * getParameter(context, "env") != null ?
		 * Environment.parse(getParameter(context, "env").toLowerCase()) : null;
		 * }
		 */

		// Load it from the test parameter.
		if (environmentToRun == null
				&& WTFTestConfig.getEnvironments().length == 1
				&& WTFTestConfig.getEnvironments()[0] != Environment.QA) {
			environmentToRun = WTFTestConfig.getEnvironments()[0];
		}

		// Load it from multiplexer.
		if (environmentToRun == null
				&& (BaseTestRetryAnalyzer) testResult.getMethod()
						.getRetryAnalyzer() != null) {
			environmentToRun = ((BaseTestRetryAnalyzer) testResult.getMethod()
					.getRetryAnalyzer()).environment;
		}

		// Load FF as default browser.
		if (environmentToRun == null) {
			environmentToRun = Environment.QA;
		}
		return environmentToRun;
	}

	private int getWebdriverEnvironmentCountByScanningTestParemeters(
			Object[] testParameters) {
		int envCount = 0;
		for (Object parameter : testParameters) {
			if (parameter instanceof WTFEnv) {
				envCount++;
			}
		}
		return envCount;
	}

	public void captureScreenShoot(WTFEnv env, ITestResult testResult) {
		if (!env.getBrowser().equals(WTFBrowser.NATIVE_HTML_UNIT))
			testResult.setAttribute("TEST_HTML_SOURCE", env.getDriver()
					.getPageSource());

		// if (env.getBrowser().equals(Browser.IE)) {
		// LOG(Level.INFO,
		// String.format("Screenshot feature is disabled for IE. Will enable soon.."));
		// }
		if (!env.getBrowser().equals(WTFBrowser.HTML_UNIT)
				&& !env.getBrowser().equals(WTFBrowser.NATIVE_HTML_UNIT)
				&& WTFTestConfig.screenShotEnabled()
				&& testResult.getThrowable() != null) {
			String path = String.format("%s.%s.%s.%s.png", testResult
					.getMethod().getRealClass().toString(), testResult
					.getMethod().getMethodName(), BaseListener
					.getBrowserName(testResult), BaseListener
					.getSiteName(testResult));
			String screenShotFilename = path.replace(
					"class com.ebay.webdriver.", "");

			File f = new File(new File(getScreenShotDirectory()),
					screenShotFilename);
			String screenShotFilePath = f.getPath();
			LOG(Level.INFO, "Testcase FAILED.. Capturing Screenshot..");

			File screenshotFile = null;
			try {
				screenshotFile = ((TakesScreenshot) env.getDriver())
						.getScreenshotAs(OutputType.FILE);
			} catch (Exception e) {
				LOG(Level.SEVERE,
						"Screenshot FAILED.. Unable to capture Screenshot..");
				return;
			}

/*			WTFDashScreenshotUtil.setTestScreenShotForWTFDash(screenshotFile,
					testResult);

			try {
				FileUtils
						.copyFile(screenshotFile, new File(screenShotFilePath));
			} catch (IOException e) {
				e.printStackTrace();
			}
			testResult.setAttribute("SCREENSHOT_FILE_PATH", screenShotFilePath);*/
		}
	}

	public void failedURL(WTFEnv env, ITestResult testResult) {
		if (testResult.getThrowable() != null && env.getDriver() != null) {
			LOG(Level.INFO, "Failure URL: " + env.getDriver().getCurrentUrl());
		}
	}

	/**
	 * Overrides and controls the individual test case execution. Injects the
	 * appropriate webdriver instance to the test parameter. Handles the test
	 * teardown.
	 */
	public void run(final IHookCallBack icb, ITestResult testResult) {
		WTFEnv envWithContext = ((WTFEnv) icb.getParameters()[icb
				.getParameters().length - 1]);

		int maxEnvCount = getWebdriverEnvironmentCountByScanningTestParemeters(icb
				.getParameters());
		List<WTFEnv> envList = new ArrayList<WTFEnv>();

		WTFBrowser browser = getCurrentBrowser(envWithContext.getTestContext(),
				testResult);

		WTFCountry country = getCurrentSite(envWithContext.getTestContext(),
				testResult);
		Environment environment = getCurrentEnvironment(
				envWithContext.getTestContext(), testResult);

		testResult.setAttribute("TestResultBrowser", browser.getBrowserName());
		testResult.setAttribute("CurrentSite", country);
		testResult.setAttribute("TestResultHost", hostname);
		testResult.setAttribute("TestResultIPAddress", ipAddress);
		testResult.setAttribute(WTFAttribute.TEST_DESCRIPTION, testResult
				.getMethod().getDescription());

    LOG(Level.INFO,
        String.format("Launching Webdriver for Browser %s, Country %s and Test Environment %s",
                      browser.getBrowserName().toUpperCase(), country, environment));

		if (!(browser == WTFBrowser.NATIVE_HTML_UNIT)) {
			for (int driverCount = 1; driverCount <= maxEnvCount; driverCount++) {
				WebDriver driver = getDriverWithRetry(
						browser,
						getCurrentSite(envWithContext.getTestContext(),
								testResult), testResult);

				if (driver != null) {
					WTFEnv env = new WTFEnv(driver, new WTFWait(driver,
							DRIVER_DEFAULT_WAIT_TIMEOUT), country, browser,
							environment, envWithContext.getTestContext(),
							testResult);
					icb.getParameters()[icb.getParameters().length
							- driverCount] = env;
					envList.add(env);
				}
			}

			new WebDriverTestTimeout(testResult, envList).start();
		} else {
			for (int driverCount = 1; driverCount <= maxEnvCount; driverCount++) {
				WebClient webClient = new WebClient();
				;

				if (webClient != null) {
					WTFEnv env = new WTFEnv(webClient, country, browser,
							environment, envWithContext.getTestContext(),
							testResult);
					icb.getParameters()[icb.getParameters().length
							- driverCount] = env;
					envList.add(env);
				}
			}
		}

		// Run Test.
		if (maxEnvCount == envList.size()) {
			setUp(envList);
			setUp(icb.getParameters());
			icb.runTestMethod(testResult);
		} else {
			if ((browser != WTFBrowser.NATIVE_HTML_UNIT)) {
				testResult.setAttribute("DRIVER_FAILED", true);
			} else
				testResult.setAttribute("DIRECT HTML UNIT CREATION FAILED",
						true);
		}

		if ((BaseTestRetryAnalyzer) testResult.getMethod().getRetryAnalyzer() != null) {
			BaseTestRetryAnalyzer btr = (BaseTestRetryAnalyzer) testResult
					.getMethod().getRetryAnalyzer();
			btr.setRetryEnabledDisabledStatus(testResult);
		}

		// Post Test
		for (WTFEnv env : envList) {
			// Check for soft asserts.
			if (env.softAssert.count() > 0) {
				setTestFailed(testResult,
						"Soft Assert: Check the soft asserts for more details..");
				env.softAssert.flushSoftAsserts();
			}

			// Logging URL where test failed
			failedURL(env, testResult);

			// capture failed test's screen shots;
			captureScreenShoot(env, testResult);

			// run tear downs.
			tearDown(env.getTearDown());

			// close driver;
			if ((browser != WTFBrowser.NATIVE_HTML_UNIT)) {
				env.getDriver().quit();
			} else {
				env.getWebClient().closeAllWindows();
			}
		}
	}

	/**
	 * Test case setup.
	 */
	protected void setUp(List<WTFEnv> envList) {
	}

	/**
	 * Test case setup.
	 */
	protected void setUp(Object[] testParameters) {
	}

	/**
	 * Returns the basic firefox profile. Override this method to add custom
	 * profile.
	 */
	protected FirefoxProfile loadFirefoxProfile() {
		return loadFirefoxProfile(null);
	}

	/**
	 * Returns the basic firefox profile. Override this method to add custom
	 * profile.
	 */
	protected FirefoxProfile loadFirefoxProfile(WTFCountry country) {
		FirefoxProfile profile = new FirefoxProfile();
		profile.setEnableNativeEvents(true);
		profile.setAssumeUntrustedCertificateIssuer(true);
		if (country != null) {
			System.out
					.println("Setting firefox profile with intl.accept_langauges as '"
							+ country.toString() + "'");
			profile.setPreference("intl.accept_languages", country.toString()
					.toLowerCase());
		}
		return profile;
	}

	/**
	 * Sets the chrome driver proxy server.
	 */
	protected void setChromeDriverService() {
		String chromeDriver = WTFTestConfig.getChromeDriver();
		String OS = System.getProperty("os.name").toLowerCase();
		String ResPath = "src/main/resources/webdriver/bin/";

		if (chromeDriver.isEmpty()) {
			if (OS.contains("mac")) { /* If Mac */
				chromeDriver = ResPath + "chromedriver.bin";
			} else if ((OS.contains("nix") || OS.contains("nux") || OS
					.contains("aix")) && !is64()) {
				/* Unix 32 bit */
				chromeDriver = ResPath + "chromedriver32.bin";
			} else if ((OS.contains("nix") || OS.contains("nux") || OS
					.contains("aix")) && is64()) {
				/* Unix 64 bit */
				chromeDriver = ResPath + "chromedriver64.bin";
			} else if (OS.contains("win")) {
				/* Windows */
				chromeDriver = ResPath + "chromedriver.exe";
			} else { /* default to windows */
				chromeDriver = ResPath + "chromedriver.exe";
			}
		}

		System.setProperty("webdriver.chrome.driver", chromeDriver);
	}

	private boolean is64() {
		boolean is64bit = false;
		if (System.getProperty("os.name").contains("Windows")) {
			is64bit = (System.getenv("ProgramFiles(x86)") != null);
		} else {
			is64bit = (System.getProperty("os.arch").indexOf("64") != -1);
		}
		return is64bit;
	}

	/**
	 * Sets the IE driver proxy server.
	 */
	protected void setIEDriverService() {
		String ResPath = "src/main/resources/webdriver/bin/";

		String IeDriverPathFormat = ResPath + "IEDriverServer%s.exe";
		String systemArchBitCode = "64";
		if (!is64()) {
			systemArchBitCode = "32";
		}
		/*
		 * Always use 32 bit driver. This is a hack to fix the slow sendkey bug
		 * on 64 bit IE drivers
		 */
		systemArchBitCode = "32";
		System.setProperty("webdriver.ie.driver",
				String.format(IeDriverPathFormat, systemArchBitCode));
	}

	private DesiredCapabilities getBasicBrowserCapabilities(WTFBrowser browser) {
		switch (browser) {
		case FIREFOX:
			return DesiredCapabilities.firefox();
		case IE:
			setIEDriverService();
			return DesiredCapabilities.internetExplorer();
		case CHROME:
			setChromeDriverService();
			return DesiredCapabilities.chrome();
		case OPERA:
			return DesiredCapabilities.opera();
		case HTML_UNIT:
			return DesiredCapabilities.htmlUnit();
		default:
			return null;
		}
	}

	private DesiredCapabilities getAdvancedBrowserCapabilities(WTFBrowser browser) {
		return new DesiredCapabilities(browser.browserNameOnGrid,
				browser.version, browser.os != null ? browser.getPlatform()
						: Platform.WINDOWS);
	}

	protected DesiredCapabilities getDesiredCapabilities(WTFBrowser browser) {
		return browser.version != null ? getAdvancedBrowserCapabilities(browser)
				: getBasicBrowserCapabilities(browser);
	}

	public synchronized WebDriver getDriver(WTFBrowser browser,
			ITestResult testResult) {
		return getDriver(browser, null, testResult);
	}

	public synchronized WebDriver getDriver(WTFBrowser browser, WTFCountry country,
			ITestResult testResult) {
		switch (browser) {
		case FIREFOX:
			return new FirefoxDriver(loadFirefoxProfile(country));
		case IE:
			setIEDriverService();
			return new InternetExplorerDriver();
		case CHROME:
			setChromeDriverService();
			return new ChromeDriver();
		case OPERA:
			return new OperaDriver();
		default:
			// TOSO (vsundramurthy) Throw invalid browser exception..
			return null;
		}
	}

	/**
	 * Returns a new webdriver instance for the given browser type.
	 */
	public WebDriver getDriverWithRetry(WTFBrowser browser, WTFCountry country,
			ITestResult testResult) {
		String testName = testResult.getName();
		WebDriver driver = null;
		int reTryMax = 5;
		while (reTryMax > 0) {
			driver = null;
			reTryMax--;
			try {
				if (WTFTestConfig.getRemoteWebDriverHubURL() != null) {
					Capabilities capabilities = getDesiredCapabilities(browser);
					driver = new RemoteWebDriver(new URL(
							WTFTestConfig.getRemoteWebDriverHubURL()),
							capabilities);
					//BeDashListener.setCapabilityMetrics(capabilities);
					if (WTFTestConfig.screenShotEnabled()) {
						driver = new Augmenter().augment(driver);
					}
				} else {
					if (browser.equals(WTFBrowser.HTML_UNIT)) {
						driver = new HtmlUnitDriver(true);
					} else {
						driver = getDriver(browser, testResult);
					}
				}
				break;
			} catch (Exception e) {
				LOG(SEVERE,
						String.format(
								"Driver creation has failed.\nTestcase: %s\nBrowser: %s\nError message: %s\nRetrying one more time..\n",
								testName, browser.getBrowserName(),
								e.getMessage()));
				try {
					// waits for a 10 seconds before retrying.
					Thread.sleep(10000);
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
			if (reTryMax <= 0) {
				LOG(SEVERE,
						"Driver creation has permanently failed on primary Grid after multiple retries..");
				if (WTFTestConfig.getBackupRemoteWebDriverHubURL() != null) {
					LOG(SEVERE, "Trying on the backup grid..");
					return getDriverWithRetryFromBackupGrid(browser, testResult);
				} else {
					LOG(SEVERE,
							"No backup grid provided, driver creating has failed on the primary grid..");
					setTestFailed(testResult,
							"Unable to connect with webdriver Grid..");
				}
			}
		}
		return driver;
	}

	/**
	 * Returns a new webdriver instance for the given browser type.
	 */
	public WebDriver getDriverWithRetryFromBackupGrid(WTFBrowser browser,
			ITestResult testResult) {
		String testName = testResult.getName();
		WebDriver driver = null;
		int reTryMax = 3;
		while (reTryMax > 0) {
			driver = null;
			reTryMax--;
			try {
				if (WTFTestConfig.getBackupRemoteWebDriverHubURL() != null) {
					Capabilities capabilities = getDesiredCapabilities(browser);
					driver = new RemoteWebDriver(new URL(
							WTFTestConfig.getBackupRemoteWebDriverHubURL()),
							capabilities);
					//BeDashListener.setCapabilityMetrics(capabilities);
					if (WTFTestConfig.screenShotEnabled()) {
						driver = new Augmenter().augment(driver);
					}
					break;
				} else {
					return null;
				}
			} catch (Exception e) {
				LOG(SEVERE,
						String.format(
								"%s:: %s driver creation has failed on backup grid, reason: %s, retrying again..",
								testName, browser.getBrowserName(),
								e.getMessage()));
				try {
					// waits for a 10 seconds before retrying.
					Thread.sleep(10000);
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
			if (reTryMax <= 0) {
				LOG(SEVERE,
						"Driver creation has permanently failed on backup grid after multiple "
								+ "retries..");
				setTestFailed(testResult,
						"Unable to connect with webdriver Grid..");
			}
		}
		return driver;
	}

	/**
	 * Returns the parameter value for the given test context. Test level
	 * parameters will get higher priority over suite level parameters.
	 */
	public static String getParameter(ITestContext context, String parameterName) {
		String parameterValue = context.getCurrentXmlTest().getParameter(
				parameterName);
		if (parameterValue == null) {
			parameterValue = context.getSuite().getParameter(parameterName);
		}
		return parameterValue;

	}

	/**
	 * Triggers the teardown methods for each item's of teardown list.
	 */
	private void tearDown(LinkedList<TearDown> stack) {
		for (TearDown tearDown : stack) {
			try {
				tearDown.tearDown();
			} catch (Throwable t) {
			}
		}
		stack.clear();
	}

	@AfterSuite(alwaysRun = true)
	public void setupAfterSuite() {
		LOG(Level.INFO, "Test Run Completed.");
	}

	@BeforeTest(alwaysRun = true)
	public void beforeTest(ITestContext testContext) {
		TestRunner tr = (TestRunner) testContext;
		tr.setMethodInterceptor(new TestMultiplier());
	}

	@BeforeSuite(alwaysRun = true)
	public void beforeSuite(ITestContext testContext)
			throws IllegalAccessException, InstantiationException {

		TestRunner tr = (TestRunner) testContext;

		if (WTFTestArgs.commandLineArgs.threadCount > 1) {
			tr.getSuite().getXmlSuite().setParallel("methods");
			tr.getTest().setParallel("methods");

			tr.getSuite().getXmlSuite()
					.setThreadCount(WTFTestArgs.commandLineArgs.threadCount);
			tr.getTest()
					.setThreadCount(WTFTestArgs.commandLineArgs.threadCount);

			tr.getSuite()
					.getXmlSuite()
					.setDataProviderThreadCount(
							WTFTestArgs.commandLineArgs.threadCount);

			TestNG tng = com.beust.testng.TestNG.getDefault();
			tng.setThreadCount(WTFTestArgs.commandLineArgs.threadCount);
		}

		addTestListenerIfNotAddedBefore(tr, BaseListener.class);
		//addTestListenerIfNotAddedBefore(tr, WTFDashNodeJsonDataPushListener.class);
		addTestListenerIfNotAddedBefore(tr, ScreenShotListener.class);
		//addTestListenerIfNotAddedBefore(tr, BeDashListener.class);
		addReporterIfNotAddedBefore(BaseListener.class);

		// tr.setMethodInterceptor(new TestMultiplier());
		if (WTFTestConfig.getSuiteName() != null) {
			System.getProperties().setProperty("wtf.dash.suitename",
					WTFTestConfig.getSuiteName());
		}
	}

	public static void addReporterIfNotAddedBefore(Class<?> reporterClass)
			throws IllegalAccessException, InstantiationException {
		TestNG tng = com.beust.testng.TestNG.getDefault();
		for (IReporter reptr : tng.getReporters()) {
			if (reporterClass.isInstance(reptr)) {
				return;
			}
		}
		tng.getReporters().add((IReporter) reporterClass.newInstance());
	}

	public static void addTestListenerIfNotAddedBefore(TestRunner tr,
			Class<?> listenerClass) throws IllegalAccessException,
			InstantiationException {
		for (ITestListener listnr : tr.getTestListeners()) {
			if (listenerClass.isInstance(listnr)) {
				return;
			}
		}
		tr.addTestListener((ITestListener) listenerClass.newInstance());
	}

	private void setTestFailed(ITestResult res, String msg) {
		res.setStatus(ITestResult.FAILURE);
		res.setThrowable(new Throwable(msg));
	}

}
