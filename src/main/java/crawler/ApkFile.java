package crawler;

import java.util.List;

public class ApkFile {
    private String url = "";
    private String location = "";
    private String name = "";
    private String category = "";
    private String description = "";
    private String version = "";
    private String md5 = "";
    private String sha1 = "";
    private String sha256 = "";

    private List<String> similarAppsUrl;

    public List<String> getSimilarAppsUrl() {
        return similarAppsUrl;
    }

    public void setSimilarAppsUrl(List<String> similarAppsUrl) {
        this.similarAppsUrl = similarAppsUrl;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public void setSha1(String sha1) {
        this.sha1 = sha1;
    }

    public void setSha256(String sha256) {
        this.sha256 = sha256;
    }

    public String getUrl() {
        return url;
    }

    public String getLocation() {
        return location;
    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public String getDescription() {
        return description;
    }

    public String getVersion() {
        return version;
    }

    public String getMd5() {
        return md5;
    }

    public String getSha1() {
        return sha1;
    }

    public String getSha256() {
        return sha256;
    }

    @Override
    public String toString() {
        return String.format("Name: %s; Category: %s; Version: %s; Description: %s;", getName(), getCategory(), getVersion(), getDescription());
    }
}
