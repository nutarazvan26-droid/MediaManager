package model.files.common;

public interface ICommonFileModel {
    boolean equals(Object o);

    int hashCode();

    String toString();

    int getId();

    void setId(int id);

    String getName();

    void setName(String name);

    String getPath();

    void setPath(String path);
}
