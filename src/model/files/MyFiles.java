package model.files;

import java.io.FileNotFoundException;
import java.util.Date;

class MyFile {
    private int id;
    private int locationId;
    private String name;
    private String extension;
    private String type;
    private long sizeBytes;
    private Date lastModified;
    private String path;
}

public final class MyFiles extends JsonFile<MyFile>{

    public MyFiles(String fileName) throws FileNotFoundException {
        super(fileName);
    }

    @Override
    public void ParseFile() {

    }
}