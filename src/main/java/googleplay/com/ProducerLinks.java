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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
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
        apkFile.setName(doc.select("div > div > div.main-content > div > div > div.details-info > div > div.info-box-top > h1 > div").text());
        apkFile.setVersion(doc.select("div > div.details-section-contents > div:nth-child(4) > div.content").text());
        apkFile.setCategory(doc.select("div > div > div.main-content > div > div > div.details-info > div > div.info-box-top > h1 > div").text());
        apkFile.setDescription(doc.select("div.show-more-content.text-body > div.id-app-orig-desc").text());

        apkFile.setSimilarAppsUrl(getSimilarAppsFor(url.replaceAll("details", "similar")));
        apkFile.setPrice(doc.select("div.outer-container > div > div.main-content > div > div > div.details-info > div > div.info-box-bottom > div > div.details-actions-right > span > span > button > span:nth-child(3)").text());

        return apkFile;
    }

    private List<String> getSimilarAppsFor(String url){
        WebDriver driver = GooglePlay.connectTo(Constants.GOOGLEPLAY_COM  + url, false);
        Document doc = Jsoup.parse(driver.getPageSource());
        return doc.select("div.outer-container > div > div.main-content > div > div > div > div > div > div > div > div.details > a.title").stream().map(similarUrl -> Constants.GOOGLEPLAY_COM + similarUrl.attr("href")).collect(Collectors.toList());
    }

    private void getApkFiles(Elements elements, int deepForSimilarApp) {
        ArrayList<ApkFile> allApk = new ArrayList<>();
        ArrayList<String> allApkUrls = new ArrayList<>();
        Set<String> similarUrls = new HashSet<>();

        for (Element link : elements) {
            ApkFile apkFile = getInfoAboutApkFrom(link.attr("href"));
            allApk.add(apkFile);
            allApkUrls.add(Constants.GOOGLEPLAY_COM + link.attr("href"));
            APK_LOG.info(String.format("[%s-%s]. Got information from %s; <QUEUE>: %s;", allApk.size(), elements.size(), Constants.GOOGLEPLAY_COM + link.attr("href"), goldenLinks.size()));

            if (!Database.containsUrlAndVersionApk(apkFile.getUrl(), apkFile.getVersion())) {
                goldenLinks.add(apkFile);
                APK_LOG.info(String.format("[NEW] The APK file %s is added to processing; <QUEUE>: %s;", apkFile.getUrl(), goldenLinks.size()));
            } else {
                APK_LOG.info(String.format("File %s already in the database", apkFile.getUrl()));
            }
        }

        int simSize = 0;
        for (int i = 0; i < deepForSimilarApp; i++) {
            int kLimit = allApk.size();
            for (int k = simSize == 0 ? 0 : (kLimit - simSize + 1); k < kLimit; k++) {
                similarUrls.addAll(allApk.get(k).getSimilarAppsUrl());
                similarUrls.removeAll(allApkUrls); //remove all links that have been added from MainPage)

                for (String url : similarUrls) {
                    ApkFile similarApkFile = getInfoAboutApkFrom(url.replaceAll(Constants.GOOGLEPLAY_COM, ""));
                    if (similarApkFile.getPrice().equals("Install")) {
                        if (!Database.containsUrlAndVersionApk(similarApkFile.getUrl(), similarApkFile.getVersion())) {
                            allApk.add(similarApkFile);
                            allApkUrls.add(url);
                            goldenLinks.add(similarApkFile);
                            simSize++;
                            APK_LOG.info(String.format("[NEW SIMILAR] The APK file %s is added to processing; <QUEUE>: %s;", similarApkFile.getUrl(), goldenLinks.size()));
                        } else {
                            APK_LOG.info(String.format("File %s already in the database", similarApkFile.getUrl()));
                        }
                    } else {
                        APK_LOG.info(String.format("Similar app isn't free. %s; %s", url, similarApkFile.getPrice()));
                    }
                }
                similarUrls.clear();
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
                    Thread.sleep(1000);
//                    try {
//                        ((HtmlUnitDriver) driver).executeScript("document.getElementById('show-more-button').click()", new Object[]{""});
//                    }catch (WebDriverException e){
//                        APK_LOG.error(e);
//                    }
//                    Thread.sleep(6000);

                    Document doc = Jsoup.parse(driver.getPageSource());
                    elements = doc.select("div > div > div.main-content > div > div > div > div.card-list.two-cards > div > div > div.details > a.title");

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
break;
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