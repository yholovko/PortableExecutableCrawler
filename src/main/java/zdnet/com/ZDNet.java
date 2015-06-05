package zdnet.com;

import crawler.Constants;
import crawler.PortableExecutableFile;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by JACOB on 05.06.2015.
 */
public class ZDNet implements Runnable {
    private String host;
    private String user;
    private String passwordToDB;
    private String databaseName;
    private String locationToFilesSaving;

    private Map<String, String> loginCookies;

    private LinkedBlockingQueue<PortableExecutableFile> goldenLinks = new LinkedBlockingQueue<PortableExecutableFile>();

    public ZDNet(String host, String user, String passwordToDB, String databaseName, String locationToFilesSaving) {
        this.host = host;
        this.user = user;
        this.passwordToDB = passwordToDB;
        this.databaseName = databaseName;
        this.locationToFilesSaving = locationToFilesSaving;
    }

    private void login() throws IOException {
        Connection.Response res = Jsoup.connect(Constants.zdNetComLogin)
                .data("login[email]", Constants.email, "login[password]", Constants.password)
                .method(Connection.Method.POST)
                .execute();

        loginCookies = res.cookies();
    }

    public void run() {
        try {
            login();

            Thread getLinksThread = new Thread(new Producer(goldenLinks, loginCookies));
            getLinksThread.start();

            Thread downloadFileThread = new Thread(new Consumer(goldenLinks, loginCookies));
            downloadFileThread.start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
