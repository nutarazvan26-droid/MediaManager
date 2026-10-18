package model.files;

import java.util.Objects;

public abstract class CommonFile implements Comparable{
    private int id;
    private String name;
    private String path;

    public CommonFile(int id, String name, String path) {
        this.id = id;
        this.name = name;
        this.path = path;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof CommonFile)) return false;
        CommonFile that = (CommonFile) o;
        return id == that.id && Objects.equals(name, that.name) && Objects.equals(path, that.path);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, path);
    }

    @Override
    public String toString() {
        return "CommonFile{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", path='" + path + '\'' +
                '}';
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

    /**
     * Simple logic to sort files by file name, for other types of supported sorting, will implement different kinds of Comparators.
     * @param o the object to be compared.
     * @return -1 if the first compared file is alphabetically smaller than the second based on the name
     * @return 0 if the first compared file has the same name with the second file
     * @return 1 if the first compared file is alphabetically bigger than the second based on the name
     */
    @Override
    public int compareTo(Object o) {
        if (!(o instanceof CommonFile)) return -1; // or throw InvalidComparedCustomException?
        CommonFile comparedFile = (CommonFile) o;
        return this.name.compareTo(comparedFile.getName());
    }
}
