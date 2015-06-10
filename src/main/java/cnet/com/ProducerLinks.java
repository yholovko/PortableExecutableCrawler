package cnet.com;

import crawler.Constants;
import crawler.Database;
import crawler.MyLinkedBlockingQueue;
import crawler.PortableExecutableFile;
import org.apache.log4j.Logger;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class ProducerLinks implements Runnable {
    static final Logger C_NET_LOG = Logger.getLogger("cNetLogger");

    private MyLinkedBlockingQueue<PortableExecutableFile> goldenLinks;

    public ProducerLinks(MyLinkedBlockingQueue<PortableExecutableFile> goldenLinks) {
        this.goldenLinks = goldenLinks;
    }

    private PortableExecutableFile isValid(Element link) {
        PortableExecutableFile pe = new PortableExecutableFile();

        String peUrl = link.attr("href");

        Document doc = CNet.connectTo(peUrl);

        if (doc != null) {
            pe.setUrl(peUrl);
            pe.setName(doc.select("#content-body-product-single > h1 > span").text());
            pe.setCategory(doc.select("#content-body-product-single > nav > ul > li:nth-child(3) > a").text());
            pe.setDescription(doc.select("#publisher-description").text());
            pe.setLicense(doc.select("#product-upper-container > div.quick-specs-container > ul.one > li.price > div.product-landing-quick-specs-row-content").text());
            pe.setVersion(doc.select("#product-upper-container > div.quick-specs-container > ul.one > li:nth-child(1) > div.product-landing-quick-specs-row-content").text());
            pe.setOperationSystem(doc.select("#product-upper-container > div.quick-specs-container > ul.two > li:nth-child(3) > div.product-landing-quick-specs-row-content.OneLinkNoTx").text());

            pe.setDownloadUrl(doc.select("#product-upper-container > div.button-ratings-container > div.button > div > div.download-now.title-detail-button-dln > a").attr("href"));
        } else {
            return null;
        }

        if (pe.getDownloadUrl().equals("")) {
            C_NET_LOG.info(String.format("Can not download the file %s;", pe.getUrl()));

            return null;
        }

        if (Database.containsUrlAndVersion(pe.getUrl(), pe.getVersion())) {
            C_NET_LOG.info(String.format("<URL>: %s; <OS>: %s; <STATUS>: IGNORED (already in the database); <QUEUE>: %s;", pe.getUrl(), pe.getOperationSystem(), goldenLinks.size()));

            return null;
        }

        C_NET_LOG.info(String.format("<URL>: %s; <OS>: %s; <STATUS>: PROCESSING; <QUEUE>: %s;", pe.getUrl(), pe.getOperationSystem(), goldenLinks.size()));

        return pe;
    }

    private boolean hasNext(Document doc) {
        Elements next = doc.select("#listing-product-list > li.listing-bar > div.listing-bar-right > div > ul > li.next > a");
        return next.last().text().equals("Next");
    }

    private Elements getCategories(Document doc) {
        return doc.select("#main > div > dl.catNav.catFly > dd > a");
    }

    public void run() {
        try {
            Document doc = CNet.connectTo(Constants.CNET_COM_WINDOWS);
            C_NET_LOG.info("\n\n" + Constants.CNET_COM_WINDOWS);

            for (Element category : getCategories(doc)) {
                doc = CNet.connectTo(category.attr("href")); //select directory

                while (true) {
                    if (doc != null) {
                        Elements links = doc.select("#listing-product-list > li > div.result-info > div.result-name > a");
                        for (Element link : links) {
                            PortableExecutableFile goldenPe = isValid(link);
                            if (goldenPe != null) {
                                if (!goldenLinks.containsByUrl(goldenPe)) {
                                    goldenLinks.put(goldenPe);
                                }
                            }
                        }
                        if (hasNext(doc)) {
                            String nextPageUrl = doc.select("#listing-product-list > li.listing-bar > div.listing-bar-right > div > ul > li.next > a").last().attr("href");
                            doc = CNet.connectTo(Constants.CNET_COM + nextPageUrl);

                            C_NET_LOG.info("\n\n" + Constants.CNET_COM + nextPageUrl);
                        } else {
                            goldenLinks.put(new PortableExecutableFile()); // LAST PAGE. 'Consumer' thread will stopped.
                            break;
                        }
                    } else {
                        break;
                    }
                }
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
            C_NET_LOG.error(e);
        }
    }
}