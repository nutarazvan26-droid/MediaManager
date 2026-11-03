package model.files.mediaFiles;

import java.util.Locale;
import java.util.function.BiPredicate;

public enum FileFilterType {
    TYPE((file, type) -> file.getType().name().equals(type)),
    EXTENSION((file, extension) -> file.getExtension().equalsIgnoreCase(extension)),
    NAME((file, text) -> file.getName().toLowerCase(Locale.ROOT).contains(text.toLowerCase(Locale.ROOT)));

    private BiPredicate<MyFileModel, String> predicate;

    FileFilterType(BiPredicate<MyFileModel, String> predicate) {
        this.predicate = predicate;
    }

    public boolean matches(MyFileModel file, String value) {
        return predicate.test(file, value);
    }
}
