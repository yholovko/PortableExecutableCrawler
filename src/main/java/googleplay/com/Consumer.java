package googleplay.com;

import crawler.ApkFile;
import crawler.Constants;
import crawler.Database;
import crawler.MyLinkedBlockingQueue;
import de.onyxbits.raccoon.CliService;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Consumer implements Runnable {
    static final Logger APK_LOG = Logger.getLogger("googlePlayLogger");
    private MyLinkedBlockingQueue<ApkFile> goldenLinks;

    public Consumer(MyLinkedBlockingQueue<ApkFile> goldenLinks) {
        this.goldenLinks = goldenLinks;
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
            APK_LOG.error("Could not generate hash from file", ex);
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

    @Override
    public void run() {
        while (true) {
            try {
                ApkFile apkFile = goldenLinks.take();
                if (!apkFile.getUrl().equals("")) {
                    APK_LOG.info(String.format("Start to download file %s", apkFile.getUrl()));

                    String packageName = apkFile.getUrl().replaceAll("https:\\//play.google.com/store/apps/details\\?id=", "");

                    String[] args = {"-i", packageName, "-u", "-a", Constants.LOCATION_TO_FILES_SAVING_APK};

                    try {
                        CliService cs = new CliService(args, APK_LOG);
                        cs.run();

                        if (cs.getStatus().equals("[DOWNLOADED]")){
                            APK_LOG.info("File " + apkFile.getUrl() + " downloaded");
                            apkFile.setLocation(cs.getFileLocation());

                            File file = new File(apkFile.getLocation());
                            apkFile.setMd5(generateMD5(file));
                            apkFile.setSha1(generateSHA1(file));
                            apkFile.setSha256(generateSHA256(file));

                            if (Database.isAppExistsAPK(apkFile.getMd5())) {
                                new File(apkFile.getLocation()).delete();
                                APK_LOG.info(String.format("File %s already in the database. Deleted", apkFile.getUrl()));
                            } else {
                                Database.insertToDatabaseAPK(apkFile);
                                APK_LOG.info(String.format("Information about %s inserted in the database", apkFile.getUrl()));
                            }
                        } else {
                            APK_LOG.error(String.format("Error while downloading file %s. Package name: %s", apkFile.getUrl(), packageName));
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                        APK_LOG.error(e);
                    }
                } else {
                    APK_LOG.info("LAST ELEMENT");
                    return; //last element
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
                APK_LOG.error(e);
            }
        }
    }
}