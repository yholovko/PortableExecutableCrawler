package zdnet.com;

import crawler.Constants;
import crawler.PortableExecutableFile;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by JACOB on 05.06.2015.
 */
public class Producer implements Runnable{
    private LinkedBlockingQueue<PortableExecutableFile> goldenLinks;
    private Map<String, String> loginCookies;
    private PortableExecutableFile goldenPe;

    public Producer(LinkedBlockingQueue goldenLinks, Map<String, String> loginCookies) {
        this.goldenLinks = goldenLinks;
        this.loginCookies = loginCookies;
    }

    private Document connectTo(String url) throws IOException {
        return Jsoup.connect(url).cookies(loginCookies).get();
    }

    private PortableExecutableFile isValid(Element link) throws IOException {
        String peUrl = link.select("div > h3 > a:nth-child(1)").attr("href");
        Document doc = connectTo(Constants.zdNetCom+peUrl);

        // if (Android || IOS ) return NULL;
        // if (URL in database and CURRENT_VERSION == DATABASE_VERSION)return NULL;

        PortableExecutableFile pe = new PortableExecutableFile();
        //parse all info
        return pe;
    }

    public void run() {
        try {
            Document doc = connectTo(Constants.zdNetComDownload);
            Elements links = doc.getElementsByAttributeValueContaining("class", "downloads item");
            for (Element link : links){
                goldenPe = isValid(link);
                if (goldenPe != null){
                    goldenLinks.put(goldenPe);
                }
            }

            System.out.println();
//            if (/* last link */){
//                goldenLinks.put("END");
//            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
