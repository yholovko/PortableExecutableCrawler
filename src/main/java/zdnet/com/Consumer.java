package zdnet.com;

import crawler.Constants;
import crawler.MyLinkedBlockingQueue;
import crawler.PortableExecutableFile;
import org.apache.log4j.Logger;
import org.jsoup.nodes.Document;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.Date;

public class Consumer implements Runnable {
    static final Logger ZD_NET_LOG = Logger.getLogger("zdNetLogger");

    private MyLinkedBlockingQueue<PortableExecutableFile> goldenLinks;

    public Consumer(MyLinkedBlockingQueue<PortableExecutableFile> goldenLinks) {
        this.goldenLinks = goldenLinks;
    }

    private boolean saveFile(String fileName, URL download) {
        try (FileOutputStream fos = new FileOutputStream(Constants.LOCATION_TO_FILES_SAVING + fileName)) {
            ReadableByteChannel rbc = Channels.newChannel(download.openStream());
            fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
            fos.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            ZD_NET_LOG.error(e);
        }

        return false;
    }

    private String getValidNameForFS(String name) {
        return name.replaceAll(" ", "")
                .replaceAll("\\\\", "")
                .replaceAll("/", "")
                .replaceAll(":", "")
                .replaceAll("\\*", "")
                .replaceAll("\\?", "")
                .replaceAll("\"", "")
                .replaceAll("\\<", "")
                .replaceAll("\\>", "")
                .replaceAll("\\|", "");
    }

    private String getExtension(String url) {
        int lastDotIndex = url.lastIndexOf(".");
        if (lastDotIndex != -1) {
            return url.substring(lastDotIndex, url.length());
        } else {
            return "";
        }
    }

    public void run() {
        while (true) {
            try {
                PortableExecutableFile pe = goldenLinks.take();
                if (!pe.getUrl().equals("")) {
                    Document doc = ZDNet.connectTo(pe.getUrl() + "download/");

                    String url = doc.select("#mantle_skin > div.contentWrapper > div > div > div.col-8 > article > div.storyBody > p.quiet > a").attr("href");
                    String extension = getExtension(url);

                    if (extension.length() == 4 && !extension.equals(".htm")) {
                        String filename = getValidNameForFS(pe.getName()) + new Date().getTime() + extension;

                        ZD_NET_LOG.info("Start to download file " + pe.getUrl());

                        if (saveFile(filename, new URL(url))) {
                            ZD_NET_LOG.info("File " + pe.getUrl() + " downloaded");
                            pe.setLocation(Constants.LOCATION_TO_FILES_SAVING + "/" + filename);
                            //get MD5, SHA1, SHA256
                            //insert to database
                        } else {
                            ZD_NET_LOG.info("Error while downloading file " + pe.getUrl());
                        }
                    } else { // not the PE file. The 'download' button is redirect to Google Play, Amazon, Apple Store etc....
                        ZD_NET_LOG.info(String.format("WARNING. %s ISN'T a PE file. Link for downloading: %s", pe.getUrl(), url));
                    }
                } else {
                    return; //last element
                }
            } catch (InterruptedException | MalformedURLException e) {
                e.printStackTrace();
                ZD_NET_LOG.error(e);
            }
        }
    }
}