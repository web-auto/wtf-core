/**
 * Copyright (C) 2014 WTF org.
 */

package org.wtf.core.feature.timeout;

import static org.wtf.core.feature.logger.BaseLogger.LOG;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;

import org.testng.ITestResult;
import org.wtf.core.WTFEnv;
import org.wtf.core.annotation.timeout.TestTimeoutAnnotationReader;



/**
 * WebDriver Test Timeout module.
 *
 * @author venkatesan.sundramurthy@gmail.com (Venkatesan Sundramurthy)
 */
public class WebDriverTestTimeout {
  private Timer timer;
  ITestResult testResult;
  List<WTFEnv> envList;
  Thread currentThread;
  int seconds = 300;

  public WebDriverTestTimeout(ITestResult testResult, List<WTFEnv> envList) {
    timer = new Timer();
    this.testResult = testResult;
    this.envList = envList;
    currentThread = Thread.currentThread();
  }

  public void start() {
    Integer secs = TestTimeoutAnnotationReader.getTestTimeout(testResult.getMethod().getMethod());
    if (secs != null && secs < 0) {
      return;
    }

    seconds = secs == null ? this.seconds : secs;

    timer.schedule(new TimerTask() {
        public void run() {
            testTimeOut();
            timer.cancel();
        }

        @SuppressWarnings("deprecation")
        private void testTimeOut() {
          for (WTFEnv env : envList) {
            env.getDriver().close();
          }
          String msg = String.format("Test timed out after %s seconds.", seconds);
          LOG(Level.SEVERE, msg);

          short retry = 10;
          while (currentThread.isAlive() && --retry > 0) {
            try {
              currentThread.interrupt();
              //Thread.sleep(500);
              //currentThread.stop(new InterruptedException(msg));
            } catch (Exception e) {
              e.printStackTrace();
            }
          }
        }
    }, seconds * 1000);
  }
}
