package model.files.MediaFiles;

import model.files.CommonFile;

import java.util.Date;
import java.util.Objects;

public class MyFile extends CommonFile {
    private int locationId;
    private String extension;
    private String type;
    private long sizeBytes;
    private Date lastModified;

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof MyFile)) return false;
        if (!super.equals(o)) return false;
        MyFile myFile = (MyFile) o;
        return locationId == myFile.locationId && sizeBytes == myFile.sizeBytes && Objects.equals(extension, myFile.extension) && Objects.equals(type, myFile.type) && Objects.equals(lastModified, myFile.lastModified);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), locationId, extension, type, sizeBytes, lastModified);
    }

    public int getLocationId() {
        return locationId;
    }

    public void setLocationId(int locationId) {
        this.locationId = locationId;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getSizeBytes() {
        return sizeBytes;
    }

    public void setSizeBytes(long sizeBytes) {
        this.sizeBytes = sizeBytes;
    }

    public Date getLastModified() {
        return lastModified;
    }

    public void setLastModified(Date lastModified) {
        this.lastModified = lastModified;
    }

    public MyFile(int id, String name, String path, int locationId, String extension, String type, long sizeBytes, Date lastModified) {
        super(id, name, path);
        this.locationId = locationId;
        this.extension = extension;
        this.type = type;
        this.sizeBytes = sizeBytes;
        this.lastModified = lastModified;
    }

    @Override
    public String toString() {
        return "MyFile{" +
                "locationId=" + locationId +
                ", extension='" + extension + '\'' +
                ", type='" + type + '\'' +
                ", sizeBytes=" + sizeBytes +
                ", lastModified=" + lastModified +
                '}';
    }
}
