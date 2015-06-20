package googleplay.com;

import crawler.ApkFile;
import crawler.Constants;
import crawler.Database;
import crawler.MyLinkedBlockingQueue;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class ProducerLinks implements Runnable {
    static final Logger APK_LOG = Logger.getLogger("googlePlayLogger");

    private MyLinkedBlockingQueue<ApkFile> goldenLinks;
    private int linksFromCategory = -1;

    public ProducerLinks(MyLinkedBlockingQueue<ApkFile> goldenLinks) {
        this.goldenLinks = goldenLinks;
    }

    private ApkFile getInfoAboutApkFrom(String url) {
        ApkFile apkFile = new ApkFile();

        WebDriver driver = GooglePlay.connectTo(Constants.GOOGLEPLAY_COM + url, false);
        Document doc = Jsoup.parse(driver.getPageSource());

        apkFile.setUrl(Constants.GOOGLEPLAY_COM + url);
        apkFile.setLocation(Constants.LOCATION_TO_FILES_SAVING_APK);
        apkFile.setName(doc.select("div > div.details-info > div.info-container > div.document-title > div").text());
        apkFile.setVersion(doc.select("div > div.details-section-contents > div:nth-child(4) > div.content").text());
        apkFile.setCategory(doc.select("div > div.details-info > div.info-container > div:nth-child(3) > a > span").text());
        apkFile.setDescription(doc.select("div.show-more-content.text-body > div.id-app-orig-desc").text());

        apkFile.setSimilarAppsUrl(doc.select("div > div.details > h2 > a").stream().map(similarUrl -> Constants.GOOGLEPLAY_COM + similarUrl.attr("href")).collect(Collectors.toList()));
        apkFile.setPrice(doc.select("#body-content > div.outer-container > div > div.main-content > div:nth-child(1) > div > div.details-info > div.info-container > div.details-actions > span > span > button > span:nth-child(3)").text());

        return apkFile;
    }

    private void getApkFiles(Elements elements, int deepForSimilarApp) {
        ArrayList<ApkFile> allApk = new ArrayList<>();
        ArrayList<String> allApkUrls = new ArrayList<>();
        Set<String> similarUrls = new HashSet<>();

        for (Element link : elements) {
            allApk.add(getInfoAboutApkFrom(link.attr("href")));
            allApkUrls.add(Constants.GOOGLEPLAY_COM + link.attr("href"));
            APK_LOG.info(String.format("[%s-%s]. Got information from %s; <QUEUE>: %s;", allApk.size(), elements.size(), Constants.GOOGLEPLAY_COM + link.attr("href"), goldenLinks.size()));
        }

        for (int i = 0; i < deepForSimilarApp; i++) {
            int simSize = similarUrls.size();
            similarUrls.clear();

            for (int k = simSize == 0 ? 0 : (allApk.size() - simSize + 1); k < allApk.size(); k++) {
                similarUrls.addAll(allApk.get(k).getSimilarAppsUrl());
            }

            APK_LOG.info(String.format("Similar apps size = %s", similarUrls.size()));
            similarUrls.removeAll(allApkUrls); //remove all links that have been added from MainPage)
            APK_LOG.info("Remove all links that have been added from MainPage...");
            APK_LOG.info(String.format("Similar apps size = %s", similarUrls.size()));

            for (String url : similarUrls) { //new unique link
                ApkFile similarApkFile = getInfoAboutApkFrom(url.replaceAll(Constants.GOOGLEPLAY_COM,""));
                if (similarApkFile.getPrice().equals("Install")) {
                    allApk.add(similarApkFile);
                    allApkUrls.add(url);
                    APK_LOG.info(String.format("Got information about similar app from %s; <QUEUE>: %s", url, goldenLinks.size()));
                }else{
                    APK_LOG.info(String.format("Similar app isn't free. %s; %s", url, similarApkFile.getPrice()));
                }
            }
        }

        for (ApkFile apkFile : allApk) {
            if (!Database.containsUrlAndVersionApk(apkFile.getUrl(), apkFile.getVersion())) {
                goldenLinks.add(apkFile);
                APK_LOG.info(String.format("[NEW] The APK file %s is added to processing", apkFile.getUrl()));
            } else {
                APK_LOG.info(String.format("File %s already in the database", apkFile.getUrl()));
            }
        }
        linksFromCategory = allApk.size();
    }

    @Override
    public void run() {
        for (String category : Constants.CATEGORIES_GOOGLE) {
            WebDriver driver = GooglePlay.connectTo(Constants.GOOGLEPLAY_COM + category, true);

            APK_LOG.info(String.format("Connected to %s", Constants.GOOGLEPLAY_COM + category));

            int last = 0;
            Elements elements;
            while (true) {
                try {
                    ((HtmlUnitDriver) driver).executeScript("document.getElementById('show-more-button').click()", new Object[]{""});
                    Thread.sleep(7000);

                    Document doc = Jsoup.parse(driver.getPageSource());
                    elements = doc.select("div > div.details > h2 > a");

                    if (last == elements.size()) {
                        break;
                    } else {
                        last = elements.size();
                    }

                    APK_LOG.info(String.format("Got %s applications from Main page. Category: %s", elements.size(), category));

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            getApkFiles(elements, Constants.DEEP_FOR_SIMILAR);

            APK_LOG.info(String.format("FINAL RESULT: Got %s applications from '%s' category", linksFromCategory, category));
        }
        try {
            goldenLinks.put(new ApkFile()); // LAST ELEMENT. 'Consumer' thread will stopped.
            APK_LOG.info("Finish. Last category.");
        } catch (InterruptedException e) {
            APK_LOG.error(e);
        }
        GooglePlay.driver.close();
    }
}