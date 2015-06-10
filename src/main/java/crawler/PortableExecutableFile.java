package crawler;

/**
 * Created by JACOB on 05.06.2015.
 */
public class PortableExecutableFile {
    private String url = "";
    private String location = "";
    private String name = "";
    private String category = "";
    private String description = "";
    private String license = "";
    private String version = "";
    private String operationSystem = "";
    private String systemRequirements = "";
    private String md5 = "";
    private String sha1 = "";
    private String sha256 = "";

    private String downloadUrl = "";

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
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

    public void setLicense(String license) {
        this.license = license.toUpperCase();
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public void setOperationSystem(String operationSystem) {
        this.operationSystem = operationSystem;
    }

    public void setSystemRequirements(String systemRequirements) {
        this.systemRequirements = systemRequirements;
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

    public String getLicense() {
        return license;
    }

    public String getVersion() {
        return version;
    }

    public String getOperationSystem() {
        return operationSystem;
    }

    public String getSystemRequirements() {
        return systemRequirements;
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
}
