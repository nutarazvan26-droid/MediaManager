package model.files;

import java.io.FileNotFoundException;

 class Location{
    private int id;
    private String name;
    private String path;

    @Override
    public String toString() {
        return "Location{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", path='" + path + '\'' +
                '}';
    }

    public Location(int id, String name, String path) {
        this.id = id;
        this.name = name;
        this.path = path;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}

public final class MyLocationsFile extends JsonFile<Location>{

    public MyLocationsFile(String fileName) throws FileNotFoundException {
        super(fileName);
    }

    @Override
    public void ParseFile() {

    }
}