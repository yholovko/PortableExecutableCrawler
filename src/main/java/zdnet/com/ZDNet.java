package zdnet.com;

import crawler.Constants;
import crawler.MyLinkedBlockingQueue;
import crawler.PortableExecutableFile;
import org.apache.log4j.Logger;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.IOException;
import java.util.Map;

import org.apache.log4j.PropertyConfigurator;
import org.jsoup.nodes.Document;

public class ZDNet implements Runnable {
    static final Logger ZD_NET_LOG = Logger.getLogger("zdNetLogger");

    private static Map<String, String> loginCookies;

    private MyLinkedBlockingQueue<PortableExecutableFile> goldenLinks = new MyLinkedBlockingQueue<PortableExecutableFile>();

    private void login() {
        Connection.Response res = null;
        while (res == null) {
            try {
                res = Jsoup.connect(Constants.ZD_NET_COM_LOGIN)
                        .data("login[email]", Constants.email, "login[password]", Constants.password)
                        .method(Connection.Method.POST)
                        .execute();
                loginCookies = res.cookies();
            } catch (IOException e) {
                ZD_NET_LOG.warn("java.net.SocketTimeoutException: Read timed out. Reconnection.....");
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                    ZD_NET_LOG.error(e1);
                }
            }
        }
    }

    public static Document connectTo(String url) {
        Document doc = null;
        while (doc == null) {
            try {
                doc = Jsoup.connect(url).cookies(loginCookies).get();
            } catch (IOException e) {
                ZD_NET_LOG.warn(String.format("Reconnection to %s", url));
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                    ZD_NET_LOG.error(e1);
                }
            }
        }
        return doc;
    }

    public void run() {
        PropertyConfigurator.configure("log4j.properties");

        ZD_NET_LOG.info("Authorization...");
        login();

        Thread getLinksThread = new Thread(new ProducerLinks(goldenLinks));
        getLinksThread.start();

        Thread downloadFileThread = new Thread(new Consumer(goldenLinks));
        downloadFileThread.start();
    }
}