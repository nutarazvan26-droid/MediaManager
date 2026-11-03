package model.files.mediaFiles;

import java.util.Comparator;

public enum FileSortType {
    NAME(Comparator.naturalOrder()),
    SIZE(Comparator.comparingLong(MyFileModel::getSizeBytes)),
    TYPE(Comparator.comparing(MyFileModel::getType)),
    LAST_MODIFIED(Comparator.comparing(MyFileModel::getLastModified));

    private Comparator<MyFileModel> comparator;

    FileSortType(Comparator<MyFileModel> comparator) {
        this.comparator = comparator;
    }

    public Comparator<MyFileModel> getComparator() {
        return comparator.thenComparing(NAME.comparator);
    }
}
