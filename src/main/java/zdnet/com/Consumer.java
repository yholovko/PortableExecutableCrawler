package zdnet.com;

import crawler.PortableExecutableFile;

import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by JACOB on 05.06.2015.
 */
public class Consumer implements Runnable {
    private LinkedBlockingQueue<PortableExecutableFile> goldenLinks;
    private Map<String, String> loginCookies;

    public Consumer(LinkedBlockingQueue<PortableExecutableFile> goldenLinks, Map<String, String> loginCookies) {
        this.goldenLinks = goldenLinks;
        this.loginCookies = loginCookies;
    }

    public void run() {
//        String res = "";
//        while (!res.equals("END")) {
//            try {
//                res = goldenLinks.take();
//                System.out.println("Consumer: " + res);
//                Thread.sleep(3000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
    }
}
