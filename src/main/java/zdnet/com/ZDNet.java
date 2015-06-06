package zdnet.com;

import crawler.Constants;
import crawler.MyLinkedBlockingQueue;
import crawler.PortableExecutableFile;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.IOException;
import java.util.Map;

public class ZDNet implements Runnable {
    private Map<String, String> loginCookies;

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
                System.err.println("java.net.SocketTimeoutException: Read timed out. Reconnection.....");
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

    public void run() {
        login();

        Thread getLinksThread = new Thread(new ProducerLinks(goldenLinks, loginCookies));
        getLinksThread.start();

        Thread downloadFileThread = new Thread(new Consumer(goldenLinks, loginCookies));
        downloadFileThread.start();
    }
}