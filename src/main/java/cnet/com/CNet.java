package cnet.com;

import crawler.MyLinkedBlockingQueue;
import crawler.PortableExecutableFile;
import org.apache.log4j.Logger;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;

import java.io.IOException;

import org.jsoup.nodes.Document;

public class CNet implements Runnable {
    static final Logger C_NET_LOG = Logger.getLogger("cNetLogger");

    private MyLinkedBlockingQueue<PortableExecutableFile> goldenLinks = new MyLinkedBlockingQueue<>();

    public static Document connectTo(String url) {
        Document doc = null;
        while (doc == null) {
            try {
                doc = Jsoup.connect(url).get();
            } catch (IOException e) {
                if (e instanceof HttpStatusException) {
                    if (((HttpStatusException) e).getStatusCode() == 404) {
                        C_NET_LOG.warn(String.format("Page not found 404. %s", url));
                        return null;
                    }
                }

                C_NET_LOG.warn(String.format("Reconnection to %s", url));
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e1) {
                    C_NET_LOG.error(e1);
                }
            }
        }
        return doc;
    }

    public void run() {
        Thread getLinksThread = new Thread(new ProducerLinks(goldenLinks));
        getLinksThread.start();

        Thread downloadFileThread = new Thread(new Consumer(goldenLinks));
        downloadFileThread.start();
    }
}