package zdnet.com;

import crawler.Constants;
import crawler.Database;
import crawler.MyLinkedBlockingQueue;
import crawler.PortableExecutableFile;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.Map;

public class ProducerLinks implements Runnable {
    static final Logger ZD_NET_LOG = Logger.getLogger("zdNetLogger");

    private MyLinkedBlockingQueue<PortableExecutableFile> goldenLinks;
    private Map<String, String> loginCookies;

    public ProducerLinks(MyLinkedBlockingQueue<PortableExecutableFile> goldenLinks, Map<String, String> loginCookies) {
        this.goldenLinks = goldenLinks;
        this.loginCookies = loginCookies;
    }

    private Document connectTo(String url) {
        Document doc = null;
        while (doc == null) {
            try {
                doc = Jsoup.connect(url).cookies(loginCookies).get();
            } catch (IOException e) {
                System.err.println(String.format("Reconnection to %s", url));
                ZD_NET_LOG.warn(String.format("Reconnection to %s", url));
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            }
        }
        return doc;
    }

    private PortableExecutableFile isValid(Element link) {
        PortableExecutableFile pe = new PortableExecutableFile();

        String peUrl = link.select("div > h3 > a:nth-child(1)").attr("href");

        Document doc = connectTo(Constants.ZD_NET_COM + peUrl);

        pe.setUrl(Constants.ZD_NET_COM + peUrl);
        pe.setName(doc.select("#mantle_skin > div.contentWrapper > div > div > div.col-8 > article > header > h1").text());
        pe.setCategory(doc.select("#breadcrumb > ul > li:nth-child(3) > a").text());
        pe.setDescription(doc.select("#mantle_skin > div.contentWrapper > div > div > div.col-8 > article > section > div.storyBody").text());

        Elements details = doc.select("#mantle_skin > div.contentWrapper > div > div > div.col-8 > article > table > tbody > tr");
        for (Element detail : details) {
            switch (detail.select("th").text()) {
                case "License":
                    pe.setLicense(detail.select("td").text());
                    break;
                case "Version":
                    pe.setVersion(detail.select("td").text());
                    break;
                case "Operating System":
                    pe.setOperationSystem(detail.select("td").text());
                    break;
                case "System Requirements":
                    pe.setSystemRequirements(detail.select("td").text());
                    break;
            }
        }

        ZD_NET_LOG.info(String.format("URL: %s; OS: %s; goldenLinksSize: %s;", pe.getUrl(), pe.getOperationSystem(), goldenLinks.size()));

        if (pe.getOperationSystem().equals("iOS") || pe.getOperationSystem().equals("Android") ||
                pe.getOperationSystem().equals("Webware") || pe.getOperationSystem().contains("Mobile Windows Phone") ||
                pe.getOperationSystem().contains("Playstation")) {
            return null;
        }

        if (Database.containsUrlAndVersion(pe.getUrl(), pe.getVersion())) {
            return null;
        }

        return pe;
    }

    private boolean hasNext(Document doc) {
        Elements next = doc.select("#mantle_skin > div.contentWrapper > div > div > div.col-8 > div.row > div.col-6 > section > nav > ul > li");
        return next.last().text().equals("Next");
    }

    public void run() {
        try {
            Document doc = connectTo(Constants.ZD_NET_COM_DOWNLOAD);

            while (true) {
                Elements links = doc.getElementsByAttributeValueContaining("class", "downloads item");
                for (Element link : links) {
                    PortableExecutableFile goldenPe = isValid(link);
                    if (goldenPe != null) {
                        if (!goldenLinks.containsByUrl(goldenPe)) {
                            goldenLinks.put(goldenPe);
                        }
                    }
                }
                if (hasNext(doc)) {
                    String nextPageUrl = doc.select("#mantle_skin > div.contentWrapper > div > div > div.col-8 > div.row > div.col-6 > section > nav > ul > li").last().child(0).attr("href");
                    doc = connectTo(Constants.ZD_NET_COM + nextPageUrl);

                    ZD_NET_LOG.info("\n"+Constants.ZD_NET_COM_DOWNLOAD+nextPageUrl);
                } else {
                    goldenLinks.put(new PortableExecutableFile()); // LAST PAGE. 'Consumer' thread will stopped.
                    break;
                }
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}