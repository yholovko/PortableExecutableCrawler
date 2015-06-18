package googleplay.com;

import crawler.ApkFile;
import crawler.Constants;
import crawler.MyLinkedBlockingQueue;
import org.apache.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

public class GooglePlay implements Runnable {
    static final Logger APK_LOG = Logger.getLogger("googlePlayLogger");
    private MyLinkedBlockingQueue<ApkFile> goldenLinks = new MyLinkedBlockingQueue<>();
    public static WebDriver driver;

    public static WebDriver connectTo(String url, boolean javaScript) {
        while (true) {
            try {
                ((HtmlUnitDriver) driver).setJavascriptEnabled(javaScript);
                driver.get(url);
                break;
            } catch (Exception e) {
                APK_LOG.warn(String.format("Reconnection to %s", url));
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e1) {
                    APK_LOG.error(e1);
                }
            }
        }
        return driver;
    }

    @Override
    public void run() {
        APK_LOG.info("Start...");

        driver = new HtmlUnitDriver();
        ((HtmlUnitDriver) driver).setProxy(Constants.PROXY_HOST, Integer.valueOf(Constants.PROXY_PORT));

        Thread getLinksThread = new Thread(new ProducerLinks(goldenLinks));
        getLinksThread.start();

        Thread downloadApkThread = new Thread(new Consumer(goldenLinks));
        downloadApkThread.start();
    }
}
