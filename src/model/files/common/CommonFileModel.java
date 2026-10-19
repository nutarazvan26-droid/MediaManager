package model.files.common;

import java.util.Objects;

/**
 * This is the base file model class for both the locations holder and the in/out files holder
 * It implements the base interface ICommonFileModel and Comparable
 * @since 2026.19.10
 * @author RazvanNuta
 * @see java.lang.Comparable
 * @see model.files.common.ICommonFileModel
 */
public abstract class CommonFileModel implements Comparable, ICommonFileModel {
    private int id;
    private String name;
    private String path;

    public CommonFileModel(int id, String name, String path) {
        this.id = id;
        this.name = name;
        this.path = path;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof CommonFileModel)) return false;
        CommonFileModel that = (CommonFileModel) o;
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

    @Override
    public int getId() {
        return id;
    }

    @Override
    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getPath() {
        return path;
    }

    @Override
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
        if (!(o instanceof CommonFileModel)) return -1; // or throw InvalidComparedCustomException?
        ICommonFileModel comparedFile = (ICommonFileModel) o;
        return this.name.compareTo(comparedFile.getName());
    }
}
