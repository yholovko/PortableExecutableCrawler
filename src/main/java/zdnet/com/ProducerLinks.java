package zdnet.com;

import crawler.Constants;
import crawler.Database;
import crawler.MyLinkedBlockingQueue;
import crawler.PortableExecutableFile;
import org.apache.log4j.Logger;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class ProducerLinks implements Runnable {
    static final Logger ZD_NET_LOG = Logger.getLogger("zdNetLogger");

    private MyLinkedBlockingQueue<PortableExecutableFile> goldenLinks;

    public ProducerLinks(MyLinkedBlockingQueue<PortableExecutableFile> goldenLinks) {
        this.goldenLinks = goldenLinks;
    }

    private PortableExecutableFile isValid(Element link) {
        PortableExecutableFile pe = new PortableExecutableFile();

        String peUrl = link.select("div > h3 > a:nth-child(1)").attr("href");
        Document doc = ZDNet.connectTo(Constants.ZD_NET_COM + peUrl);

        if (doc != null) {
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
        } else {
            return null;
        }

        if (pe.getOperationSystem().equals("iOS") || pe.getOperationSystem().equals("Android") ||
                pe.getOperationSystem().equals("Webware") || pe.getOperationSystem().contains("Mobile Windows Phone") ||
                pe.getOperationSystem().contains("Playstation")) {
            ZD_NET_LOG.info(String.format("<URL>: %s; <OS>: %s; <STATUS>: IGNORED; <QUEUE>: %s;", pe.getUrl(), pe.getOperationSystem(), goldenLinks.size()));

            return null;
        }

        if (Database.containsUrlAndVersionPE(pe.getUrl(), pe.getVersion())) {
            ZD_NET_LOG.info(String.format("<URL>: %s; <OS>: %s; <STATUS>: IGNORED (already in the database); <QUEUE>: %s;", pe.getUrl(), pe.getOperationSystem(), goldenLinks.size()));

            return null;
        }

        ZD_NET_LOG.info(String.format("<URL>: %s; <OS>: %s; <STATUS>: PROCESSING; <QUEUE>: %s;", pe.getUrl(), pe.getOperationSystem(), goldenLinks.size()));

        return pe;
    }

    private boolean hasNext(Document doc) {
        Elements next = doc.select("#mantle_skin > div.contentWrapper > div > div > div.col-8 > div.row > div.col-6 > section > nav > ul > li");
        return next.last().text().equals("Next");
    }

    public void run() {
        try {
            Document doc = ZDNet.connectTo(Constants.ZD_NET_COM_DOWNLOAD);
            ZD_NET_LOG.info("\n\n" + Constants.ZD_NET_COM_DOWNLOAD);

            while (true) {
                if (doc != null) {
                    Elements links = doc.getElementsByAttributeValueContaining("class", "downloads item");
                    for (Element link : links) {
                        PortableExecutableFile goldenPe = isValid(link);
                        if (goldenPe != null) {
                            if (!goldenLinks.containsByUrlPe(goldenPe)) {
                                goldenLinks.put(goldenPe);
                            }
                        }
                    }
                    if (hasNext(doc)) {
                        String nextPageUrl = doc.select("#mantle_skin > div.contentWrapper > div > div > div.col-8 > div.row > div.col-6 > section > nav > ul > li").last().child(0).attr("href");
                        doc = ZDNet.connectTo(Constants.ZD_NET_COM + nextPageUrl);

                        ZD_NET_LOG.info("\n\n" + Constants.ZD_NET_COM + nextPageUrl);
                    } else {
                        goldenLinks.put(new PortableExecutableFile()); // LAST PAGE. 'Consumer' thread will stopped.
                        ZD_NET_LOG.info("\n\n Finish. Last page.");
                        break;
                    }
                } else {
                    break;
                }
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
            ZD_NET_LOG.error(e);
        }
    }
}