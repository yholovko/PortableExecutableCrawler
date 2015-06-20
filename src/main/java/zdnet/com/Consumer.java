package zdnet.com;

import crawler.Constants;
import crawler.Database;
import crawler.MyLinkedBlockingQueue;
import crawler.PortableExecutableFile;
import org.apache.log4j.Logger;
import org.jsoup.nodes.Document;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

public class Consumer implements Runnable {
    static final Logger ZD_NET_LOG = Logger.getLogger("zdNetLogger");

    private MyLinkedBlockingQueue<PortableExecutableFile> goldenLinks;

    public Consumer(MyLinkedBlockingQueue<PortableExecutableFile> goldenLinks) {
        this.goldenLinks = goldenLinks;
    }

    private boolean saveFile(String fileName, URL download) {
        new File(Constants.LOCATION_TO_FILES_SAVING_PE).mkdirs();

        try (FileOutputStream fos = new FileOutputStream(Constants.LOCATION_TO_FILES_SAVING_PE + fileName)) {
            URLConnection conn = download.openConnection();
            conn.setConnectTimeout(10 * 60 * 1000);
            conn.setReadTimeout(10 * 60 * 1000); //10 min

            ReadableByteChannel rbc = Channels.newChannel(conn.getInputStream());
            fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);

            return true;
        } catch (IOException e) {
            ZD_NET_LOG.error(e);
            new File(Constants.LOCATION_TO_FILES_SAVING_PE + fileName).delete();
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

    private String hashFile(File file, String algorithm) {
        try (FileInputStream inputStream = new FileInputStream(file)) {
            MessageDigest digest = MessageDigest.getInstance(algorithm);

            byte[] bytesBuffer = new byte[1024];
            int bytesRead;

            while ((bytesRead = inputStream.read(bytesBuffer)) != -1) {
                digest.update(bytesBuffer, 0, bytesRead);
            }

            byte[] hashedBytes = digest.digest();

            return convertByteArrayToHexString(hashedBytes);
        } catch (NoSuchAlgorithmException | IOException ex) {
            ZD_NET_LOG.error("Could not generate hash from file", ex);
        }
        return "";
    }

    private String generateMD5(File file) {
        return hashFile(file, "MD5");
    }

    private String generateSHA1(File file) {
        return hashFile(file, "SHA-1");
    }

    private String generateSHA256(File file) {
        return hashFile(file, "SHA-256");
    }

    private String convertByteArrayToHexString(byte[] arrayBytes) {
        StringBuilder stringBuffer = new StringBuilder();
        for (byte arrayByte : arrayBytes) {
            stringBuffer.append(Integer.toString((arrayByte & 0xff) + 0x100, 16)
                    .substring(1));
        }
        return stringBuffer.toString();
    }

    public void run() {
        while (true) {
            try {
                PortableExecutableFile pe = goldenLinks.take();
                if (!pe.getUrl().equals("")) {
                    Document doc = ZDNet.connectTo(pe.getUrl() + "download/");

                    if (doc != null) {
                        String url = doc.select("#mantle_skin > div.contentWrapper > div > div > div.col-8 > article > div.storyBody > p.quiet > a").attr("href");
                        String extension = getExtension(url);

                        if (extension.length() == 4 && !extension.equals(".htm") && !extension.equals(".php")) {
                            String filename = getValidNameForFS(pe.getName()) + new Date().getTime() + extension;

                            ZD_NET_LOG.info("Start to download file " + pe.getUrl());

                            if (saveFile(filename, new URL(url))) {
                                ZD_NET_LOG.info("File " + pe.getUrl() + " downloaded");
                                pe.setLocation(Constants.LOCATION_TO_FILES_SAVING_PE + filename);
                                File file = new File(pe.getLocation());
                                pe.setMd5(generateMD5(file));
                                pe.setSha1(generateSHA1(file));
                                pe.setSha256(generateSHA256(file));

                                if (Database.isAppExistsPE(pe.getMd5(), pe.getSha1(), pe.getSha256())) {
                                    new File(pe.getLocation()).delete();
                                    ZD_NET_LOG.info(String.format("File %s already in the database. Deleted", pe.getUrl()));
                                } else {
                                    Database.insertToDatabasePE(pe);
                                    ZD_NET_LOG.info(String.format("Information about %s inserted in the database", pe.getUrl()));
                                }
                            } else {
                                new File(pe.getLocation()).delete();
                                ZD_NET_LOG.error("Error while downloading file " + pe.getUrl());
                            }
                        } else { // not the PE file. The 'download' button is redirect to Google Play, Amazon, Apple Store etc....
                            ZD_NET_LOG.warn(String.format("%s isn't a PE file. Link for downloading: %s", pe.getUrl(), url));
                        }
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