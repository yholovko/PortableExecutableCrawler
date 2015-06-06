package zdnet.com;

import crawler.MyLinkedBlockingQueue;
import crawler.PortableExecutableFile;

import java.util.Map;

public class Consumer implements Runnable {
    private MyLinkedBlockingQueue<PortableExecutableFile> goldenLinks;
    private Map<String, String> loginCookies;

    public Consumer(MyLinkedBlockingQueue<PortableExecutableFile> goldenLinks, Map<String, String> loginCookies) {
        this.goldenLinks = goldenLinks;
        this.loginCookies = loginCookies;
    }

    public void run() {
        // проверить ГЕЙМ категорию
        //Playstation 4 Console Games Playstation;  http://downloads.zdnet.com/product/2095-76406344/
        // Mobile Windows Phone 8 http://downloads.zdnet.com/product/20428-76406375/
        // Webware http://downloads.zdnet.com/product/2649-76406328/
        // http://downloads.zdnet.com/product/2356-75449028/
        //
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
